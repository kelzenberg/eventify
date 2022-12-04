# ![Logo Eventify](.github/logo.svg)

## Event Planning Simplified

Plan your next event with ease.
Invite your friends, create lists, balance the books & enjoy your event. All in one web app.
> This is a university project.

### Project Repository Structure

- [`/api`](https://github.com/kelzenberg/eventify/tree/master/api) REST API ([Spring Framework](https://spring.io/projects/spring-framework))
- [`/app`](https://github.com/kelzenberg/eventify/tree/master/app) App ([JS React Library](https://reactjs.org/))

### Prerequisites

- Java 14+
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- (optional) a PostgreSQL client, e.g. [Postico](https://eggerapps.at/postico)
- (optional) allow invalid SSL certificates for resources loaded from localhost.  
  For Chrome, enable this flag [`chrome://flags/#allow-insecure-localhost`](chrome://flags/#allow-insecure-localhost)
- (optional) [Setup Postman](https://learning.postman.com/docs/sending-requests/certificates/) to work with certificates

### Quick Start

```sh
make setup # declare environment values (required once)

make db-run # start PostgreSQL database in a Docker container
make api-run # build and run the Spring API
make app-watch # install frontend app dependencies & start it in development mode with live preview
```

To run the Spring API in IntelliJ IDEA please see the [`/api/README.md`](https://github.com/kelzenberg/eventify/blob/master/api/README.md).
