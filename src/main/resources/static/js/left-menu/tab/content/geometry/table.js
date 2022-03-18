function updateTablesRows() {

    clearTables()

    const operation = operationSelect.value
    if (operation != 'None') {
        const resource = drawGeometry.value.toLowerCase() + 's'
        const url = `${rootUrl}/${resource}/params/${operation}`
        fetch(url).then(handleErrors).then(response => response.json())
            .then(classifyOperationParams)
            .then(updateTables)
            .catch(errorAlert)
    }
}

function classifyOperationParams(operationParams) {
    const entries = Object.entries(operationParams)
    const resultRows = [], layerRows = [], paramRows = []
    
    for (let i in entries) {
        const type = entries[i][1]
        const name = entries[i][0]

        if (name == 'result') resultRows.push(entries[i])
        else if (type.includes('Wkt')) layerRows.push(new LayerRow(i, name, type).getElement())
        else paramRows.push(new ParameterRow(i, name, 'number').getElement())
    }

    let index = layerRows.length
    for (let i in resultRows) {
        const type = resultRows[i][1]
        const name = resultRows[i][0]

        resultRows[i] = new ResultRow(index, name, type, processOperation).getElement()
        index++
    }
    
    return { layerRows, paramRows, resultRows }
}