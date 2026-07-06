![SpringLens](.info/banner.png)

# SpringLens

An embedded local log diagnostics dashboard for Spring Boot applications.

> Think of it like an ELK-stack but depoloyed directly into your Spring Boot API, no docker images no containers, just add it to your POM & you're ready to go.

### How does it work?

It connects a logs reader to your Spring Boot api, then append the received logs to a file that is saved in your working/jar directory, & this file is used when you open the dashboard & fetch logs.

If you use Docker containers or delete the old directory whenever you redeploy the jar, the logs file will be deleted, and you will no longer be able to see old logs in the dashboard.

### Features

- Password security, defaults to admin but can be customized
- Start/End Data-time filters
- Log data filters
- Sorting by timestamp
- ERROR logs stackTrace viewer

# Usage

## Installation

```xml
<dependency>
    <groupId>io.github.anassmdi</groupId>
    <artifactId>springlens-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

Here's the list of properties & their default values. 

```properties
spring-lens.enabled=true
spring-lens.path=/spring-lens
spring-lens.password=admin
```

Note: In case you're using a Servlet context-path, say **/api**, the url becomes **/api/your-spring-lens-path**.

## Demo

![Preview](.info/preview.gif)
