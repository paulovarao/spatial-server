const drawSource = new ol.source.Vector({ wrapX: false })
const geometryType = document.getElementById('geometry-type')
const clearDrawButton = document.getElementById('clear')
const toggleDraw = document.getElementById('toggle-draw')

let draw; // global so we can remove it later

function addInteraction() {
    const value = geometryType.value;
    if (value !== 'None') {
        draw = new ol.interaction.Draw({
            source: drawSource,
            type: value == 'Line' ? 'LineString' : value
        });
        map.addInteraction(draw);
        toggleDraw.checked = true
        if(drawSource.getFeatures().length > 0) clearDrawSource()
    }
}

function updateInteraction() {
    map.removeInteraction(draw);
    addInteraction();
}

function toggleInteraction() {
    if (geometryType.value !== 'None')
        toggleDraw.checked ? map.addInteraction(draw) : map.removeInteraction(draw)
}

function clearVectorSource(source) {
    source.getFeatures().forEach(f => source.removeFeature(f));
}

function clearDrawSource() {
    clearVectorSource(drawSource)
    // updateInteraction()
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
toggleDraw.onchange = toggleInteraction