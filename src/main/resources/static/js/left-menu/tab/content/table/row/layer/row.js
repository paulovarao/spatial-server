class LayerRow extends Row {
    constructor(id, name, type) {
        super(id, name)
        this.type = type
    }

    getElement() {
        const row = this.createTableRow('layer-control', this.createDataArray())
        if (this.type.includes('List')) row.setAttribute('array', '')
        return row
    }
    
    createInputFile(idParam, interaction) {
        const inputFile = document.createElement('input')
        inputFile.setAttribute('type', 'file')
        inputFile.setAttribute('class', 'hidden')
        inputFile.setAttribute(idParam, '')
        this.addInteraction(inputFile, interaction)
        return this.createRowDataWithChild(inputFile)
    }

    createDataArray() {
        return [
            this.createSimpleRowData('layer-id', this.id),
            this.createSimpleRowData('layer-name', this.name),
            this.createDisabledInput('text', 'layer-color', { onchange: updateColorBackground }),
            this.createDisabledInput('checkbox', 'layer-visible', { onclick: changeVisibilityAtRow }),
            this.createButton('layer-save', 'Save', { onclick: saveLayerAtRow }),
            this.createButton('layer-import', 'Import', { onclick: importLayerFile }),
            this.createButton('layer-clear', 'Clear', { onclick: clearLayerAtRow }),
            this.createInputFile('layer-file', { onchange: loadFile })
        ]
    }
}

function saveLayerAtRow(event) {
    const mapLayer = new MapLayer(event.target)
    mapLayer.addLayerToMap(drawSource.getFeatures(), clearDrawSource)
}

function importLayerFile(event) {
    importedLayer = new MapLayer(event.target)
    importedLayer.importLayer()
}