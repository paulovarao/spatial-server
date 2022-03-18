const drawGeometry = document.getElementById('geometry-type')

const drawSource = new ol.source.Vector({ wrapX: false })
let drawInteraction

function updateGeometryTypeOptions() {
    selectControl.element = drawGeometry
    const url = `${rootUrl}/geometry/types`
    fetch(url).then(handleErrors).then(response => response.json())
        .then(options => selectControl.appendOptions(options))
        .catch(errorAlert)
}

function addDrawInteraction() {
    const value = drawGeometry.value;
    if (value !== 'None') {
        drawInteraction = new ol.interaction.Draw({
            source: drawSource,
            type: value == 'Line' ? 'LineString' : value
        });
        map.addInteraction(drawInteraction);
        drawEnableCheckbox.checked = true
        if(drawSource.getFeatures().length > 0) this.clearSource()
    }
}

function clearDrawSource() {
    drawSource.getFeatures().forEach(f => drawSource.removeFeature(f));
}

function updateOperationOptions() {
    const selectedValue = drawGeometry.value
    selectControl.element = operationSelect
    if (selectedValue == 'None') selectControl.clearExistingOptions()
    else {
        const resource = selectedValue.toLowerCase() + 's'
        const url = `${rootUrl}/${resource}/operations`
        fetch(url).then(handleErrors).then(response => response.json())
            .then(options => selectControl.appendOptions(options))
            .catch(errorAlert)
    }
    updateTablesRows()
}

drawGeometry.onchange = () => {
    clearDrawSource()
    map.removeInteraction(drawInteraction)
    addDrawInteraction()
    operationSelect.value = 'None'
    updateOperationOptions()
}

updateGeometryTypeOptions()