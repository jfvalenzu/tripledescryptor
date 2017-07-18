# tripledescryptor

[![Developer](https://img.shields.io/badge/Developer-jfvalenzu-red.svg)](https://github.com/jfvalenzu)
[![Download](https://api.bintray.com/packages/nightonke/maven/boommenu/images/download.svg)](https://github.com/jfvalenzu/tripledescryptor/archive/master.zip)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

Simple Java utility for encrypt text strings using Triple DES, giving output as Base64. 

### Requirements

* It requires [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or higger.


# Running

```sh
USAGE: ./tripledes [text_to_encrypt] [keyfile.bg] [vectorfile.bg]

```
#Example

```sh
./tripledes iwanttoencryptthistext key.bg iv.bg
```

### Development

TripleDEScryptor uses [Spring Boot] (https://projects.spring.io/spring-boot/) and TripleDES java library written by David Flanagan.

* For development, use [Maven] v2 or higher and Java 8. This project uses [Spring-Boot](https://projects.spring.io/spring-boot/), so it's
recommended use the [STS] (https://spring.io/tools/sts/) IDE.


#Build
```sh
$ mvn clean compile
$ mvn package
```

#Run
```sh
$ java -Dpass=yourplaintext -jar target/tripledescryptor-0.0.1-SNAPSHOT.jar --keyPath=key.bg --ivPath=iv.bg
```

### Licence
tripledescryptor is released under license GPL Version 3 (https://github.com/jfvalenzu/tripledescryptor/blob/master/LICENSE).

