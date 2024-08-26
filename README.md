# Spring Boot oAuth2 and Keycloak project

A Spring Boot demo project that provides authentication and authorization through a Keycloak server.

# Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
  - [Installation](#installation)
  - [Accessing and managing Keycloak](#accessing-and-managing-keycloak)
- [Keycloak Configuration](#keycloak-configuration)
  - [Importing the realm configuration](#importing-the-realm-configuration)
  - [Testing the authentication using a REST client](#testing-the-authentication-using-a-rest-client)

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

The configurations are the following:

- Realm: `sbok-dev`
- Client: `sbok-auth-srv`
- Realm roles: `admin` and `user`
- Users and roles:
  - `john`:
    - Username: `john`
    - Password: `password`
    - Roles: `user`
  - `admin`:
    - Username: `admin`
    - Password: `admin`
    - Roles: `admin`

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

The URL to authenticate users is the `token_endpoint` URL. In this project, it is http://localhost:8080/auth/realms/sbok-dev/protocol/openid-connect/token.

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
