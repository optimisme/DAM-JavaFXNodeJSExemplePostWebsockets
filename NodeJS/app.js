const express = require('express')
const fs = require('fs/promises')
const url = require('url')
const post = require('./post.js')

// Iniciar servidors HTTP i WebSockets
const app = express()

// Configurar el port del servidor HTTP
const port = process.env.PORT || 3000

// Publicar els arxius HTTP de la carpeta 'public'
app.use(express.static('public'))

// Activar el servidor HTTP
const httpServer = app.listen(port, appListen)
function appListen () {
  console.log(`Example app listening for HTTP queries on: http://localhost:${port}`)
}

// Definir URL per les dades tipus POST
app.post('/dades', getDades)
async function getDades (req, res) {
  let receivedPOST = await post.getPostObject(req)
  let result = {};

  var arxiuText = await fs.readFile("./public/consoles/llista-dades.json", { encoding: 'utf8'})
  var objLlistaConsoles = JSON.parse(arxiuText)

  if (receivedPOST) {
    if (receivedPOST.type == "consola") {
      var objLlistaFiltrada = objLlistaConsoles.filter(function (obj) { return obj.name == receivedPOST.name })
      await wait(1500)
      if (objLlistaFiltrada.length > 0) {
        result = { status: "OK", result: objLlistaFiltrada[0] }
      }
    }
    if (receivedPOST.type == "marques") {
      var objLlistaMarques = objLlistaConsoles.map(function (obj) { return obj.brand })
      await wait(1500)
      let senseDuplicats = [...new Set(objLlistaMarques)]
      result = { status: "OK", result: senseDuplicats.sort() } 
    }
  }

  res.writeHead(200, { 'Content-Type': 'application/json' })
  res.end(JSON.stringify(result))
}

// Força un temps d'espera
function wait (ms) {
  return new Promise(resolve => setTimeout(resolve, ms))
}