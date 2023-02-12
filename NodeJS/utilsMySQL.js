var mysql   = require('mysql2')

class Obj {

    // Inicia la connexió amb la base de dades
    init (parameters) {
        this.pool  = mysql.createPool({
            connectionLimit : 10,
            host        : parameters.host,
            port        : parameters.port,
            user        : parameters.user,
            password    : parameters.password,
            database    : parameters.database
        })

        this.pool.on('connection', (connection) => { connection.query('SET @@session.group_concat_max_len = @@global.max_allowed_packet') })
        console.log('MySQL connected with destination: ' + this.db)
    }

    // Tanca la connexió amb la base de dades
    end () {
        this.pool.end()
    }

    // Fer una consulta a la base de dades
    callbackQuery (queryStr, callback) {
        this.pool.getConnection((err, connection) => {
            if (err) {
                return callback(err)
            } else {
                return connection.query(queryStr, (err, rst) => {
                    connection.release()
                    return callback(err, rst)
                })
            }
        })
    }

    // Fer una consulta a la base de dades amb 'promises'
    query (queryStr) {
        return new Promise((resolve, reject) => {
            return this.callbackQuery(queryStr, (err, rst) => { if (err)  { return reject(err) } else { return resolve(rst) } })
        })
    }
}

// Export
module.exports = Obj
