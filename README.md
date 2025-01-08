<h1 align="center" style="font-weight: bold;">Inventory App API</h1>

<p align="center">This repository houses the backend API for a user-friendly inventory management application. The API prioritizes a well-designed interface with an emphasis on providing valuable resources to users.</p>


### Features

*   **Comprehensive CRUD Operations:** Create, Read, Update, and Delete inventory items and categories through this REST API, ensuring the smooth operation of the application.
*   **Robust Security:** Implemented Spring Security safeguards data integrity and access control with JSON Web Tokens (JWTs) for secure authentication.
*   **Solid Foundation:** Leverages Java, Spring Boot, Spring Data JPA/Hibernate, Flyway, and MySQL for a well-established technical stack.
*   **Testing:** Unit tests powered by JUnit and Mockito guarantee code reliability and quality.
*   **API Documentation:** Swagger integration provides interactive API documentation for easy exploration and understanding.
*   **Dockerization:** Optimized for containerized deployment using Docker.
*   **Caching:** Leverages Redis to improve performance and reduce database load.
*   **Bucket Storage:** To enhance image and file storage and management, Cloudinary was integrated in the application. Cloudinary provides a robust cloud-based platform for storing, managing, and delivering media assets.
*   **Metrics:** To provide insights into the application's health, performance, and usage, Spring Boot Actuator was integrated. This feature offers a set of production-ready features to help monitoring and managing the application.

### Production Environment

To use the API in a production environment, access the following address:

```bash
https://inventory-api-pgb4.onrender.com
```

#### Note
* **Authentication:** To access API resources, you must provide a valid authentication token in the request header. Obtain your authentication token through the <code>/auth</code> endpoint with the following credentials:

```json
{
	"username": "testUser",
	"password": "testUser"
}
```

### Getting Started

### Prerequisites

* Java 21
* Maven (build tool)
* MySql (database)
* Docker (containerization platform)

### Running Locally:

### 1. Clone the Project:

```bash
git clone git@github.com:felipesousac/inventory-server.git
```

### 2. Configure Database Credentials:

Update the <code>application.properties</code> file with your MySQL connection details (host, port, username, password, database name).

### 3. Start the Server:

Inside the project directory, execute:

```bash
./mvnw spring-boot:run
```
### 4. Run the Redis Instance (using Docker):

In the project directory, run:

```bash
docker compose up redis
```

### 5. Access API Documentation:

Once the server starts (usually on port 8080), open:

```
http://localhost:8080/swagger-ui/index.html
```

This URL provides interactive documentation for navigating the API endpoints.

### Client-Side Application:

Instructions on running the client-side application will be provided in a separate repository. It can be [found here](https://github.com/felipesousac/inventory-client)
