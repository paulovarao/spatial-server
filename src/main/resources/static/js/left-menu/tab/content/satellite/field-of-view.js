function multiToSinglePolygon(polygon) {
    const features = [wktFormat.readFeature(polygon, mapControls.defaultProjection)]
    const sf = singleFeatures(features)
    return wktGeometries(sf)
}

async function flatEdges(coordinates, tle, lookAngle) {
    const firstAndLast = [coordinates[0], coordinates[coordinates.length - 1]]
    const edges = []
    for (let i in firstAndLast) {
        const c1 = firstAndLast[i]
        const r1 = await c1.getRange(lookAngle)

        const timeInterval = r1 * 250 // conversion factor
        const timeDifference = i == 0 ? -timeInterval : timeInterval
        const t = new Date(c1.time.getTime() + timeDifference)

        const c2 = getSatelliteCoordinates(tle, t)
        const r2 = await c2.getRange(lookAngle)

        const points = [c1.getPoint(), c2.getPoint()]
        const distanceInKm = Math.max(r1, r2) * 1.4

        const buffer = await lineBuffer(points, distanceInKm)
        multiToSinglePolygon(buffer[0]).forEach(p => edges.push(p))
    }
    return edges
}

async function computeBlindRegions(ranges, blindRegions, edges) {
    const blindRegion = await union(blindRegions)
    const brSinglePolArray = multiToSinglePolygon(blindRegion[0])

    const results = []
    for (let pol of multiToSinglePolygon(ranges[0])) {
        const array = [pol]
        brSinglePolArray.forEach(p => array.push(p))
        edges.forEach(e => array.push(e))

        const rp = await difference(array)
        multiToSinglePolygon(rp[0]).forEach(p => results.push(p))
    }
    const fov = await union(results)
    return fov
}

async function getFieldOfViews(coordinates, lookAngle, blindLookAngle, tle) {
    const rangePols = []
    const blindPols = []
    const numberOfAzimuths = 24

    for (let c of coordinates) {
        const range = await c.rangeBuffer(lookAngle, numberOfAzimuths)
        multiToSinglePolygon(range).forEach(sp => rangePols.push(sp))
        if (blindLookAngle > 0) {
            const blindRange = await c.rangeBuffer(blindLookAngle, numberOfAzimuths)
            multiToSinglePolygon(blindRange).forEach(sp => blindPols.push(sp))
        }
    }

    let fov = await union(rangePols)

    if (blindPols.length > 0) {
        // include line buffers to remove round edges
        const edges = await flatEdges(coordinates, tle, lookAngle)
        fov = computeBlindRegions(fov, blindPols, edges)
    }

    return fov
}

async function satelliteOpportunities(inputs) {
    const stepInSeconds = 1

    const timeArray = datetimeArray(inputs.begin, inputs.end, stepInSeconds)
    const tle = await getLatestTLE(inputs.satelliteId)

    const coordinates = timeArray.map(t => getSatelliteCoordinates(tle, t).getObject())

    return await fieldOfRegard(coordinates, inputs.maxLookAngle, inputs.minLookAngle)
    // const coordinates = timeArray.map(t => getSatelliteCoordinates(tle, t))
    // const result = await getFieldOfViews(coordinates, inputs.maxLookAngle, inputs.minLookAngle, tle)
    // return result
}

async function updateSatelliteFOV(event) {
    const inputs = requestInputs()
    const errorFound = checkInputs(inputs)
    if (!errorFound) {
        const satelliteOpp = await satelliteOpportunities(inputs)
        updateResult(event, satelliteOpp)
    }
}

function checkInputs(inputs) {
    const nullParamCheck = param => param == null || param == undefined || param === ''
    
    const lookAngleIntervalCheck = inputs => {
        const max = new Number(inputs.maxLookAngle), min = new Number(inputs.minLookAngle)
        const maxIsNull = nullParamCheck(max), minIsNull = nullParamCheck(min)
        const maxIsHigh = max > 60, maxIsLow = max <= 0, minIsHigh = min >= max, minIsLow = min < 0
        return maxIsNull || minIsNull || maxIsHigh || maxIsLow || minIsHigh || minIsLow
    }

    const timeSpan = numberOfDays => {
        const dayMilli = 24 * 60 * 60 * 1000, interval = numberOfDays * dayMilli
        const today = new Date(), max = new Date(today.getTime() + interval), min = new Date(today.getTime() - interval)
        return { max, min }
    }
    
    const periodIntervalCheck = inputs => {
        const begin = new Date(`${inputs.begin}Z`), end = new Date(`${inputs.end}Z`)
        const span = timeSpan(8)
        return nullParamCheck(inputs.begin) || nullParamCheck(inputs.end) || end.getTime() <= begin.getTime() 
            || begin.getTime() < span.min.getTime() || end.getTime() > span.max.getTime()
    }

    let error = null
    if (nullParamCheck(inputs.satelliteId)) error = 'Invalid satellite ID'
    else if (lookAngleIntervalCheck(inputs)) error = 'Invalid Look Angle interval'
    else if (periodIntervalCheck(inputs)) error = 'Invalid Time interval'

    const errorFound = error != null
    if (errorFound) errorAlert(error)
    return errorFound
}