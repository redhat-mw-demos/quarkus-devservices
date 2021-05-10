# Quarkus DevServices

This project showcases the new [DevServices capabilities introduced in Quarkus 1.13](https://quarkus.io/blog/quarkus-1-13-0-final-released/#zero-config-setup-with-devservices).

One main goal of Quarkus is to make developer lives easier and bring back developer joy. When starting an application in dev mode, Quarkus is now able to automatically start database containers. Quarkus will automatically pick a database container image for you based on the database extensions selected in the project.

This example uses RESTEasy Reactive for the JAX-RS layer and Hibernate Reactive with Panache with the Reactive PostgreSQL client. The DevServices would work exactly the same if using the non-reactive versions.

## Anatomy of the application
- [`pom.xml`](pom.xml)
    - The database driver is the only important piece of information.
    
       ```xml
      <dependency>
          <groupId>io.quarkus</groupId>
          <artifactId>quarkus-reactive-pg-client</artifactId>
      </dependency>
       ```
- [`Fruit`](src/main/java/com/redhat/domain/Fruit.java)
    - JPA entity class
- [`FruitRepository`](src/main/java/com/redhat/repository/FruitRepository.java)
    - [Panache repository class](https://quarkus.io/guides/hibernate-orm-panache#solution-2-using-the-repository-pattern) for interacting with the data source
- [`FruitResource`](src/main/java/com/redhat/rest/FruitResource.java)
    - Resource class exposing REST endpoints
        - HTTP `GET` to `/fruits` returns all fruits
        - HTTP `GET` to `/fruits/{name}` returns
            - The fruit for the given `{name}` if found
            - HTTP status `404` if the fruit for the given `{name}` is not found
        - HTTP `POST` to `/fruits` with valid JSON representing a fruit returns the fully-populated fruit
        - HTTP `POST` to `/fruits` with invalid JSON representing a fruit returns HTTP status `400`
- [`application.yml`](src/main/resources/application.yml)
    - Application configuration
       > Notice there is nothing related to any container images or dev services

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw clean compile quarkus:dev
```

If a valid container runtime is found you should see something similar to the below in the output **BEFORE** the application is started:

```shell
INFO  [org.tes.DockerClientFactory] (build-48) Docker host IP address is localhost
INFO  [org.tes.DockerClientFactory] (build-48) Connected to docker: 
  Server Version: 20.10.6
  API Version: 1.41
  Operating System: Docker Desktop
  Total Memory: 3935 MB
INFO  [🐳 .2]] (build-47) Creating container for image: postgres:13.2
INFO  [🐳 .2]] (build-47) Starting container with ID: 4d628e27adaf2fbd9fa8c72e4230b32f6160c15436413dc44939ca2b76a20e0e
INFO  [🐳 .2]] (build-47) Container postgres:13.2 is starting: 4d628e27adaf2fbd9fa8c72e4230b32f6160c15436413dc44939ca2b76a20e0e
INFO  [🐳 .2]] (build-47) Container postgres:13.2 started in PT4.722896S
```

Quarkus DevServices automatically chose the PostgreSQL 13.2 container image based on the Postgres extension found.

   > The image can also be customized using the `quarkus.datasource.devservices.image-name` property (i.e. `quarkus.datasource.devservices.image-name=postgres:10`)

Once the application is running you can then hit the various REST endpoints:

### Example endpoints
```shell
$ curl -i http://localhost:8080/fruits
HTTP/1.1 200 OK
content-length: 2
Content-Type: application/json

[]
```

```shell
$ curl -i -X POST http://localhost:8080/fruits -H "Content-Type:application/json" -d '{"name":"apple","description":"Yummy fruit"}'
HTTP/1.1 200 OK
content-length: 51
Content-Type: application/json

{"id":1,"name":"apple","description":"Yummy fruit"}
```

```shell
$ curl -i http://localhost:8080/fruits/apple
HTTP/1.1 200 OK
Content-Type: application/json
content-length: 51

{"id":1,"name":"apple","description":"Yummy fruit"}
```

```shell
$ curl -i http://localhost:8080/fruits/pear
HTTP/1.1 404 Not Found
Content-Type: application/json
content-length: 0
```
