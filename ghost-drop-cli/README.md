# Ghost Drop CLI

Ghost Drop is a file-handling service that allows users to upload and download files with unique codes, leveraging encryption for secure storage. This CLI tool enables interaction with the Ghost Drop application directly from the terminal.

## Features

- Upload files to the Ghost Drop server.
- Retrieve a unique code for each uploaded file.
- Download files associated with a unique code as a ZIP archive.

## Prerequisites

- [Node.js](https://nodejs.org/en/) installed on your machine.

## Installation

To install the Ghost Drop CLI, clone the repository and install the dependencies:

```bash
npm install cli-ghost-drop
```

```bash
# Commands to interact.
ghostdrop upload <file-path>
ghostdrop download <code>
```
