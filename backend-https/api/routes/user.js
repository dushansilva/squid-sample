const express = require('express');
const router = express.Router();

router.get('/test', (req, res, next) => {
            res.status(200).json({
                message: 'Hello user'
            });
});

module.exports = router;