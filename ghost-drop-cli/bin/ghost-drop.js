#!/usr/bin/env node
require('dotenv').config();

// To execute the cli commands.
const { Command } = require('commander');

// To upload and download files.
const FormData = require('form-data');
const axios = require('axios');

// To read and write files.
const fs = require('fs');
const path = require('path');

const program = new Command();
const ENDPOINT_URL = "https://ghost-drop.onrender.com/api/v1/ghost-drop/anonymous";

// CLI Metadata
program
   .name('ghostdrop')
   .description('CLI for Ghost-Drop application!')
   .version('1.1.0');

// Upload Command.
program
   .command('upload <file>')
   .description('Upload a file using Ghost-Drop and retrieve unique code')
   .action(async (file) => {
      console.log(`Uploading file: ${file}...`);

      const formData = new FormData();
      formData.append('files', fs.createReadStream(file));

      try {
         const response = await axios.post(ENDPOINT_URL, formData, {
            headers: {
               ...formData.getHeaders(),
            },
         });

         const { uniqueCode } = response.data;
         console.log('File uploaded successfully!');
         console.log(`Unique code for download: ${uniqueCode}`);

      } catch (error) {
         console.error('Error uploading file:', error.message);
      }
   });

// Download Command.
program
   .command('download <code>')
   .description('Download files associated with a code as a ZIP')
   .action(async (code) => {
      console.log(`Downloading files for code: ${code}...`);

      try {
         const response = await axios({
            url: `${ENDPOINT_URL}?code=${code}`,
            method: 'GET',
            responseType: 'stream',
         });

         const outputPath = path.resolve(__dirname, `${code}.zip`);
         const writer = fs.createWriteStream(outputPath);

         response.data.pipe(writer);

         writer.on('finish', () => {
            console.log(`Downloaded zip file: ${outputPath}`);
         });

         writer.on('error', (error) => {
            console.error('Error downloading file:', error.message);
         });

      } catch (error) {
         console.error('Error downloading zip:', error.message);
      }
   });

// Parse CLI arguments.
program.parse(process.argv);