const resultLayer = new ol.layer.Vector()

const resultTable = document.querySelector('table[result]')

class ResultRowTag extends RowTag {
    constructor(type) {
        super(-1, 'result')

        const isWkt = type.includes('Wkt')
        const array = isWkt ? this.createDataLayerArray() : this.createDataArray()

        const row = this.createTableRow('result-control', array)
        if (isWkt) row.setAttribute('layer', '')
        resultTable.appendChild(row)
    }

    createDataArray() {
        return [
            this.createSimpleRowData('result-name', this.name),
            this.createButton('result-update', 'Update')
        ]
    }
    
    createDataLayerArray() {
        return [
            this.createSimpleRowData('result-name', this.name),
            this.createDisabledInput('text', 'layer-color'),
            this.createDisabledInput('checkbox', 'layer-visible'),
            this.createButton('result-update', 'Update'),
            this.createButton('layer-clear', 'Clear')
        ]
    }
}

function wktGeometries(features) {
    return features.map(f => wktFormat.writeGeometry(f.getGeometry(), defaultProjection))
}

function requestInputs() {
    const requestBody = {}

    function layerValue(node) {
        try {
            const id = node.querySelector('[layer-id]').innerHTML
            const wkt = wktGeometries(inputLayers[id].getSource().getFeatures())
            return node.hasAttribute('array') ? wkt : wkt[0]
        } catch (error) {
            console.log(error.message)
        }
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

function updateResultLayer(event, jsonResponse) {
    const features = jsonResponse.map(wkt => wktFormat.readFeature(wkt, defaultProjection))
    const layerRow = new LayerRow(event.target)
    layerRow.addLayerToMap(features)
}

function updateResult(event, jsonResponse) {
    const resultText = document.querySelector('[result-text]')
    resultText.innerHTML = jsonResponse
    const resultRow = document.querySelector('[result-control]')
    if (resultRow.hasAttribute('layer')) updateResultLayer(event, jsonResponse)
}

function processOperation(event) {
    const requestBody = requestInputs()

    const operation = operationSelect.value
    const resource = geometryType.value.toLowerCase() + 's'
    const url = `${rootUrl}/${resource}/${operation}`
    fetch(url, requestParams('POST', requestBody)).then(handleErrors)
        .then(response => response.json())
        .then(response => updateResult(event, response))
        .catch(errorAlert)
}