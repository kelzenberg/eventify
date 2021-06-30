# eventify app

This directory contains the frontend of eventify.

## Installation
To install the frontend simply run ```npm ci```.

## Building
To build the project either run ```npm run build-dev``` or ```npm run build-prod``` depending on your target environment. The output can be found in the newly created 'dist' folder.

## Development
During development it is recommended to use ```npm run watch-server``` to automatically rebuild when files have changed and to get a server that will immediately hot reload these changes.  
Please note that the watch script will not necessarily update the files in the 'dist' directory. Always use the building commands if the goal is to get usable and up to date files.

## Tests
To run all tests execute ```npm test```.