# Hero API

## Run
Set these environment variables:
MICRONAUT_ENVIRONMENTS=dev;

Start local database
```bash
docker compose up -d
```

Start application
```bash
./gradlew run
```

[Swagger](http://localhost:8080/swagger-ui/)

## Deployment to Heroku

Login into heroku
```bash
heroku login
heroku container:login
```

Build and deploy container
```bash
./gradlew stage
heroku container:push web
heroku container:release web
```

## Micronaut 3.2.1 Documentation

- [User Guide](https://docs.micronaut.io/3.2.1/guide/index.html)
- [API Reference](https://docs.micronaut.io/3.2.1/api/index.html)
- [Configuration Reference](https://docs.micronaut.io/3.2.1/guide/configurationreference.html)
- [Micronaut Guides](https://guides.micronaut.io/index.html)
---

## Feature security documentation

- [Micronaut Security documentation](https://micronaut-projects.github.io/micronaut-security/latest/guide/index.html)

## Feature jdbc-hikari documentation

- [Micronaut Hikari JDBC Connection Pool documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#jdbc)

## Feature hibernate-jpa documentation

- [Micronaut Hibernate JPA documentation](https://micronaut-projects.github.io/micronaut-sql/latest/guide/index.html#hibernate)

## Feature hibernate-validator documentation

- [Micronaut Hibernate Validator documentation](https://micronaut-projects.github.io/micronaut-hibernate-validator/latest/guide/index.html)

## Feature testcontainers documentation

- [https://www.testcontainers.org/](https://www.testcontainers.org/)

## Feature http-client documentation

- [Micronaut HTTP Client documentation](https://docs.micronaut.io/latest/guide/index.html#httpClient)

