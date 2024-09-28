### Ghost Drop:

- This is the complete `backend` of an application called ghost drop.
- The current config enables to store the files on the server in the `**uniqueCode**` folder.
- The urls and code are stored in database using the entity `**UrlMapping**`.

### Features:

- `Automatic` resource expiry after 24 hours, which needs to be specified.
- `Cron` cleans the expired resources, and it is destined to run every 6/12 hours.
- Implementing `Patterns` to work with various file handlers.
- `Files` are `encrypted` and `decrypted` during storage to promote transparency.
- `Multithreading` to support file uploads faster and concurrently.

### Future Scope:

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