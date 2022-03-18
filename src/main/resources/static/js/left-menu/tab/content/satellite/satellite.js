async function getLatestTLE(catNumber) {
    const url = `${rootUrl}/satellite/tle/${catNumber}`
    const tle = await fetch(url).then(handleErrors)
        .then(response => response.text())
        .then(res => res.split('\r\n'))
        .then(data => {
            const tleData = {}
            tleData.satellite = data[0].trim()
            tleData.line1 = data[1]
            tleData.line2 = data[2]
            return tleData
        })
    return tle
}

function datetimeArray(begin, end, timeStepSec) {
    const times = []
    const beginDate = new Date(`${begin}Z`), endDate = new Date(`${end}Z`);
    const length = Math.floor((endDate.getTime() - beginDate.getTime()) / timeStepSec / 1000);
    for (let i = 0; i < length; i++) {
        const t = new Date(beginDate.getTime() + i * timeStepSec * 1000)
        times.push(t)
    }
    if (times[-1] != endDate) times.push(endDate)
    return times
}

function getSatelliteCoordinates(tle, time) {
    const satrec = satellite.twoline2satrec(tle.line1, tle.line2)
    const gmst = satellite.gstime(time);
    const positionAndVelocity = satellite.propagate(satrec, time);
    const positionEci = positionAndVelocity.position;
    const positionGd = satellite.eciToGeodetic(positionEci, gmst);

    const longitude = satellite.degreesLong(positionGd.longitude)
    const latitude = satellite.degreesLong(positionGd.latitude)

    return new SatelliteCoordinate(time, longitude, latitude, positionGd.height)
}

function multiToSinglePolygon(polygon) {
    const features = [wktFormat.readFeature(polygon, mapControls.defaultProjection)]
    const sf = singleFeatures(features)
    return wktGeometries(sf)
}

async function getFieldOfViews(coordinates, lookAngle) {
    const maxRangePols = []
    for (let c of coordinates) maxRangePols.push(await c.rangeBuffer(lookAngle, 16))
    
    const instantFOVs = []
    maxRangePols.forEach( pol => pol.forEach( p => multiToSinglePolygon(p).forEach(sp => instantFOVs.push(sp)) ) )
    return instantFOVs
}

async function union(polygons) {
    const url = `${rootUrl}/polygons/union`
    const result = await fetch(url, requestParams('POST', { polygons })).then(res => res.json())
    return result
}

async function satelliteOpportunities() {
    const inputs = requestInputs()
    const catNumber = inputs.satelliteId
    const maxLookAngleDeg = inputs.maxLookAngle, minLookAngleDeg = inputs.minLookAngle
    const begin = inputs.begin, end = inputs.end

    const stepInSeconds = 5

    const timeArray = datetimeArray(begin, end, stepInSeconds)
    const tle = await getLatestTLE(catNumber)

    const coordinates = timeArray.map(t => getSatelliteCoordinates(tle, t))
    for (let c of coordinates) await c.updateRadius()

    const maxPolygons = await getFieldOfViews(coordinates, maxLookAngleDeg)
    const minPolygons = await getFieldOfViews(coordinates, minLookAngleDeg)

    let result = await union(maxPolygons)

    if (minPolygons.length > 0) {
        const minRange = await union(minPolygons)

        // CORRECT THIS (iterate over arrays to consider all parts)
        const body = { polygons: [multiToSinglePolygon(result[0])[0], multiToSinglePolygon(minRange[0])[0]] }

        url = `${rootUrl}/polygons/difference`
        result = await fetch(url, requestParams('POST', body)).then(res => res.json())
    }

    return result
}

async function updateSatelliteOrbit(event) {
    const satelliteOpp = await satelliteOpportunities()
    updateResult(event, satelliteOpp)
}

