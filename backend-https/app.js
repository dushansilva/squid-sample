const express = require('express');
const app = express();
const bodyParser = require('body-parser');
const morgan = require('morgan');

// routes
const userRoutes = require('./api/routes/user');

app.use(morgan('dev'));
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

//handling cross origin requests
app.use((req, res, next) => {
    res.header('Access-Control-Allow-Origin', '*');
    res.header('Access-Control-Allow-Headers',
        'Origin,X-Requested-With,Content-Type,Accept,Authorization');
    res.header('Access-Control-Allow-Methods', 'PUT,POST,GET,PATCH,DELETE');
    next();
});

//middleware
app.use('/user', userRoutes);

//error handling
app.use((req, res, next) => {
    const error = Error('Not found');
    error.status = 404;
    next(error);
});

app.use((error, req, res, next) => {
    res.status(error.status || 500);
    res.json({
        error: {
            message: error.message
        }
    });
});

module.exports = app;