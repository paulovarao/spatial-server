const resultLayer = new ol.layer.Vector()
const resultTable = document.querySelector('table[result]')
const resultText = document.querySelector('[result-text]')

let result = null

class ResultRowTag extends RowTag {
    constructor(type) {
        super(-1, 'result')

        const isWkt = type.includes('Wkt')
        const isArray = type.includes('List')
        const array = isWkt ? this.createDataLayerArray() : this.createDataArray()

        const row = this.createTableRow('result-control', array)
        if (isWkt) row.setAttribute('layer', '')
        if (isArray) row.setAttribute('array', '')
        resultTable.appendChild(row)
    }

    createDataArray() {
        return [
            this.createSimpleRowData('result-name', this.name),
            this.createButton('result-update', 'Update'),
            this.createButton('result-clear', 'Clear'),
            this.createButton('result-export', 'Export')
        ]
    }

    createDataLayerArray() {
        return [
            this.createSimpleRowData('result-name', this.name),
            this.createDisabledInput('text', 'layer-color'),
            this.createDisabledInput('checkbox', 'layer-visible'),
            this.createButton('result-update', 'Update'),
            this.createButton('result-clear', 'Clear'),
            this.createButton('result-export', 'Export')
        ]
    }
}

function updateResultInteractions() {
    const updateButton = document.querySelector('[result-update]')
    updateButton.onclick = processOperation

    const clearButton = document.querySelector('[result-clear]')
    clearButton.onclick = clearResult

    const exportResultButton = document.querySelector('[result-export]')
    exportResultButton.addEventListener('click', exportResult, false);
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
            errorAlert("Geometry is empty")
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
    result = jsonResponse
    resultText.innerHTML = jsonResponse.join(';')
    const resultRow = document.querySelector('[result-control]')
    if (resultRow.hasAttribute('layer')) updateResultLayer(event, jsonResponse)
}

function clearResultData() {
    result = null
    resultText.innerHTML = null
}

function clearResult(event) {
    clearResultData()
    const resultRow = document.querySelector('[result-control]')
    if (resultRow.hasAttribute('layer')) clearLayerAtRow(event)
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

function exportResult() {
    const scenario = requestInputs()
    scenario['result'] = result
    const link = document.getElementById('downloadlink')
    link.href = makeTextFile(JSON.stringify(scenario))
    link.click()
}