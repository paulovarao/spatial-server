const layerArray = []

class MapLayer {
    constructor(childNode) {
        this.rowNode = childNode.parentNode.parentNode
        this.id = this.rowNode.querySelector('[layer-id]').innerHTML
        this.colorNode = this.rowNode.querySelector('[layer-color]')
        this.visibleNode = this.rowNode.querySelector('[layer-visible]')
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
        return featureStyle(feature, color)
    }

    addLayerToMap(features, callback) {
        this.removeLayerFromMap()

        if (features.length > 0) {
            layerArray[this.id] = new ol.layer.Vector()
            const layer = layerArray[this.id]
            
            this.updateColor(randomColor())
            this.enable()

            layer.setSource(new ol.source.Vector({ features }))
            layer.setStyle(feature => this.layerStyle(feature))
            
            map.addLayer(layer)

            if (callback) callback()
        }
    }

    removeLayerFromMap() {
        const layer = layerArray[this.id]
        layerArray[this.id] = null

        this.updateColor()
        this.disable()
        map.removeLayer(layer)
    }

    updateVisibility() {
        const layer = layerArray[this.id]
        layer.setVisible(this.visibleNode.checked)
    }

    importLayer() {
        const fileInput = this.rowNode.querySelector('[layer-file]')
        fileInput.click()
    }
}

function clearAllLayers() {
    const layers = tabContent.querySelectorAll('[layer-id]')
    layers.forEach(l => new MapLayer(l).removeLayerFromMap())
}