# Notifications Service

Spring Boot application for scheduling and simulating notifications across channels (e.g., email, push). It uses RabbitMQ for message queuing and Quartz Scheduler for reliable task scheduling. The system adjusts for users' time zones to avoid sending notifications during inappropriate hours, ensures reliable delivery with retries, supports message prioritization, and allows immediate delivery overrides. It also includes a dashboard for monitoring notification statuses and server metrics.

## Table of Contents
- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Setup](#setup)
- [Building and Running the Application](#building-and-running-the-application)
- [Related Repos](#related-repos)
- [Endpoints](#endpoints)
- [License](#license)

## Overview
The Notifications Service is a Spring Boot application that allows for publishing notifications to various channels. It uses a message queue (RabbitMQ) to send messages to subscribed consumers. The service can handle different types of notifications and sends them based on the configured channels.

## Technologies Used
- Java 21
- Spring Boot 3.x
- Maven
- Docker
- RabbitMQ
- Spock
- Prometheus
- Grafana

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
The RabbitMQ Management with dashboards will now be accessible at http://localhost:15672/.

## Related Repos

This project sends messages to RabbitMQ queues, but **does not handle the processing** of those messages directly.

To properly consume messages from the queues (`pushQueue` and `emailQueue`), you must **clone and run** the following listener services:

- [Push Notifications Listener](https://github.com/dminior8/push-notifications-listener)

- [Email Notifications Listener](https://github.com/dminior8/email-notifications-listener)

Make sure both services are up and running to ensure correct message consumption from RabbitMQ.


## Endpoints

| Method   | URL                          | Description                                                                       |
| -------- |------------------------------|-----------------------------------------------------------------------------------|
| `GET`    | `/api/v1/notifications/{id}` | Retrieves a previously scheduled notification by its unique ID.                   |
| `POST`   | `/api/v1/notifications`      | Schedules a new notification for processing through the configured message queue. |
| `POST`   | `/api/v1/notifications/{id}/force-send`     | Forces an immediate send of the notification identified by its ID. |
| `DELETE`   | `/api/v1/notifications/{id}`      | Cancels the scheduled notification with the given ID.                      |
| `GET`   | `/api/v1/metrics` | Retrieves metrics related to notifications, including statuses such as sent, pending, and failed. |


### Examples

#### Sending notification

```
{
  "message": "Your message content",
  "channel": "EMAIL",
  "priority": "LOW",
  "recipient": "anowak",
  "timestamp": "2025-04-05T12:00:00",
  "zoneId": "Europe/Warsaw"
}
```
#### Parameters
- ```message```: The content of the notification.
- ```channel```: The communication channel to use (EMAIL, PUSH).
- ```priority```: The priority of message (HIGH/LOW). High priority notifications should be send first.
- ```recipient```: Username or other identifier of recipient.
- ```timestamp```: The time at which the notification should be sent.
- ```zoneId```: The timezone for the notification.

**Response:**
Status: ```200 OK ``` (on successful notification).

Response Body:
```
Notification scheduled: a9a583f0-da7d-41f6-94b9-2468be63ad70
```
The UUID is the unique identifier for the scheduled notification.

## License
This project is licensed under the [MIT License](https://github.com/dminior8/notifications-service/blob/main/LICENSE) - see the LICENSE file for details.
