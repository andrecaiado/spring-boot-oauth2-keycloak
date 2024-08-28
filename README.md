# Spring Boot oAuth2 and Keycloak project

A Spring Boot demo project that provides authentication and authorization through a Keycloak server.

# Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [About the `.env` file](#about-the-env-file)
  - [Accessing and managing Keycloak](#accessing-and-managing-keycloak)
- [Keycloak Configuration](#keycloak-configuration)
  - [Importing the realm configuration](#importing-the-realm-configuration)
  - [Testing the authentication using a REST client](#testing-the-authentication-using-a-rest-client)
- [Implementation](#implementation)
  - [Security configuration](#security-configuration)
  - [Role based authorization](#role-based-authorization)
    - [Enable method security](#enable-method-security)
    - [Extract roles from the JWT token](#extract-roles-from-the-jwt-token)
  - [Endpoints](#endpoints)

# Features
- Configures and runs a Keycloak server in a Docker container
- Configures a Spring Boot application to use the Keycloak server for authentication and authorization
- Provides a REST API to authenticate and authorize users
- Provides a REST API to access a protected resource
- Provides a REST API to refresh the access token

# Requirements

- Java 17
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
./mvnw clean install
```

4. Run the application by executing the following command:

```shell 
./mvnw spring-boot:run
```

## About the `.env` file
> The best practices recommend not to store sensitive information in the source code or other project files, e.g., the `.env` file.
>
> One usually follows the following steps to secure sensitive information contained in the `.env` file:
>
> - Add the `.env` file to the `.gitignore` file to prevent it from being committed to the repository.
>
> - Provide a `.env` template file with empty values and instructions on how to fill it.
>
> For the sake of simplicity of this example project and because some of the sensitive data is provided in the [sbok-dev-realm.json](local-dev%2Fsbok-dev-realm.json), the above steps were not followed.
> 
> Of course, if you are going to use this project in a production environment, please follow the best practices and change the sensitive data accordingly.

## Accessing and managing Keycloak

When running the application, Keycloak will be available at `http://localhost:8080/auth`. The default credentials are:

- Username: `admin`
- Password: `password`

For more information on configuring Keycloak server, please refer to the [Keycloak Getting started guides](https://www.keycloak.org/guides).

# Keycloak Configuration

This section describes the Keycloak configuration used in the project.

The configuration was first created manually in the Keycloak server and then exported to a JSON files that is loaded when the Keycloak server starts.

The exported file is the following: [sbok-dev-realm.json](local-dev/sbok-dev-realm.json)

**Note**: 

It's possible to export realm settings through the Keycloak admin console however, the users are not included in the exported file.

To export all settings, including the users, the following command was executed inside the Keycloak server container:

```shell
/opt/keycloak/bin/kc.sh export --dir /opt/keycloak/data/import --users realm_file --realm sbok-dev
```

The configurations contained in the exported file are:

- Realm: `sbok-dev`
- Client: `sbok-auth-srv`
- Realm roles: `admin` and `user`
- Users and roles:
  - `john`:
    - Username: `john`
    - Password: `password`
    - Realm roles assigned: `user`
  - `admin`:
    - Username: `admin`
    - Password: `admin`
    - Realm roles assigned: `admin`

**Solve the issue that impacts the userinfo endpoint**

To solve the issue that impacts the userinfo endpoint (returning http status code 403), the following steps were taken:

1. Access the Keycloak admin console
2. Switch to the `sbok-dev` realm
3. Click on `Clients scopes` in left menu
4. Create a client scope with the following settings:
   - Name: `openid`
   - Assign type: `Default`
   - Protocol: `OpenID Connect`
5. Click on `Clients` in left menu and select the `sbok-auth-srv` client in the list
6. Navigate to the `Client Scopes` tab
7. Add the `openid` client scope to the assigned scopes

The above steps were taken based on the following discussion:
- https://keycloak.discourse.group/t/issue-on-userinfo-endpoint-at-keycloak-20/18461

These steps are also included in the exported Keycloak configuration file.

## Importing the realm configuration

To automatically import the realm configuration when the Keycloak server starts, the following configuration was added to the [docker-compose.yaml](local-dev/docker-compose.yaml) file:

```yaml
  keycloak:
    ...
    command: "-v start-dev --import-realm"
    ...
    volumes:
      - ./sbok-dev-realm.json:/opt/keycloak/data/import/sbok-dev-realm.json
```

## Testing the authentication using a REST client

Access the [OpenID Endpoint Configuration URL](http://localhost:8080/realms/sbok-dev/.well-known/openid-configuration) to get the necessary URLs to authenticate and authorize users.

The above URL can be found at the [Realm settings](http://localhost:8080/admin/master/console/#/sbok-dev/realm-settings) page of the Keycloak server.

The URL to authenticate users is the `token_endpoint` URL. In this project, it is http://localhost:8080/realms/sbok-dev/protocol/openid-connect/token.

Create a POST request to the `token_endpoint` URL with the following parameters:

- Body type: `x-www-form-urlencoded`
- Key-value pairs:
  - grant_type: `password`
  - client_id: `sbok-auth-srv`
  - username: `john`
  - password: `password`
  - client_secret: `p201PFVJpK5a5jRkkKcwzYZvO7Yc0lUT`

The `client_secret` can be found at the client details page, in the credentials tab, of the Keycloak server.

If the request is successful, the response will contain, among other fields, the `access_token`.

# Implementation

This section describes the implementation of the project focused on the integration with Keycloak.

## Security configuration

The security configuration is defined in the [SecurityConfig](src/main/java/com/andrecaiado/springboot/oauth2/keycloak/config/SecurityConfig.java) class.

In the filter chain, we added the `oauth2ResourceServer` so Spring Boot knows what resource server to use in order to validate the JWT token that will be received in the requests.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    ...
    
    http
            .oauth2ResourceServer(oauth2ResourceServer ->
                oauth2ResourceServer
                    .jwt(Customizer.withDefaults())
            );
    ...
    
    return http.build();
  }
}
```

The resource server properties are defined in the [application.yml](src/main/resources/application.yml) file:

```yaml
spring:
  security:
  oauth2:
    resourceserver:
      jwt:
        issuer-uri: ${KEYCLOAK_URL}/realms/sbok-dev
        jwk-set-uri: ${spring.security.oauth2.resourceserver.jwt.issuer-uri}/protocol/openid-connect/certs
```

Some properties are loaded from the .env file.

Once again, please refer to the [OpenID Endpoint Configuration URL](http://localhost:8080/realms/sbok-dev/.well-known/openid-configuration) to get the necessary URIs.

## Role based authorization

This section describes how to implement role-based authorization in the project.

### Enable method security

Enable the `@PreAutorize` annotation by adding the `@WebMethodSecurity` annotation in the [SecurityConfig](src/main/java/com/andrecaiado/springboot/oauth2/keycloak/config/SecurityConfig.java) class:

```java
@Configuration
@EnableWebSecurity
@EnableWebMethodSecurity
public class SecurityConfig {
  ...
}
```

Add the `@PreAuthorize` annotation to the methods that need authorization:

```java
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/info")
  @PreAuthorize("hasRole('user')")
  public ResponseEntity<JsonNode> getUserInfo(@RequestHeader("Authorization") String bearerToken) {
    bearerToken = bearerToken.replace("Bearer ", "");
    return ResponseEntity.ok(userService.getUserInfo(bearerToken));
  }
}
```

### Extract roles from the JWT token

We need to implement a custom `JwtAuthenticationConverter` to extract the roles from the JWT token so the @PreAuthorize annotation can work properly.

The roles are extracted from the `realm_access` and `resource_access` claims of the JWT token.

The `JwtAuthenticationConverter` is defined in the [JwtAuthConverter.java](src%2Fmain%2Fjava%2Fcom%2Fexample%2Fspringboottemplate%2Fsecurity%2FJwtAuthConverter.java) class.

## Endpoints

The project provides the following endpoints:

- `/api/v1/auth/login`: Authenticates a user and returns the access token
- `/api/v1/auth/refresh`: Refreshes the access token
- `/api/v1/user/info`: Returns the user information (users with role `user`)
- `/api/v1/admin/info`: Returns a protected resource (users with role `admin`)

A [postman collection](local-dev/spring-boot-oauth2-keycloak.postman_collection.json) is provided to test the endpoints.
