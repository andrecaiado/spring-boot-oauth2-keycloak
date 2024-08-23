# Spring Boot oAuth2 and Keycloak project

A Spring Boot demo project that provides authentication and authorization through a Keycloak server.

# Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [Accessing and managing Keycloak](#accessing-and-managing-keycloak)

# Features
- Installs and configures a Keycloak server
- Configures a Spring Boot application to use the Keycloak server for authentication and authorization
- Provides a REST API to authenticate and authorize users
- Provides a REST API to access a protected resource
- Provides a REST API to refresh the access token

# Requirements

- Java 21
- Maven
- Docker
- Docker Compose

# Getting Started

This section provides a step-by-step guide on how to run the project.

## Installation

1. Clone the repository by executing the following command:

```shell
git clone git@github.com:andrecaiado/spring-boot-oauth2-keycloak.git
```

2. Navigate into the project directory:

```
cd your-repository-name
```

3. Install the dependencies by executing the following command:

```shell
mvn clean install
```

4. Run the application by executing the following command:

```shell 
mvn spring-boot:run
```

## Accessing and managing Keycloak

When running the application, Keycloak will be available at `http://localhost:8080/auth`. The default credentials are:

- Username: `admin`
- Password: `admin`

For more information on configuring Keycloak server, please refer to the [Keycloak Getting started guides](https://www.keycloak.org/guides).

