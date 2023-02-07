# External system aka TPS

## Docker compose

To run TPS + Postgres run in microservices infra:
```shell
cp docker-compose.override.{example,}.yml 
docker-compose up -d
```

To run specific version use environment variable `TPS_VERSION`:
```shell
TPS_VERSION=0.0.6-SNAPSHOT docker compose up -d
```
