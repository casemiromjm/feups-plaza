# FEUP's Plaza

![Static Badge](https://img.shields.io/badge/JDK-21-gray?labelColor=orange)
![Static Badge](https://img.shields.io/badge/Docker-ollama_%2B_llama3-gray?logo=docker&logoColor=white&labelColor=2496ED)
![Static Badge](https://img.shields.io/badge/SQLite-3.53-gray?logo=sqlite&logoColor=blue&labelColor=white)


## Dependencies
- JDK 21
- Docker for ollama + llama3
- sqlite3 3.53 for storing login data

_NOTE: a justfile is available for easier running, but it is not a dependency of any kind_

## Instructions

### How to compile and run?

Create and populate the database:
```sh
# if you have just available
just database

# if not
rm database/database.db
sqlite3 database/database.db < database/create.sql
sqlite3 database/database.db < database/populate.sql
```

To start the server, run the following command in the project's root folder:
```sh
# if you have just available:
just run -s [-a ADDRESS] [-p PORT]

# if not:
./gradlew run --args='-s [-a ADDRESS] [-p PORT]'
```

OBS<sub>1</sub>: you can check the available CLI arguments by running `./gradlew run --args='-h'` \
OBS<sub>2</sub>: by default the address is localhost (127.0.0.1) and the port is 8080

In another terminal run the client app with the following command:

```sh
# if you have just available:
just run -c [-a ADDRESS] [-p PORT]

# if not:
./gradlew run --args='-c [-a ADDRESS] [-p PORT]'
```

_NOTE: keep in mind that the address and port must be same as the server_

You can use the following credentials for testing our application:
```
username: tester
password: pass
```

### What should you do to have AI embedded in chat?
Ensure you have ollama running to have AI features in chat!

If you are running the client for the first time, run:
```sh
docker compose up -d
```

If you have already run the client once (and did not run `docker compose down`), you should use:
```sh
# to restart ollama
docker compose start

# to stop ollama
docker compose stop
```

If you want to stop ollama and remove the container:
```sh
docker compose down
```
