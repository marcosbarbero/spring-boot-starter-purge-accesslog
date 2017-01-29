Purgeable AccessLog for Spring Boot applications
---
Module to enable purge on access log files for spring-boot based applications.  

Adding Project Lombok Agent
---

This project uses [Project Lombok](http://projectlombok.org/features/index.html)
to generate getters and setters etc. Compiling from the command line this
shouldn't cause any problems, but in an IDE you need to add an agent
to the JVM. Full instructions can be found in the Lombok website. The
sign that you need to do this is a lot of compiler errors to do with
missing methods and fields.

Usage		
----		
This project is available on maven central		
		
Add the dependency on pom.xml		
```		
<dependency>		
    <groupId>com.marcosbarbero.boot</groupId>		
    <artifactId>spring-boot-starter-purge-accesslog</artifactId>		
    <version>1.0.0.RELEASE</version>		
</dependency>		
```
   
Sample configuration

```
server:
  accesslog.purge:
    enabled: true #default false
    execute-on-startup: true
    execution-interval: 10
    execution-interval-unit: SECONDS
    max-history: 1
    max-history-unit: MINUTES
```

Contributing
---

Spring Boot Purge AccessLog is released under the non-restrictive Apache 2.0 license, 
and follows a very standard Github development process, using Github tracker for issues 
and merging pull requests into master. If you want to contribute even something trivial 
please do not hesitate, but follow the guidelines below.

###Code of Conduct

This project adheres to the Contributor Covenant [code of conduct](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/blob/master/docs/code-of-conduct.adoc). 
By participating, you are expected to uphold this code. Please report unacceptable behavior to marcos.hgb@gmail.com.

Footnote
---
Any doubt open an [issue](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/issues).  
Any fix send me a [Pull Request](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/pulls).
