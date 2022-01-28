const drawSource = new ol.source.Vector({ wrapX: false })
const geometryType = document.getElementById('geometry-type')
const clearDrawButton = document.getElementById('clear')

let draw; // global so we can remove it later

function addInteraction() {
    const value = geometryType.value;
    // if (value == 'Line') value = 'LineString'
    if (value !== 'None') {
        draw = new ol.interaction.Draw({
            source: drawSource,
            type: value == 'Line' ? 'LineString' : value
        });
        map.addInteraction(draw);
    }
}

function updateInteraction() {
    map.removeInteraction(draw);
    addInteraction();
}

function clearVectorSource(source) {
    source.getFeatures().forEach(f => source.removeFeature(f));
}

function clearDrawSource() {
    clearVectorSource(drawSource)
    updateInteraction()
}

function updateGeometryTypeOptions() {
    const url = `${rootUrl}/geometry/types`
    fetch(url).then(handleErrors).then(response => response.json())
        .then(options => appendOptions(geometryType, options))
        .catch(errorAlert)
}

updateGeometryTypeOptions()

geometryType.onchange = () => {
    updateInteraction()
    updateOperationOptions()
}

clearDrawButton.onclick = clearDrawSource