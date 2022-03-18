const tabContent = document.querySelector('div[tab-content]')
const tableTypes = ['parameters', 'layers', 'result']

class Table {
    constructor(type) {
        this.table = tabContent.querySelector(`table[${type}]`)
    }

    appendRows(rows) {
        rows.forEach(row => this.table.appendChild(row))
    }

    clearRows() {
        while (this.table.firstChild) this.table.removeChild(this.table.lastChild)
    }

    hide() {
        this.table.parentNode.classList.add('hidden')
    }

    show() {
        this.table.parentNode.classList.remove('hidden')
    }
    
    clear() {
        this.clearRows()
        this.hide()
    }
}

function updateTables(classifiedRows) {
    const tableRows = [classifiedRows.paramRows, classifiedRows.layerRows, classifiedRows.resultRows]
    for (let i in tableTypes) {
        const table = new Table(tableTypes[i])
        table.appendRows(tableRows[i])
        if (tableRows[i].length > 0) table.show()
    }
}

function clearTables() {
    clearAllLayers()
    clearResultData()
    getTables().forEach(t => t.clear())
}

function getTables() {
    return tableTypes.map(tt => new Table(tt))
}