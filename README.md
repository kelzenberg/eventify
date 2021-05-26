# ![Logo Eventify](.github/logo.svg)

## Event Planning Simplified

Plan your next event with ease.
Invite your friends, create lists, balance the books & enjoy your event.  
All in one web app.

### Project Repository Structure

- [`/api`](https://github.com/kelzenberg/eventify/tree/master/api) REST API ([Spring Framework](https://spring.io/projects/spring-framework))
- [`/app`](https://github.com/kelzenberg/eventify/tree/master/app) App ([JS React Library](https://reactjs.org/))

### Prerequisites

- Java 14+
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- (optional) a PostgreSQL client, e.g. [Postico](https://eggerapps.at/postico)

### Quick Start

```sh
make setup # declare environment values (required once)

make db # start PostgreSQL database in a Docker container
make app-install # install dependencies for the `/app`
make app-watch # start the frontend app in development mode
```

Then run the ***API*** as described in the [`/api/README.md`](https://github.com/kelzenberg/eventify/blob/master/api/README.md).
