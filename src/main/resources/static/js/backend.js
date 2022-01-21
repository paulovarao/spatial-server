///////////////////////////////       BACKEND         ///////////////////////////////

// constants & variables
const rootUrl = 'http://localhost:8080'

// functions
function requestParams(method, body) {
    return {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(body)
    }
}

function handleErrors(response) {
    if (!response.ok) {
        return response.text().then(text => { throw new Error(text) })
    }
    return response;
}
