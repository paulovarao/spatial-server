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
        // .catch(errorAlert)
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
    for (let c of coordinates) maxRangePols.push(await c.rangeBuffer(lookAngle, 24))
    
    const instantFOVs = []
    maxRangePols.forEach( pol => pol.forEach( p => multiToSinglePolygon(p).forEach(sp => instantFOVs.push(sp)) ) )
    return instantFOVs
}

async function union(polygons) {
    const url = `${rootUrl}/polygons/union`
    const result = await fetch(url, requestParams('POST', { polygons })).then(res => res.json())
    return result
}

async function difference(polygons) {
    const url = `${rootUrl}/polygons/difference`
    const result = await fetch(url, requestParams('POST', { polygons })).then(res => res.json())
    return result
}

async function flatEdges(coordinates, tle, lookAngle) {
    const firstAndLast = [coordinates[0], coordinates[coordinates.length-1]]
    const edges = []
    for (let i in firstAndLast) {
        const c1 = firstAndLast[i]
        const r1 = c1.getRange(lookAngle)
        const timeInterval = r1 * 250 // conversion factor
        const timeDifference = i == 0 ? -timeInterval : timeInterval
        const t = new Date(c1.time.getTime() + timeDifference)

        const c2 = getSatelliteCoordinates(tle, t)
        await c2.updateRadius()
        const r2 = c2.getRange(lookAngle)

        const url = `${rootUrl}/points/line-buffer`
        const body = { 
            points: [c1.getPoint(), c2.getPoint()], 
            distanceInKm: Math.max(r1, r2) * 1.4
        }
        const lineBuffer = await fetch(url, requestParams('POST', body)).then(res => res.json())
        multiToSinglePolygon(lineBuffer[0]).forEach(p => edges.push(p))
    }
    return edges
}

async function satelliteOpportunities() {
    const inputs = requestInputs()
    const stepInSeconds = 1

    const timeArray = datetimeArray(inputs.begin, inputs.end, stepInSeconds)
    const tle = await getLatestTLE(inputs.satelliteId)

    const coordinates = timeArray.map(t => getSatelliteCoordinates(tle, t))
    for (let c of coordinates) await c.updateRadius()

    let result = await getFieldOfViews(coordinates, inputs.maxLookAngle).then(union)
    
    if (inputs.minLookAngle > 0) {
        const minRange = await getFieldOfViews(coordinates, inputs.minLookAngle).then(union)
        
        // include line buffers to remove round edges
        const edges = await flatEdges(coordinates, tle, inputs.maxLookAngle)

        const results = []
        for (let pol of multiToSinglePolygon(result[0])) {
            const array = [pol]
            multiToSinglePolygon(minRange[0]).forEach(p => array.push(p))
            edges.forEach(e => array.push(e))
            const rp = await difference(array)
            multiToSinglePolygon(rp[0]).forEach(p => results.push(p))
        }

        result = await union(results)
    }

    return result
}

async function updateSatelliteOrbit(event) {
    const satelliteOpp = await satelliteOpportunities()
    updateResult(event, satelliteOpp)
}

