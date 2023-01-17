
// Aquesta funció genera una espera de "time" milisegons en funcions "async"
function wait (time) {
    return new Promise((resolve, request) => {
        setTimeout(resolve, time);
    })
}

// Enviar un missatge de tipus "consola" al servidor
function mostraConsola (nom) {
    loadData({
        type: "consola",
        name: nom
    })
}

// Aquesta funció carrega dades del servidor amb "POST"
async function loadData (dataObj) {

    // Mostra 'Carregant dades...' mentre espera
    refData = document.getElementById("data")
    refData.innerHTML = "Carregant dades..."

    // Prepara la crida POST
    let post = {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(dataObj)
    }

    // Fa la crida POST, espera la resposta, i la deixa a 'obj'
    let receivedData = await fetch("/dades", post)
    let obj = await receivedData.json()
    
    // Forcem una espera
    await wait(500) 
    
    // Mostrar les dades rebudes
    refData.innerHTML = `
    <h2>${obj.result.name}</h2>
    <p>Any: ${obj.result.date}</p>
    <p>Marca: ${obj.result.brand}</p>
    <img src="${obj.result.image}"/>
    `
}