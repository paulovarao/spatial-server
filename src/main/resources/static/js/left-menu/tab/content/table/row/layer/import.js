let importedLayer

function loadFile() {
    const fr = new FileReader()
    fr.onload = () => {
        const array = JSON.parse(fr.result)
        const features = array.map(wkt => wktFormat.readFeature(wkt, mapControls.defaultProjection))
        const validFeatures = geometryValidation(features)
        if (validFeatures) importedLayer.addLayerToMap(singleFeatures(features))
        else errorAlert("Imported features geometry type must be " + geometryType.value)
    }
    fr.readAsText(this.files[0])
}