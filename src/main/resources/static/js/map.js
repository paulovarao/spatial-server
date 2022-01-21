///////////////////////////////      MAP CONTROL     ///////////////////////////////

const defaultProjection = {
    dataProjection: 'EPSG:4326',
    featureProjection: 'EPSG:3857'
}

const wktFormat = new ol.format.WKT()

var map;

const osmLayer = new ol.layer.Tile({
    source: new ol.source.OSM()
})

const layers = [osmLayer, new ol.layer.Vector({ source: drawSource }), ]

const mousePositionControl = new ol.control.MousePosition({
    coordinateFormat: ol.coordinate.createStringXY(4),
    projection: 'EPSG:4326',
    className: 'custom-mouse-position',
    target: document.getElementById('mouse-position'),
});

function buildMap() {
    map = new ol.Map({
        controls: ol.control.defaults().extend([mousePositionControl]),
        target: 'map',
        layers: layers,
        view: new ol.View({
            center: ol.proj.fromLonLat([-45, -15]),
            zoom: 4
        })
    });
}

buildMap()