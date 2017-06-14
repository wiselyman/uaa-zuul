FROM java:8
VOLUME /tmp
RUN mkdir /app
ADD api-gateway-0.0.1-SNAPSHOT.jar /app/app.jar
ADD runboot.sh /app/
RUN bash -c 'touch /app/app.jar'
WORKDIR /app
RUN chmod a+x runboot.sh
EXPOSE 8080
CMD /app/runboot.sh
RUN echo "Asia/Shanghai" > /etc/timezone;