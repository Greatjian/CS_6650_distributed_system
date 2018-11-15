# AWS setup

- EC2

```
sudo su
yum list installed
yum update
```

- environment

```
yum install java-1.8.0
yum remove java-1.7.0-openjdk
java -version
yum install tomcat8 tomcat8-webapps tomcat8-admin-webapps tomcat8-docs-webapp
service tomcat8 start
fuser -v -n tcp 8080
netstat -na | grep 8080
sudo chkconfig --list tomcat8
sudo chkconfig tomcat8 on
```

- tomcat manager

```
cd /usr/share/tomcat8/webapps/manager/META-INF
vi context.xml --> comment <valve>
service tomcat8 restart
cd tomcat8/conf
vi tomcat-users.xml
-->
<role rolename="manager-gui"/>
  <user username="admin" password="admin" roles="manager-gui"/>
service tomcat8 restart
```

- debug

```
cd var/log/tomcat8
vi catalina.2018-....log
```

