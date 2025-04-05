# Notifications Service

A simple service for handling notifications using a publisher-subscriber model with message queues. This service is part of a larger system designed to send notifications to different communication channels.

## Table of Contents
- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Setup](#setup)
- [Building and Running the Application](#building-and-running-the-application)
- [Endpoints](#endpoints)
- [Contributing](#contributing)
- [License](#license)

## Overview
The Notifications Service is a Spring Boot application that allows for publishing notifications to various channels. It uses a message queue (such as RabbitMQ or Kafka) to send messages to subscribed consumers. The service can handle different types of notifications and sends them based on the configured channels.

## Technologies Used
- Java 21
- Spring Boot 3.x
- Maven
- Docker
- RabbitMQ (for messaging)

## Setup

### Prerequisites
- Java 21 (JDK 21)
- Maven 3.8.x or higher
- Docker (for containerization)

### Clone the repository
```bash
git clone https://github.com/dminior8/NotificationsMQ.git
```
```bash
cd notifications-service
```

### Install dependencies
To install the required dependencies, run the following command:

```bash
./mvnw clean install
```
This will download and install all the necessary dependencies for the application.

Sometimes, when this issue occurs, you may need to set the JAVA_HOME environment variable by running in PowerShell command:

```bash
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"
```

## Building and Running the Application
This project includes a Dockerfile to build and run the application within a Docker container. To build and run the container:

### Running with Docker

Make sure Maven Wrapper is available in your repo. Check if the following file exists:
````
.mvn/wrapper/maven-wrapper.jar
````

If it doesn’t exist, generate the wrapper:
```bash
mvn -N io.takari:maven:wrapper
```

This creates:
`.mvn/wrapper/` with required files`


#### Build Docker image
```bash
docker build -t notifications-service .
```

#### Running Docker Compose
```bash
docker-compose up --build
```
The application will now be accessible at http://localhost:8080.
The RabbitMQ Management will now be accessible at http://localhost:15672/.


## Endpoints

| Method   | URL                          | Description                                                                       |
| -------- |------------------------------|-----------------------------------------------------------------------------------|
| `GET`    | `/api/v1/notifications/{id}` | Retrieves a previously scheduled notification by its unique ID.                                |
| `POST`   | `/api/v1/notifications`      | Schedules a new notification for processing through the configured message queue. |


### Examples

#### Sending notification

```
{
  "message": "Your message content",
  "channel": "EMAIL",
  "priority": "LOW",
  "timestamp": "2025-04-05T12:00:00",
  "zoneId": "Europe/Warsaw"
}
```
#### Parameters
- ```message```: The content of the notification.
- ```channel```: The communication channel to use (EMAIL, PUSH).
- ```priority```: The priority of message (HIGH/LOW). High priority notifications should be send first.
- ```timestamp```: The time at which the notification should be sent.
- ```zoneId```: The timezone for the notification.

**Response:**
Status: ```200 OK ``` (on successful notification).

Response Body:
```
{
"message": "Notification scheduled: <UUID>"
}
```
The UUID is the unique identifier for the scheduled notification.

## Licence
This project is licensed under the [MIT License](https://github.com/dminior8/notifications-service/blob/main/LICENSE) - see the LICENSE file for details.
