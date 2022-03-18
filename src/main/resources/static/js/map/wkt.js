const wktFormat = new ol.format.WKT()

function wktGeometries(features) {
    return features.map(f => wktFormat.writeGeometry(f.getGeometry(), mapControls.defaultProjection))
}

function geometryValidation(features) {
    for (let f of features) {
        const type = f.getGeometry().getType()
        if (!type.includes(drawGeometry.value)) return false;
    }
    return true
}

function singleFeatures(features) {
    
    const singlePoint = coordinates => new ol.geom.Point(coordinates)
    const singleLine = coordinates => new ol.geom.LineString(coordinates)
    const singlePolygon = coordinates => new ol.geom.Polygon(coordinates)

    const geometryConversion = (coordinates, convertFunction) => {
        const array = []
        for (let c of coordinates) {
            const sf = new ol.Feature({
                geometry: convertFunction(c)
            })
            array.push(sf)
        }
        return array
    }

    function singleFeatureArray(type, geometry) {
        let convertFunction = null
        if (type.includes('Point')) convertFunction = singlePoint
        else if (type.includes('LineString')) convertFunction = singleLine
        else if (type.includes('Polygon')) convertFunction = singlePolygon

        return convertFunction == null ? [] : geometryConversion(geometry.getCoordinates(), convertFunction)
    }
    
    const singleFeatures = []
    for (let f of features) {
        const type = f.getGeometry().getType()

        if (type.includes('Multi')) {
            const geometry = f.getGeometry()
            const sfArray = singleFeatureArray(type, geometry)
            for (let sf of sfArray) singleFeatures.push(sf)
        } else {
            singleFeatures.push(f)
        }
    }
    return singleFeatures
}