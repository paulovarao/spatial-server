
function getArrayMean(array) {
	if (array.length == 1) return array[0];
	const max = array.reduce(function(a, b) {
		return Math.max(a, b);
	}), min = array.reduce(function(a, b) {
		return Math.min(a, b);
	});
	return (max + min)/2;
}

function getCenterCoordinate(coordinates) {
	const xArray = [];
	const yArray = [];
	for (let i = 0; i < coordinates.length; i++) {
		const c = coordinates[i];
		xArray.push(c[0]);
		yArray.push(c[1]);
	}
	const xMean = getArrayMean(xArray);
	const yMean = getArrayMean(yArray);
	return [xMean, yMean];
}

function getCenters(feature) {
	
	const geometry = feature.getGeometry();
	const geometryType = geometry.getType();

    function centroidsArray(isMatrix) {
        const centroids = [];
		const geoms = geometry.getCoordinates();
		for (let i = 0; i < geoms.length; i++) {
            const center = isMatrix ? getCenterCoordinate(geoms[i][0]) : getCenterCoordinate(geoms[i])
			centroids.push( center );
		}
		return centroids;
    }
	
	if (geometryType == 'Point') {
		return [geometry.getCoordinates()];
	}
	
	if (geometryType == 'MultiPoint') {
		return geometry.getCoordinates();
	}
	
	if (geometryType == 'LineString') {
		return [ getCenterCoordinate( geometry.getCoordinates() ) ];
	}
	
	if (geometryType == 'MultiLineString') {
		return centroidsArray(false);
	}
	
	if (geometryType == 'MultiPolygon') {
		return centroidsArray(true);
	}
	
	return [getCenterCoordinate(geometry.getCoordinates()[0])];
}

function featureStyle(feature, color) {

	const fillColor = Array.from(color)
	fillColor[3] = 0.2

	const style1 = new ol.style.Style({
		stroke: new ol.style.Stroke({ color, width: 2 }),
		fill: new ol.style.Fill({ color: fillColor })
	});
	
	const style2 = new ol.style.Style({
        image: new ol.style.Circle({
            radius: 5,
            fill: new ol.style.Fill({ color })
        }),
        geometry: new ol.geom.MultiPoint( getCenters(feature) )
    });
	
	return [style1, style2];

    return 
}