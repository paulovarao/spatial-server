class ResultRow extends Row {
    constructor(id, name, type, updateCallback) {
        super(id, name)
        this.type = type
        this.updateCallback = updateCallback
    }

    getElement() {
        const isWkt = this.type.includes('Wkt')
        const isArray = this.type.includes('List')
        const array = isWkt ? this.createDataLayerArray() : this.createDataArray()

        const row = this.createTableRow('result-control', array)
        if (isWkt) row.setAttribute('layer', '')
        if (isArray) row.setAttribute('array', '')
        return row
    }

    createDataArray() {
        const array = this.createDataLayerArray()
        return [array[1], array[4], array[5], array[6]]
    }

    createDataLayerArray() {
        return [
            this.createSimpleRowData('layer-id', this.id),
            this.createSimpleRowData('result-name', this.name),
            this.createDisabledInput('text', 'layer-color', { onchange: updateColorBackground }),
            this.createDisabledInput('checkbox', 'layer-visible', { onclick: changeVisibilityAtRow }),
            this.createButton('result-update', 'Update', { onclick: this.updateCallback }),
            this.createButton('result-clear', 'Clear', { onclick: clearResult }),
            this.createButton('result-export', 'Export', { onclick: exportResult })
        ]
    }
}

let result

function getTextArea() {
    return tabContent.querySelector('[result-text]')
}

///////////////////////       UPDATE       ///////////////////////

function updateResultLayer(event, jsonResponse) {
    const features = jsonResponse.map(wkt => wktFormat.readFeature(wkt, mapControls.defaultProjection))
    const mapLayer = new MapLayer(event.target)
    mapLayer.addLayerToMap(features)
}

function updateResult(event, jsonResponse) {
    result = jsonResponse
    getTextArea().innerHTML = jsonResponse.join(';')
    const resultRow = tabContent.querySelector('[result-control]')
    if (resultRow.hasAttribute('layer')) updateResultLayer(event, jsonResponse)
}

function processOperation(event) {
    const requestBody = requestInputs(tabContent)

    const operation = operationSelect.value
    const resource = drawGeometry.value.toLowerCase() + 's'
    const url = `${rootUrl}/${resource}/${operation}`
    fetch(url, requestParams('POST', requestBody)).then(handleErrors)
        .then(response => response.json())
        .then(response => updateResult(event, response))
        .catch(errorAlert)
}

///////////////////////       CLEAR       ///////////////////////

function clearResultData() {
    result = null
    getTextArea().innerHTML = null
}

function clearResult(event) {
    clearResultData()
    const resultRow = tabContent.querySelector('[result-control]')
    if (resultRow.hasAttribute('layer')) clearLayerAtRow(event)
}

///////////////////////       EXPORT       ///////////////////////

function exportResult() {
    const scenario = requestInputs()
    scenario['result'] = result
    const link = document.getElementById('downloadlink')
    link.href = makeTextFile(JSON.stringify(scenario))
    link.click()
}

function layerValue(node) {
    try {
        const id = node.querySelector('[layer-id]').innerHTML

        const wkt = wktGeometries(layerArray[id].getSource().getFeatures())
        return node.hasAttribute('array') ? wkt : wkt[0]
    } catch (error) {
        console.log("Geometry is empty")
    }
}

function requestInputs() {
    const requestBody = {}

    function updateRequestBody(type, valueFunction) {
        const inputs = tabContent.querySelectorAll(`[${type}-control]`)
        for (let node of inputs) {
            const name = node.querySelector(`[${type}-name]`).innerHTML
            requestBody[name] = valueFunction(node)
        }
    }

    updateRequestBody('layer', layerValue)
    updateRequestBody('param', node => node.querySelector('[param-value]').value)

    return requestBody
}

// const exportResultButton = this.tab.querySelector('[result-export]')
// exportResultButton.addEventListener('click', this.exportResult, false);