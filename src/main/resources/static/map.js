///////////////////////////////       BACKEND         ///////////////////////////////

// constants & variables
const rootUrl = 'http://localhost:8080'

// functions
function requestParams(method, body) {
    return {
        method,
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(body)
    }
}

function handleErrors(response) {
    if (!response.ok) {
        console.log(response.statusText)
        throw new Error(response.statusText);
    }
    return response;
}

///////////////////////////////      MAP CONTROL     ///////////////////////////////

const defaultProjection = {
    dataProjection: 'EPSG:4326',
    featureProjection: 'EPSG:3857'
}

const wktFormat = new ol.format.WKT()

const drawSource = new ol.source.Vector({ wrapX: false })

let draw; // global so we can remove it later
var map;
let operationParams;

const targetLayers = [new ol.layer.Vector(), new ol.layer.Vector()];
const resultLayer = new ol.layer.Vector()

const geometryType = document.getElementById('geometry-type');
const clearDrawButton = document.getElementById('clear')

const layersTable = document.querySelector('table[layers]')

const paramsTable = document.querySelector('table[parameters]')
const updateButton = document.querySelector('[layer-update]')

const osmLayer = new ol.layer.Tile({
    source: new ol.source.OSM()
})

const layers = [osmLayer, new ol.layer.Vector({ source: drawSource })]

const mousePositionControl = new ol.control.MousePosition({
    coordinateFormat: ol.coordinate.createStringXY(4),
    projection: 'EPSG:4326',
    className: 'custom-mouse-position',
    target: document.getElementById('mouse-position'),
});

// classes
class LayerRow {
    constructor(childNode) {
        this.rowNode = childNode.parentNode.parentNode
        const layerId = this.rowNode.querySelector('[layer-id]')
        this.id = layerId ? layerId.innerHTML : -1
        this.colorNode = this.rowNode.querySelector('[layer-color]')
        this.visibleNode = this.rowNode.querySelector('[layer-visible]')
    }

    getLayer() {
        return this.id == -1 ? resultLayer : targetLayers[this.id]
    }

    updateColor(color = null) {
        const backgroundColor = color == null ? 'ffffff' : color
        this.colorNode.value = color
        this.colorNode.style.backgroundColor = `#${backgroundColor}`
    }

    enable() {
        this.visibleNode.checked = true
        Array.from(this.rowNode.querySelectorAll('input'))
            .forEach(input => input.classList.remove('disabled-input'))
    }

    disable() {
        this.visibleNode.checked = false
        Array.from(this.rowNode.querySelectorAll('input'))
            .forEach(input => input.classList.add('disabled-input'))
    }

    layerStyle(feature) {
        const color = hexToInt(this.colorNode.value)
        color.push(0.7)
        return pointStyle(feature, color)
    }

    addLayerToMap(features, callback) {
        if (features.length > 0) {
            const layer = this.getLayer()

            map.removeLayer(layer)

            this.updateColor(randomColor())
            this.enable()

            layer.setSource(new ol.source.Vector({ features }))
            layer.setStyle(feature => this.layerStyle(feature))

            map.addLayer(layer)

            if (callback) callback()
        }
    }

    removeLayerFromMap() {
        const layer = this.getLayer()
        this.updateColor()
        this.disable()
        map.removeLayer(layer)
    }

    updateVisibility() {
        const layer = this.getLayer()
        layer.setVisible(this.visibleNode.checked)
    }
}

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

class LayerRowTag extends RowTag {
    constructor(id, name) {
        super(id, name)

        const row = this.createTableRow('layer-control', this.createDataArray())
        layersTable.appendChild(row)
    }

    createDataArray() {
        return [
            this.createSimpleRowData('layer-id', this.id),
            this.createSimpleRowData('layer-name', this.name),
            this.createDisabledInput('text', 'layer-color'),
            this.createDisabledInput('checkbox', 'layer-visible'),
            this.createButton('layer-save', 'Save'),
            this.createButton('layer-clear', 'Clear')
        ]
    }

    createDisabledInput(type, idParam) {
        const input = document.createElement('input')
        input.setAttribute('class', 'disabled-input')
        input.setAttribute('type', type)
        input.setAttribute(idParam, '')
        return this.createRowDataWithChild(input)
    }
    
    createButton(idParam, label) {
        const button = document.createElement('button')
        button.setAttribute('class', 'generic-bt')
        button.setAttribute(idParam, '')
        button.innerHTML = label
        return this.createRowDataWithChild(button)
    }
}

class ParameterRowTag extends RowTag {
    constructor(id, name, type) {
        super(id, name)

        this.type = type

        const row = this.createTableRow('param-control', this.createDataArray())
        paramsTable.appendChild(row)
    }

    createDataArray() {
        return [
            this.createSimpleRowData('param-id', this.id),
            this.createSimpleRowData('param-name', this.name),
            this.createInput()
        ]
    }

    createInput() {
        const input = document.createElement('input')
        input.setAttribute('type', this.type)
        input.setAttribute('param-value', '')
        return this.createRowDataWithChild(input)
    }
}

// functions
function clearVectorSource(source) {
    source.getFeatures().forEach(f => source.removeFeature(f));
}

function buildMap() {
    map = new ol.Map({
        controls: ol.control.defaults().extend([mousePositionControl]),
        target: 'map',
        layers: layers,
        view: new ol.View({
            center: ol.proj.fromLonLat([-45, -15]),
            zoom: 4
        })
    });
}

function addInteraction() {
    const value = geometryType.value;
    if (value !== 'None') {
        draw = new ol.interaction.Draw({
            source: drawSource,
            type: value,
        });
        map.addInteraction(draw);
    }
}

function updateInteraction() {
    map.removeInteraction(draw);
    addInteraction();
}

function clearDrawSource() {
    clearVectorSource(drawSource)
    updateInteraction()
};

function pointStyle(feature, color) {
    return new ol.style.Style({
        image: new ol.style.Circle({
            radius: 5,
            fill: new ol.style.Fill({ color })
        }),
        geometry: new ol.geom.Point(feature.getGeometry().getCoordinates())
    });
}

function hexToInt(hex) {
    const rgb = [0, 1, 2].map(i => parseInt(hex.substring(2 * i, 2 * (i + 1)), 16))
    return rgb
}

function randomColor() {

    function intToHex(c) {
        const hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }

    const rgb = [0, 1, 2].map(i => Math.floor(Math.random() * 256))
    return rgb.reduce((hex, c) => hex + intToHex(c), '')
}

function updateColorBackground(event) {
    const colorInput = event.target
    const layerRow = new LayerRow(colorInput)
    colorInput.style.backgroundColor = `#${layerRow.colorNode.value}`
}

function saveLayerAtRow(event) {
    const layerRow = new LayerRow(event.target)
    layerRow.addLayerToMap(drawSource.getFeatures(), clearDrawSource)
}

function clearLayerAtRow(event) {
    const layerRow = new LayerRow(event.target)
    layerRow.removeLayerFromMap()
}

function changeVisibilityAtRow(event) {
    const layerRow = new LayerRow(event.target)
    const layer = layerRow.id == -1 ? resultLayer : targetLayers[layerRow.id]
    layerRow.updateVisibility(layer)
}

function wktGeometries(features) {
    return features.map(f => wktFormat.writeGeometry(f.getGeometry(), defaultProjection))
}

function updateResultLayer(event, jsonResponse) {
    const features = jsonResponse.map(wkt => wktFormat.readFeature(wkt, defaultProjection))
    const layerRow = new LayerRow(event.target)
    layerRow.addLayerToMap(features)
}

function processOperation(event) {
    const layerIds = document.querySelectorAll('[layer-id]')
    const ids = Array.from(layerIds).map(id => id.innerHTML)
    const wktArray = ids.map(id => wktGeometries(targetLayers[id].getSource().getFeatures()))

    const params = document.querySelectorAll('[param-value]')
    const values = Array.from(params).map(param => param.value)

    const requestBody = {
        points: wktArray[0],
        centroid: wktArray[1][0],
        angleDeg: parseFloat(values[0]),
        rotationSense: parseInt(values[1])
    }

    const operation = operationSelect.value
    const resource = geometryType.value.toLowerCase() + 's'
    const url = `${rootUrl}/${resource}/${operation}`
    fetch(url, requestParams('POST', requestBody)).then(handleErrors)
        .then(response => response.json())
        .then(response => updateResultLayer(event, response))
        .catch(errorAlert)
}

// script
buildMap()

// events
geometryType.onchange = () => {
    updateInteraction()
    updateOperationOptions()
}

clearDrawButton.onclick = clearDrawSource

updateButton.onclick = processOperation


/////////////////////////////      HTML CONTROL     ///////////////////////////////

// constants & variables
const toggleMenuButton = document.querySelector('aside > button')
const toggleMenu = document.querySelector('aside')

const wrapper = document.querySelector('#wrapper')
const operationSelect = document.querySelector('#operation')
const about = document.querySelector('#about')
const aboutButton = document.querySelector('#about-bt')
const closeAboutButton = document.querySelector('#about button')
const alertCard = document.querySelector('#alert')
const alertMessage = document.querySelector('#alert-message')
const closeAlertButton = document.querySelector('#alert button')
const hiddenClass = 'hidden'

// functions
function toggle() {
    const current = toggleMenu.classList[0]
    toggleMenu.classList.remove(current)
    const isHidden = current == 'aside-hidden'
    toggleMenuButton.innerHTML = isHidden ? '<' : '>'
    toggleMenu.classList.add(isHidden ? 'aside-shown' : 'aside-hidden')
}

function showElements(elements) {
    wrapper.style.display = 'flex'
    elements.forEach(element => element.classList.remove(hiddenClass))
}
function hideElements(elements) {
    wrapper.style.display = 'none'
    elements.forEach(element => element.classList.add(hiddenClass))
}

function errorAlert(error) {
    showElements([alertCard])
    alertMessage.innerHTML = !error.response ? 'Connection to server failed' : error
    // alertMessage.innerHTML = error
}

function clearExistingOptions(select) {
    Array.from(select.childNodes).filter(option => option.innerHTML != 'None')
        .forEach(option => select.removeChild(option))
}

function appendOptions(select, options) {

    function createOption(optionValue) {
        const option = document.createElement('option')
        option.value = optionValue
        option.innerHTML = optionValue
        return option
    }

    clearExistingOptions(select)
    options.forEach(option => select.appendChild(createOption(option)))
}

function updateGeometryTypeOptions() {
    const url = `${rootUrl}/geometry/types`
    fetch(url).then(handleErrors).then(response => response.json())
        .then(options => appendOptions(geometryType, options))
        .catch(errorAlert)
}

function updateOperationOptions() {
    const selectedValue = geometryType.value
    if (selectedValue == 'None') clearExistingOptions(operationSelect)
    else {
        const resource = selectedValue.toLowerCase() + 's'
        const url = `${rootUrl}/${resource}/operations`
        fetch(url).then(handleErrors).then(response => response.json())
            .then(options => appendOptions(operationSelect, options))
            .catch(errorAlert)
    }
}

function clearTableControls(table) {
    while(table.firstChild) table.removeChild(table.lastChild)
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
        if (type.includes('Wkt')) new LayerRowTag(i, name)
        else new ParameterRowTag(i, name, 'number')
    }

    updateLayerInputInteractions()
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

// script
updateGeometryTypeOptions()

// events
toggleMenuButton.onclick = toggle
aboutButton.onclick = () => showElements([about])
closeAboutButton.onclick = () => hideElements([about])
closeAlertButton.onclick = () => hideElements([alertCard])
operationSelect.onchange = updateInputParameters