function buildSatelliteSection() {
    const classifiedRows = {
        paramRows: satelliteParametersRows(), 
        layerRows: [], 
        resultRows: satelliteResultRows()
    }
    updateTables(classifiedRows)
}

function satelliteParametersRows() {
    const satId = new ParameterRow(1, 'satelliteId', 'number').getElement()
    const maxLa = new ParameterRow(2, 'maxLookAngle', 'number').getElement()
    const minLa = new ParameterRow(3, 'minLookAngle', 'number').getElement()
    const begin = new ParameterRow(4, 'begin', 'datetime-local').getElement()
    const end = new ParameterRow(5, 'end', 'datetime-local').getElement()
    return [satId, maxLa, minLa, begin, end]
}

function satelliteResultRows() {
    const result = new ResultRow(0, 'result', 'List.Wkt', updateSatelliteOrbit).getElement()
    return [result]
}