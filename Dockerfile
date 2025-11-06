FROM openjdk:26-oraclelinux8
ADD target/stranger-strings.jar stranger-strings.jar

ENTRYPOINT ["java", "-jar", "stranger-strings.jar"]