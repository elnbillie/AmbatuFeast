const { Pool } = require('pg')

const pool = new Pool({
    user: 'postgres',
    host: 'localhost',
    database: 'ambatufeast',
    password: 'qwerty',
    port: 5432
});


pool.connect()
    .then(() => console.log('Connected to postgres'))
    .catch(err => {
        console.log('Database connection failed! Bad config:', err);
        process.exit(-1);
    });

module.exports = { pool };
