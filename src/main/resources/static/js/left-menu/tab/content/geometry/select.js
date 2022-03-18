const selectControl = {
    element: null,

    clearExistingOptions() {
        Array.from(this.element.childNodes).filter(option => option.innerHTML != 'None')
            .forEach(option => this.element.removeChild(option))
        // operationSelect.value = 'None'
        // geometryTab.updateTablesVisibility(false)
    },

    createOption(optionValue) {
        const option = document.createElement('option')
        option.value = optionValue
        option.innerHTML = optionValue
        return option
    },

    appendOptions(options) {
        this.clearExistingOptions()
        options.forEach(option => this.element.appendChild(this.createOption(option)))
    }
}