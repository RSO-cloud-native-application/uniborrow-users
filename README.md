# Uniborrow - Users service

## Description

The service handles user interaction. It currently supports listing all users.

## Structure

The service is built with KumuluzEE. It consists of 4 modules:

* `api`: Defines the service endpoints.
* `services`: Business logic for the service.
* `models`: Defines entities and connection to the database.
* `lib`: Definition of data the service uses.

## Prerequisites

To set the database:

```
docker run --name uniborrow-users-db \
    -e POSTGRES_DB=users \
    -e POSTGRES_PASSWORD=postgres \ 
    -e POSTGRES_USER=dbuser \ 
    -p 5432:5432 \
    postgres:13
```