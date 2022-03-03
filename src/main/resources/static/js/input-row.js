class RowTag {
    constructor(id, name) {
        this.id = id
        this.name = name
    }

    createTableRow(rowType, dataArray) {
        const tr = document.createElement('tr')
        tr.setAttribute(rowType, '')
        dataArray.forEach(td => tr.appendChild(td))
        return tr
    }
    
    createButton(idParam, label) {
        const button = document.createElement('button')
        button.setAttribute('class', 'generic-bt')
        button.setAttribute(idParam, '')
        button.innerHTML = label
        return this.createRowDataWithChild(button)
    }

    createDisabledInput(type, idParam) {
        const input = document.createElement('input')
        input.setAttribute('class', 'disabled-input')
        input.setAttribute('type', type)
        input.setAttribute(idParam, '')
        return this.createRowDataWithChild(input)
    }

    createSimpleRowData(attributeName, value) {
        const td = document.createElement('td')
        td.setAttribute(attributeName, '')
        td.innerHTML = value
        return td
    }

    createRowDataWithChild(childElement) {
        const td = document.createElement('td')
        td.appendChild(childElement)
        return td
    }
}

function updateLayerInputInteractions() {
    const saveButtons = document.querySelectorAll('[layer-save]')
    const clearButtons = document.querySelectorAll('[layer-clear]')
    const checkBoxes = document.querySelectorAll('[layer-visible]')
    const colorInputs = document.querySelectorAll('[layer-color]')
    const fileImports = document.querySelectorAll('[layer-import]')
    const fileLoads = document.querySelectorAll('[layer-file]')

    Array.from(saveButtons).forEach(bt => bt.onclick = saveLayerAtRow)
    Array.from(clearButtons).forEach(bt => bt.onclick = clearLayerAtRow)
    Array.from(checkBoxes).forEach(cb => cb.onchange = changeVisibilityAtRow)
    Array.from(colorInputs).forEach(ci => ci.onchange = updateColorBackground)
    Array.from(fileImports).forEach(fi => fi.onclick = importLayerFile)
    Array.from(fileLoads).forEach(fl => fl.onchange = loadFile)

    updateResultInteractions()
}

function updateParamsVisibility(hasParams) {
    const hiddenElements = document.querySelectorAll('[menu-hidden-element]')
    
    const operationNotSelected = operationSelect.value == 'None'

    hiddenElements.forEach(he => {
        const paramsIsEmpty = he.hasAttribute('params-section') && !hasParams
        if (operationNotSelected || paramsIsEmpty) he.classList.add('hidden')
        else he.classList.remove('hidden')
    })
}

function updateTableControls(params) {
    operationParams = params
    const entries = Object.entries(params)
    let hasParams = false
    for (let i in entries) {
        const type = entries[i][1]
        const name = entries[i][0]
        
        if (name == 'result') new ResultRowTag(type)
        else if (type.includes('Wkt')) new LayerRowTag(i, name, type.includes('List'))
        else {
            new ParameterRowTag(i, name, 'number')
            hasParams = true
        }
    }

    updateParamsVisibility(hasParams)

    updateLayerInputInteractions()
}

function clearTableControls(table) {
    while(table.firstChild) {
        const clearButtons = table.querySelectorAll('[layer-clear]')
        if (clearButtons) {
            for (let bt of clearButtons) new LayerRow(bt).removeLayerFromMap()
        }
        table.removeChild(table.lastChild)
    }
}

function updateInputParameters() {
    clearTableControls(paramsTable)
    
    clearTableControls(layersTable)
    clearTableControls(resultTable)

    clearResultData()
    
    const operation = operationSelect.value
    if (operation != 'None') {
        const resource = geometryType.value.toLowerCase() + 's'
        const url = `${rootUrl}/${resource}/params/${operation}`
        fetch(url).then(handleErrors).then(response => response.json())
        .then(updateTableControls)
    }
}
