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
        console.log(response.statusText)
        throw new Error(response.statusText);
    }
    return response;
}
