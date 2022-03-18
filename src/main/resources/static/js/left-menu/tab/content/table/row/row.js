class Row {
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
    
    createButton(idParam, label, interaction) {
        const button = document.createElement('button')
        button.setAttribute('class', 'generic-bt')
        button.setAttribute(idParam, '')
        button.innerHTML = label
        this.addInteraction(button, interaction)
        return this.createRowDataWithChild(button)
    }

    createDisabledInput(type, idParam, interaction) {
        const input = document.createElement('input')
        input.setAttribute('class', 'disabled-input')
        input.setAttribute('type', type)
        input.setAttribute(idParam, '')
        this.addInteraction(input, interaction)
        return this.createRowDataWithChild(input)
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

    addInteraction(element, interaction) {
        if (interaction) {
            const entries = Object.entries(interaction)
            element[`${entries[0][0]}`] = entries[0][1]
        }
    }
}

function updateColorBackground(event) {
    const colorInput = event.target
    const mapLayer = new MapLayer(colorInput)
    colorInput.style.backgroundColor = `#${mapLayer.colorNode.value}`
}

function changeVisibilityAtRow(event) {
    const mapLayer = new MapLayer(event.target)
    const layer = mapLayer.id == -1 ? resultLayer : layerArray[mapLayer.id]
    mapLayer.updateVisibility(layer)
}

function clearLayerAtRow(event) {
    const mapLayer = new MapLayer(event.target)
    mapLayer.removeLayerFromMap()
}

