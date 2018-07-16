Purgeable AccessLog for Spring Boot applications [![Build Status](https://travis-ci.org/marcosbarbero/spring-boot-starter-purge-accesslog.svg?branch=master)](https://travis-ci.org/marcosbarbero/spring-boot-starter-purge-accesslog)
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
```xml		
<dependency>		
    <groupId>com.marcosbarbero.boot</groupId>		
    <artifactId>spring-boot-starter-purge-accesslog</artifactId>		
    <version>1.0.2.RELEASE</version>		
</dependency>		
```
   
Sample configuration

```yaml
server:
  accesslog.purge:
    enabled: true #default false
    execute-on-startup: true #default false
    execution-interval: 10 #default 24
    execution-interval-unit: SECONDS #default HOURS
    max-history: 1 #default 30
    max-history-unit: MINUTES #default DAYS
```

Contributing
---

Spring Boot Purge AccessLog is released under the non-restrictive Apache 2.0 license, 
and follows a very standard Github development process, using Github tracker for issues 
and merging pull requests into master. If you want to contribute even something trivial 
please do not hesitate, but follow the guidelines below.

### Maintainers

The current maintainers (people who can merge pull requests) are:

  * [marcosbarbero](https://github.com/marcosbarbero)
  * [matheusgg](https://github.com/matheusgg)

### Code of Conduct

This project adheres to the Contributor Covenant [code of conduct](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/blob/master/docs/code-of-conduct.adoc). 
By participating, you are expected to uphold this code. Please report unacceptable behavior to marcos.hgb@gmail.com.

Footnote
---
Any doubt open an [issue](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/issues).  
Any fix send me a [Pull Request](https://github.com/marcosbarbero/spring-boot-starter-purge-accesslog/pulls).
