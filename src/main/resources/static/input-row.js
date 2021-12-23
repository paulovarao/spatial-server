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

    Array.from(saveButtons).forEach(bt => bt.onclick = saveLayerAtRow)
    Array.from(clearButtons).forEach(bt => bt.onclick = clearLayerAtRow)
    Array.from(checkBoxes).forEach(cb => cb.onchange = changeVisibilityAtRow)
    Array.from(colorInputs).forEach(ci => ci.onchange = updateColorBackground)
}

function updateTableControls(params) {
    operationParams = params
    const entries = Object.entries(params)
    for (let i in entries) {
        const type = entries[i][1]
        const name = entries[i][0]
        if (type.includes('Wkt')) new LayerRowTag(i, name, type.includes('List'))
        else new ParameterRowTag(i, name, 'number')
    }

    updateLayerInputInteractions()
}

function clearTableControls(table) {
    while(table.firstChild) table.removeChild(table.lastChild)
}

function updateInputParameters() {
    clearTableControls(layersTable)
    clearTableControls(paramsTable)
    
    const operation = operationSelect.value
    if (operation != 'None') {
        const resource = geometryType.value.toLowerCase() + 's'
        const url = `${rootUrl}/${resource}/${operation}/params`
        fetch(url).then(handleErrors).then(response => response.json())
            .then(updateTableControls)
    }
}
