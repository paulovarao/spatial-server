///////////////////////////////      MAP CONTROL     ///////////////////////////////

const mapControls = {
    projection: 'EPSG:4326',
    
    defaultProjection: {
        dataProjection: 'EPSG:4326',
        featureProjection: 'EPSG:3857'
    },
}

const mousePositionControl = new ol.control.MousePosition({
    coordinateFormat: ol.coordinate.createStringXY(4),
    projection: mapControls.projection,
    className: 'custom-mouse-position',
    target: document.getElementById('mouse-position'),
})

var map = new ol.Map({
    controls: ol.control.defaults().extend([mousePositionControl]),
    target: 'map',
    layers: [new ol.layer.Tile({ source: new ol.source.OSM() }), new ol.layer.Vector({ source: drawSource }), ],
    view: new ol.View({
        center: ol.proj.fromLonLat([-45, -15]),
        zoom: 4
    })
})
