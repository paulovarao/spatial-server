const drawEnableCheckbox = document.getElementById('toggle-draw')

drawEnableCheckbox.onchange = () => {
    if (drawGeometry.value !== 'None') {
        drawEnableCheckbox.checked ? map.addInteraction(drawInteraction)
            : map.removeInteraction(drawInteraction)
    }
}