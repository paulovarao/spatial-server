const layersTable = document.querySelector('table[layers]')
const inputLayers = [];

class LayerRow {
    constructor(childNode) {
        this.rowNode = childNode.parentNode.parentNode
        const layerId = this.rowNode.querySelector('[layer-id]')
        this.id = layerId ? layerId.innerHTML : -1
        this.colorNode = this.rowNode.querySelector('[layer-color]')
        this.visibleNode = this.rowNode.querySelector('[layer-visible]')
    }

    getLayer() {
        return this.id == -1 ? resultLayer : inputLayers[this.id]
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
            if (this.id > -1) inputLayers[this.id] = new ol.layer.Vector()

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
        if (this.id > -1) inputLayers[this.id] = null

        this.updateColor()
        this.disable()
        map.removeLayer(layer)
    }

    updateVisibility() {
        const layer = this.getLayer()
        layer.setVisible(this.visibleNode.checked)
    }
}

class LayerRowTag extends RowTag {
    constructor(id, name, isArray) {
        super(id, name)
        const row = this.createTableRow('layer-control', this.createDataArray())
        if (isArray) row.setAttribute('array', '')
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
    const layer = layerRow.id == -1 ? resultLayer : inputLayers[layerRow.id]
    layerRow.updateVisibility(layer)
}