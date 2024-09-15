### Ghost Drop:

- This is the complete `backend` of an application called ghost drop.
- The current config enables to store the files on the server in the `**uniqueCode**` folder.
- The urls and code are stored in database using the entity `**UrlMapping**`.

### Features:

- `Automatic` resource expiry after 24 hours, which needs to be specified.
- `Cron` cleans the expired resources, and it is destined to run every 6/12 hours.
- Implementing `Patterns` to work with various file handlers.

### Future Scope:

- Perform the `encryption` and `decryption` of the files.
- Maybe implement `AWS` if you have an account.

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