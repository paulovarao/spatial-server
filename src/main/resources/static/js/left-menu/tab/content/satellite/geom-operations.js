async function defaultPostRequest(resource, body) {
    const url = `${rootUrl}${resource}`
    const response = await fetch(url, requestParams('POST', body))
        .then(handleErrors).then(res => res.json())
        .catch(errorAlert)
    return response
}

async function union(polygons) {
    return await defaultPostRequest('/polygons/union', { polygons })
}

async function difference(polygons) {
    return await defaultPostRequest('/polygons/difference', { polygons })
}

async function lineBuffer(points, distanceInKm) {
    return await defaultPostRequest('/points/line-buffer', { points, distanceInKm })
}

async function circularBuffer(points, distanceInKm, numberOfAzimuths) {
    return await defaultPostRequest('/points/circular-buffer', { points, distanceInKm, numberOfAzimuths })
}

async function radius(points) {
    return await defaultPostRequest('/points/radius', { points })
}

async function fieldOfRegard(positions, maxLookAngle, minLookAngle) {
    return await defaultPostRequest('/satellite/field-of-regard', { positions, maxLookAngle, minLookAngle })
}