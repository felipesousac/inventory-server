<h1 align="center" style="font-weight: bold;">Inventory app (in building)</h1>

<p align="center">An inventory application made simple, focusing on a user friendly interface and providing useful resources to the final user.</p>

## Technologies (until now)

*   Java
*   Spring Boot
*   Spring Data JPA/ Hibernate
*   Flyway
*   Lombok
*   MySql

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

#### Now you can run the client side of the application, [found here](https://github.com/felipesousac/inventory-client)