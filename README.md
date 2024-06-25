
## Stock Trading System

This repository contains a distributed system for a Stock Trading System. The system is composed of multiple microservices: Data Management, Business Logic Management, and User Management Advertisement, StockAndNews, Eureka. Each microservice is responsible for a specific aspect of the stock trading system and communicates with each other via RESTful APIs.

## Overview

The Stock Trading System allows users to buy and sell stocks through a user-friendly interface. It provides functionalities such as user authentication, stock data management, and transaction processing.

### Services

1. **Data Management Service**
   - Responsible for managing all persistant data in a database and exposing CRUD operations to interact with the databse. Exposes News and Stock data to other services.
   
2. **Business Logic Management Service**
   - Implements the core business logic of the stock trading system, including processing buy and sell orders, calculating portfolio values, and generating transaction reports.
   
3. **User Management Service**
   - Manages user accounts, authentication, and user-related operations such as setting balance, retrieving transaction history, and managing user profiles.

4. **Stock and News Service**
   - Emulates real world stock and news service APIs, accessed via the Data Service.

5. **Advertisement Service**
   - Serves advertisements to the user when the user isn't logged in.

6. **Eureka Service**
   - Allows for service discovery and registration as service come online and shut down. Used to enhance fault tolerance and scalability.

## Technologies Used

- **Java Spring Boot**: Microservices are built using Spring Boot, providing a robust and scalable framework for building RESTful APIs.
- **RESTful APIs**: Communication between microservices is done via RESTful APIs, allowing for interoperability and loose coupling.
- **Docker**: Microservices are containerized using Docker, enabling easy deployment and scalability.
- **Eureka**: Service Registration and discovery.
- **Swagger**: Documentation of the APIs for ease of access / development.
- **OkHTTP**: Used by the client to communicate with the system.

## How to run
**Package The Application (from root of folder):**

```mvn clean package```

**Start the System (this can take some time):**

```docker compose up```

**Start the Client (In seperate terminal, if possible, try wait for the Services to finish starting, before starting client):**

```mvn install -pl core```

```mvn compile exec:java -pl client```

**URLs:**

Eureka Server: ```http://localhost:8761/```

User Service Swagger: ```http://localhost:8080/swagger-ui/#/user-controller```

Video: https://gitlab.com/ucd-cs-rem/comp30220-2024/pleasegiveusafirst/-/blob/main/Video.mp4

Report: https://gitlab.com/ucd-cs-rem/comp30220-2024/pleasegiveusafirst/-/blob/main/Report.pdf?ref_type=heads
