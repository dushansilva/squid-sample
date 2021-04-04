const https = require('https');
const fs = require('fs');
const app = require('./app');

const privateKey  = fs.readFileSync('./certs/squid-ca-key.pem', 'utf8');
const certificate = fs.readFileSync('./certs/squid-ca-cert.pem', 'utf8');

const credentials = {key: privateKey, cert: certificate};

const port = 3002;

const server = https.createServer(credentials, app);

server.listen(port);