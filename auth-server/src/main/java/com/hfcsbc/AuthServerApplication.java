package com.hfcsbc;

import com.hfcsbc.domain.SysAuthority;
import com.hfcsbc.domain.SysRole;
import com.hfcsbc.domain.SysUser;
import com.hfcsbc.repository.SysAuthotityRepository;
import com.hfcsbc.repository.SysRoleRepository;
import com.hfcsbc.repository.SysUserRepository;
import com.hfcsbc.repository.support.WiselyRepositoryImpl;
import com.hfcsbc.security.SecurityUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableJpaRepositories(repositoryBaseClass = WiselyRepositoryImpl.class)
public class AuthServerApplication {

	@Bean(name = "auditorAware")
	public AuditorAware<String> auditorAware() {
		return ()-> SecurityUtils.getCurrentUserUsername();
	}

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

//	@Bean
//	CommandLineRunner userInitclr(PasswordEncoder passwordEncoder,
//								  SysUserRepository userRepository,
//								  SysRoleRepository roleRepository,
//								  SysAuthotityRepository authotityRepository){
//		return new CommandLineRunner() {
//			@Override
//			public void run(String... strings) throws Exception {
//				SysAuthority authority = initAuthority(authotityRepository);
//				initUsersAndRoles(passwordEncoder,roleRepository,userRepository,authority);
//			}
//		};
//	}

	private SysAuthority initAuthority(SysAuthotityRepository authotityRepository){
		SysAuthority authority = new SysAuthority();
		authority.setName("查看demo");
		authority.setValue("query-demo");
		authority.setCreatedBy("wyf");
		return authotityRepository.save(authority);
	}
	private void initUsersAndRoles(PasswordEncoder passwordEncoder,
			                       SysRoleRepository roleRepository,
								   SysUserRepository userRepository,
								   SysAuthority authority){
		SysRole roleAdmin = new SysRole();
		roleAdmin.setName("管理员");
		roleAdmin.setValue("ROLE_ADMIN");
		Set<SysAuthority> authorities = new HashSet<>();
		authorities.add(authority);
		roleAdmin.setAuthorities(authorities);
		roleAdmin.setCreatedBy("wyf");
		roleAdmin = roleRepository.save(roleAdmin);

		SysRole roleUser = new SysRole();
		roleUser.setName("普通用户");
		roleUser.setValue("ROLE_USER");
		roleUser.setCreatedBy("wyf");
		roleUser = roleRepository.save(roleUser);

		SysUser userAdmin = new SysUser();
		userAdmin.setUsername("admin");
		userAdmin.setPassword(passwordEncoder.encode("admin"));
		Set<SysRole> rolesForAdmin = new HashSet<>();
		rolesForAdmin.add(roleAdmin);
		userAdmin.setRoles(rolesForAdmin);
		userAdmin.setCreatedBy("wyf");
		userRepository.save(userAdmin);

		SysUser userNormal = new SysUser();
		userNormal.setUsername("wyf");
		userNormal.setPassword(passwordEncoder.encode("wyf"));
		userNormal.setCreatedBy("wyf");
		Set<SysRole> rolesForNormal = new HashSet<>();
		rolesForNormal.add(roleUser);
		userNormal.setRoles(rolesForNormal);
		userRepository.save(userNormal);

	}
}
