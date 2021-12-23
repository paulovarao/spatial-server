const resultLayer = new ol.layer.Vector()

const updateButton = document.querySelector('[layer-update]')

function wktGeometries(features) {
    return features.map(f => wktFormat.writeGeometry(f.getGeometry(), defaultProjection))
}

function updateResultLayer(event, jsonResponse) {
    const features = jsonResponse.map(wkt => wktFormat.readFeature(wkt, defaultProjection))
    const layerRow = new LayerRow(event.target)
    layerRow.addLayerToMap(features)
}

function requestInputs() {
    const requestBody = {}

    function layerValue(node) {
        const id = node.querySelector('[layer-id]').innerHTML
        const wkt = wktGeometries(inputLayers[id].getSource().getFeatures())
        return node.hasAttribute('array') ? wkt : wkt[0]
    }

    function updateRequestBody(type, valueFunction) {
        const inputs = document.querySelectorAll(`[${type}-control]`)
        for (let node of inputs) {
            const name = node.querySelector(`[${type}-name]`).innerHTML
            requestBody[name] = valueFunction(node)
        }
    }

    updateRequestBody('layer', layerValue)
    updateRequestBody('param', node => node.querySelector('[param-value]').value)

    return requestBody
}

function processOperation(event) {
    const requestBody = requestInputs()

    const operation = operationSelect.value
    const resource = geometryType.value.toLowerCase() + 's'
    const url = `${rootUrl}/${resource}/${operation}`
    fetch(url, requestParams('POST', requestBody)).then(handleErrors)
        .then(response => response.json())
        .then(response => updateResultLayer(event, response))
        .catch(errorAlert)
}

updateButton.onclick = processOperation