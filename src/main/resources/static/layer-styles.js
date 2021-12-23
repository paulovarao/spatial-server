
function pointStyle(feature, color) {
    return new ol.style.Style({
        image: new ol.style.Circle({
            radius: 5,
            fill: new ol.style.Fill({ color })
        }),
        geometry: new ol.geom.Point(feature.getGeometry().getCoordinates())
    });
}