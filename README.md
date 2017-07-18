# tripledescryptor
Simple Java utility for encrypt text strings using Triple DES, giving output on Base64. 

# Requirements

* [Maven]  or higher
* It requires [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or higger


# Running
USAGE: ./tripledes [text_to_encrypt] [keyfile.bg] [vectorfile.bg]

### Development

TripleDEScryptor uses [Spring Boot] (https://projects.spring.io/spring-boot/) and TripleDES java library written by David Flanagan.

For compile execute: 
```sh
$ mvn clean compile
$ mvn package
$ java -Dpass=yourplaintext -jar target/tripledescryptor-0.0.1-SNAPSHOT.jar --keyPath=key.bg --ivPath=iv.bg
```
### Licence
tripledescryptor is released under license GPL Version 3 (https://github.com/jfvalenzu/tripledescryptor/blob/master/LICENSE).

