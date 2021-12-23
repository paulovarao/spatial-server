const operationSelect = document.querySelector('#operation')

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

operationSelect.onchange = updateInputParameters