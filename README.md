# Spring Cloud下基于OAUTH2认证授权的实现

在`Spring Cloud`需要使用`OAUTH2`来实现多个微服务的统一认证授权，通过向`OAUTH服务`发送某个类型的`grant type`进行集中认证和授权，从而获得`access_token`，而这个token是受其他微服务信任的，我们在后续的访问可以通过`access_token`来进行，从而实现了微服务的统一认证授权。

本示例提供了四大部分：

- `discovery-service`:服务注册和发现的基本模块
- `auth-server`:OAUTH2认证授权中心
- `order-service`:普通微服务，用来验证认证和授权
- `api-gateway`:边界网关(所有微服务都在它之后)

OAUTH2中的角色：

- `Resource Server`:被授权访问的资源
- `Authotization Server`：OAUTH2认证授权中心
- `Resource Owner`： 用户
- `Client`：使用API的客户端(如Android 、IOS、web app)

Grant Type：

- `Authorization Code`:用在服务端应用之间
- `Implicit`:用在移动app或者web app(这些app是在用户的设备上的，如在手机上调起微信来进行认证授权)
- `Resource Owner Password Credentials(password)`:应用直接都是受信任的(都是由一家公司开发的，本例子使用)
- `Client Credentials`:用在应用API访问。

### 1.基础环境

使用`Postgres`作为账户存储，`Redis`作为`Token`存储，使用`docker-compose`在服务器上启动`Postgres`和`Redis`。

```Yaml
Redis:
  image: sameersbn/redis:latest
  ports:
    - "6379:6379"
  volumes:
    - /srv/docker/redis:/var/lib/redis:Z
  restart: always

PostgreSQL:
  restart: always
  image: sameersbn/postgresql:9.6-2
  ports:
    - "5432:5432"
  environment:
    - DEBUG=false

    - DB_USER=wang
    - DB_PASS=yunfei
    - DB_NAME=order
  volumes:
    - /srv/docker/postgresql:/var/lib/postgresql:Z
```



### 2.auth-server

####  2.1 OAuth2服务配置

`Redis`用来存储`token`，服务重启后，无需重新获取`token`.

```java
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisConnectionFactory connectionFactory;


    @Bean
    public RedisTokenStore tokenStore() {
        return new RedisTokenStore(connectionFactory);
    }


    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)//若无，refresh_token会有UserDetailsService is required错误
                .tokenStore(tokenStore());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("android")
                .scopes("xx") //此处的scopes是无用的，可以随意设置
                .secret("android")
                .authorizedGrantTypes("password", "authorization_code", "refresh_token")
            .and()
                .withClient("webapp")
                .scopes("xx")
                .authorizedGrantTypes("implicit");
    }
}


```



#### 2.2 Resource服务配置

`auth-server`提供user信息，所以`auth-server`也是一个`Resource Server`

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
                .authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .httpBasic();
    }
}

```



```java
@RestController
public class UserController {

    @GetMapping("/user")
    public Principal user(Principal user){
        return user;
    }
}

```



#### 2.3 安全配置

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {



    @Bean
    public UserDetailsService userDetailsService(){
        return new DomainUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    //不定义没有password grant_type
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    


}
```

#### 2.4 权限设计

采用`用户(SysUser)`<->`角色(SysRole)`<->`权限(SysAuthotity)`设置，彼此之间的关系是`多对多`。通过`DomainUserDetailsService` 加载用户和权限。



#### 2.5 配置

```Yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  application:
      name: auth-server

  jpa:
    open-in-view: true
    database: POSTGRESQL
    show-sql: true
    hibernate:
      ddl-auto: update
  datasource:
    platform: postgres
    url: jdbc:postgresql://192.168.1.140:5432/auth
    username: wang
    password: yunfei
    driver-class-name: org.postgresql.Driver
  redis:
    host: 192.168.1.140

server:
  port: 9999


eureka:
  client:
    serviceUrl:
      defaultZone: http://${eureka.host:localhost}:${eureka.port:8761}/eureka/



logging.level.org.springframework.security: DEBUG

logging.leve.org.springframework: DEBUG

##很重要
security:
  oauth2:
    resource:
      filter-order: 3
```

#### 2.6 测试数据

`data.sql`里初始化了两个用户`admin`->`ROLE_ADMIN`->`query_demo`,`wyf`->`ROLE_USER`



### 3.order-service



#### 3.1 Resource服务配置

```java
@Configuration
@EnableResourceServer
public class ResourceServerConfig  extends ResourceServerConfigurerAdapter{

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
                .authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .httpBasic();
    }
}

```



#### 3.2 用户信息配置

`order-service`是一个简单的微服务，使用`auth-server`进行认证授权，在它的配置文件指定用户信息在`auth-server`的地址即可：

```yaml
security:
  oauth2:
    resource:
      id: order-service
      user-info-uri: http://localhost:8080/uaa/user
      prefer-token-info: false
```

#### 3.3 权限测试控制器

具备`authority`未`query-demo`的才能访问，即为`admin`用户

```java
@RestController
public class DemoController {
    @GetMapping("/demo")
    @PreAuthorize("hasAuthority('query-demo')")
    public String getDemo(){
        return "good";
    }
}

```



### 4 api-gateway



`api-gateway`在本例中有2个作用：

- 本身作为一个client，使用`implicit`


- 作为外部app访问的方向代理









#### 4.1 关闭csrf并开启Oauth2 client支持

```Java
@Configuration
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

    }

}
```



#### 4.2 配置

```java
zuul:
  routes:
    uaa:
      path: /uaa/**
      sensitiveHeaders:
      serviceId: auth-server
    order:
      path: /order/**
      sensitiveHeaders:
      serviceId: order-service
  add-proxy-headers: true

security:
  oauth2:
    client:
      access-token-uri: http://localhost:8080/uaa/oauth/token
      user-authorization-uri: http://localhost:8080/uaa/oauth/authorize
      client-id: webapp
    resource:
      user-info-uri: http://localhost:8080/uaa/user
      prefer-token-info: false
```



### 5 演示

#### 5.1 客户端调用

使用`Postman`向`http://localhost:8080/uaa/oauth/token`发送请求获得`access_token`(admin用户的如`7f9b54d4-fd25-4a2c-a848-ddf8f119230b`)

- admin用户

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/post-admin.png)

  -------------

  ​

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/user-admin.png)

  -----------

  ​

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/demo-admin.png)

  -------

  ​

  ​

- wyf用户

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/post-wyf.png)

  ----

  ​

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/user-wyf.png)

  ----

  ​

  ![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/demo-wyf.png)



#### 5.2 api-gateway中的webapp调用

暂时没有做测试，下次补充。

### 6 注销oauth2
#### 6.1 增加自定义注销`Endpoint`
所谓注销只需将`access_token`和`refresh_token`失效即可，我们模仿`org.springframework.security.oauth2.provider.endpoint.TokenEndpoint`写一个使`access_token`和`refresh_token`失效的`Endpoint`:

```
@FrameworkEndpoint
public class RevokeTokenEndpoint {

    @Autowired
    @Qualifier("consumerTokenServices")
    ConsumerTokenServices consumerTokenServices;

    @RequestMapping(method = RequestMethod.DELETE, value = "/oauth/token")
    @ResponseBody
    public String revokeToken(String access_token) {
        if (consumerTokenServices.revokeToken(access_token)){
            return "注销成功";
        }else{
            return "注销失败";
        }
    }
}
```

#### 6.2 注销请求方式
![](https://raw.githubusercontent.com/wiselyman/uaa-zuul/master/images/logout.png)
