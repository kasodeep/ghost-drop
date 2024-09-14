### Build & Deploy:

- Run the command

```shell
./mvnw clean package
```

- Create the Dockerfile and write the initialization steps.

```shell
docker build -t ghost-drop .
docker tag ghost-drop kasodeep/ghost-drop:latest
docker push kasodeep/ghost-drop:latest
```