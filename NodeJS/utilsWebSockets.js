// Description: WebSocket server for the app

const WebSocket = require('ws')
const { v4: uuidv4 } = require('uuid')

class Obj {

    init (httpServer, port) {      

        // Define empty callbacks
        this.onConnection = (socket, id) => {}
        this.onMessage = (socket, id, obj) => { }
        this.onClose = (socket, id) => { }

        // Run WebSocket server
        this.wss = new WebSocket.Server({ server: httpServer })
        this.socketsClients = new Map()
        console.log(`Listening for WebSocket queries on ${port}`)

        // What to do when a websocket client connects
        this.wss.on('connection', (ws) => { this.newConnection(ws) })
    }

    end () {
        this.wss.close()
    }

    // A websocket client connects
    newConnection (ws) {

        console.log("Client connected")

        // Add client to the clients list
        const id = uuidv4()
        const color = Math.floor(Math.random() * 360)
        const metadata = { id, color }
        this.socketsClients.set(ws, metadata)

        // Send clients list to everyone
        this.sendClients()
        if (this.onConnection && typeof this.onConnection === "function") {
            this.onConnection(ws, id)
        }

        // What to do when a client is disconnected
        ws.on("close", () => {
            this.closeConnection(ws)
            this.socketsClients.delete(ws)  
        })

        // What to do when a client message is received
        ws.on('message', (bufferedMessage) => { this.newMessage(ws, id, bufferedMessage)})
    }

    closeConnection (ws) {
        if (this.onClose && typeof this.onClose === "function") {
            var id = this.socketsClients.get(ws).id
            this.onClose(ws, id)
        }
    }

    // Send clientsIds to everyone connected with websockets
    sendClients () {
        var clients = []
        this.socketsClients.forEach((value, key) => {
            clients.push(value.id)
        })
        this.wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                var id = this.socketsClients.get(client).id
                var messageAsString = JSON.stringify({ type: "clients", id: id, list: clients })
                client.send(messageAsString)
            }
        })
    }
  
    // Send a message to all websocket clients
    broadcast (obj) {
        this.wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
            }
        })
    }
  
    // Send a private message to a specific websocket client
    private (obj) {
        this.wss.clients.forEach((client) => {
            if (this.socketsClients.get(client).id == obj.destination && client.readyState === WebSocket.OPEN) {
                var messageAsString = JSON.stringify(obj)
                client.send(messageAsString)
                return
            }
        })
    }

    // A message is received from a websocket client
    newMessage (ws, id, bufferedMessage) {
        var messageAsString = bufferedMessage.toString()
        var messageAsObject = {}
            
        try { messageAsObject = JSON.parse(messageAsString) } 
        catch (e) { console.log("Could not parse bufferedMessage from WS message") }

        if (this.onMessage && typeof this.onMessage === "function") {
            this.onMessage(ws, id, messageAsObject)
        }
    }
}

module.exports = Obj

