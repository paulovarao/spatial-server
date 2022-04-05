async function defaultPostRequest(resource, body) {
    const url = `${rootUrl}${resource}`
    const response = await fetch(url, requestParams('POST', body))
        .then(handleErrors).then(res => res.json())
        .catch(errorAlert)
    return response
}

async function fieldOfRegard(positions, maxLookAngle, minLookAngle) {
    return await defaultPostRequest('/satellite/field-of-regard', { positions, maxLookAngle, minLookAngle })
}