# XM Crypto Investment Service

A small service that calculates **summary statistics** for cryptocurrency prices (e.g., oldest/newest, min/max, and normalized range) based on historical rate data.

The project is organized as a multi-module Gradle build (domain/usecase/dao/controller/infrastructure) and is designed to run locally via **Docker Compose** (PostgreSQL + Redis + the service).

---
## What this service does

Given crypto rate records, the service can:

- return **statistics for a single currency** (oldest, newest, min, max, normalized value)
- return **statistics for all currencies** (sorted by “normalized” descending)
- return the **top currency** by normalized value by day

---
## Tech stack (high level)

- Java / Spring Boot (web)
- PostgreSQL (persistence)
- Redis (caching)
- Gradle multi-module build
- Docker + Docker Compose for local environment

---
## Local run (Docker Compose)

The repository includes `docker-compose-local.yml` for a full local setup.

### Prerequisites

- Docker + Docker Compose v2

### Start

docker compose -f docker-compose-local.yml up --

### Stop

docker compose -f docker-compose-local.yml down

After startup the service is expected to be available on:

- `http://localhost:8080`

---
## Configuration

The Docker Compose file passes configuration via environment variables.

Typical variables (names may vary by module/config):

- `APP_DATA_DIR` — path to input data directory (mounted into the container)
- `DB_HOST` — JDBC base host, e.g. `jdbc:postgresql://<db-host>:5432`
- `DB_NAME` — database name
- `DB_USER` / `DB_PASS` — database credentials
- `REDIS_HOST` / `REDIS_PORT` / `REDIS_PASS` — Redis connection settings
- `JAVA_OPTS` — JVM options

> Note: All variables that are needed for local start added in config by default.

---
## Data

A `data/` directory exists in the repo and is mounted into the container as read-only:  
Neded for V2 flyway migration in `db.migration/V2__init_prices.class` in DAO module.
- container: `/data` (by default)

The service expects crypto rate inputs in that directory (for example, CSV files per currency).  
If the exact CSV schema matters for users, add it here (see “What info to provide” below).

---
## API

The service exposes HTTP endpoints to request statistics.

If Swagger/OpenAPI UI is enabled, it is typically available at one of:

- `http://localhost:8080/swagger-ui/index.html`

### Example localhost requests (curl for Postman import)

curl --location '[http://localhost:8080/api/v1/currencies](http://localhost:8080/api/v1/currencies)'

curl --location '[http://localhost:8080/api/v1/stat?currency=BTC](http://localhost:8080/api/v1/stat?currency=BTC)'

curl --location '[http://localhost:8080/api/v1/top?date=2022-01-01T00%3A00%3A00%2B05%3A00](http://localhost:8080/api/v1/top?date=2022-01-01T00%3A00%3A00%2B05%3A00)'

---
## For improvement

- The service is designed to be **easy to extend**.
- When new currencies appear, add them to:
  - `CurrencyType`
  - `CurrencyTypeDto`
- The service logic already supports searching by a **date range**. To enable this end-to-end:
  - update/extend the APIv2 (e.g., accept `dateFrom` and `dateTo` query parameters)
  - pass the correct values into `CryptoSearchRequest` (so the downstream search can use them)
