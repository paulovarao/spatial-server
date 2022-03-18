class ParameterRow extends Row {
    constructor(id, name, type) {
        super(id, name)
        this.type = type
    }

    getElement() {
        return this.createTableRow('param-control', this.createDataArray())
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