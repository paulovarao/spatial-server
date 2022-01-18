const paramsTable = document.querySelector('table[parameters]')

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