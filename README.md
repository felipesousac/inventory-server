<h1 align="center" style="font-weight: bold;">Inventory app API (in building)</h1>

<p align="center">An inventory application made simple, focusing on a user-friendly interface and providing valuable resources to the end user.</p>
<p align="center">This REST API provides all CRUD operations necessary to maintain the app running as expected and securely. Its structure was designed considering best practices in all project aspects, and unit tests are in place to ensure everything functions properly.</p>


## Tech Stack (until now)

*   Java
*   Spring Boot
*   Spring Data JPA/ Hibernate
*   Flyway
*   MySql
*   Spring Security
*   JSON Web Tokens
*   JUnit/ Mockito
*   Swagger

## Running locally

### Prerequisites

* Java 17
* Maven
* MySql

### Clone project

```bash
git clone git@github.com:felipesousac/inventory-server.git
```

### Configure database credentials

Inside <code>application.properties</code> file, configure MySql credentials to connect with your local database.

### Start project

Inside back-end project folder, run:

```bash
./mvnw spring-boot:run
```

## Docs

After running locally, the documentation can be found on:

```
http://localhost:8080/swagger-ui/index.html
```

#### Now you can run the client side of the application, [found here](https://github.com/felipesousac/inventory-client)
