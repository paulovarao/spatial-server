const satelliteIdInput = document.getElementById("sat-id")
const maxLookAngleInput = document.getElementById("max-LA")
const minLookAngleInput = document.getElementById("min-LA")
const beginInput = document.getElementById("begin")
const endInput = document.getElementById("end")

const satelliteUpdateButton = document.getElementById("sat-update")

class SatelliteCoordinate {
    constructor(time, longitude, latitude, height) {
        this.time = time
        this.longitude = longitude
        this.latitude = latitude
        this.height = height
    }

    getPoint() {
        return `POINT(${this.longitude} ${this.latitude})`
    }

    async updateRadius() {
        const url = `${rootUrl}/points/radius`
        fetch(url, requestParams('POST', { points: [this.getPoint()] }))
        this.radius = await fetch(url, requestParams('POST', { points: [this.getPoint()] }))
            .then(handleErrors)
            .then(response => response.json())
            .then(res => res[0])
    }

    getRange(lookAngle) {
        const degreesToRadians = Math.PI / 180
        const laRad = lookAngle * degreesToRadians

        const angle = Math.asin(Math.sin(laRad) * (this.radius + this.height) / this.radius)
        return (angle - laRad) * this.radius // equivalent to 180 - larad - (180 - angle)
    }

    async rangeBuffer(lookAngle, azimuths) {
        const body = {
            points: [this.getPoint()],
            distanceInKm: this.getRange(lookAngle),
            numberOfAzimuths: azimuths
        }

        const url = `${rootUrl}/points/circular-buffer`
        const polygons = await fetch(url, requestParams('POST', body))
            .then(handleErrors)
            .then(response => response.json())

        return polygons
    }
}

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
    const features = [wktFormat.readFeature(polygon, defaultProjection)]
    const sf = singleFeatures(features)
    return wktGeometries(sf)
}

async function satelliteOpportunities() {
    const catNumber = satelliteIdInput.value
    const maxLookAngleDeg = maxLookAngleInput.value, minLookAngleDeg = minLookAngleInput.value
    const begin = beginInput.value, end = endInput.value

    const stepInSeconds = 5

    const timeArray = datetimeArray(begin, end, stepInSeconds)
    const tle = await getLatestTLE(catNumber)

    const coordinates = timeArray.map(t => getSatelliteCoordinates(tle, t))
    for (let c of coordinates) await c.updateRadius()

    const maxRangePols = []
    for (let c of coordinates) maxRangePols.push(await c.rangeBuffer(maxLookAngleDeg, 16))

    const minRangePols = []
    for (let c of coordinates) minRangePols.push(await c.rangeBuffer(minLookAngleDeg, 16))

    const maxPolygons = []
    const minPolygons = []

    maxRangePols.forEach( pol => pol.forEach( p => multiToSinglePolygon(p).forEach(sp => maxPolygons.push(sp)) ) )
    minRangePols.forEach( pol => pol.forEach( p => multiToSinglePolygon(p).forEach(sp => minPolygons.push(sp)) ) )

    let url = `${rootUrl}/polygons/union`
    let result = await fetch(url, requestParams('POST', { polygons: maxPolygons })).then(res => res.json())

    if (minPolygons.length > 0) {
        const minRange = await fetch(url, requestParams('POST', { polygons: minPolygons })).then(res => res.json())

        // CORRECT THIS (iterate over arrays to consider all parts)
        const body = { polygons: [multiToSinglePolygon(result[0])[0], multiToSinglePolygon(minRange[0])[0]] }

        url = `${rootUrl}/polygons/difference`
        result = await fetch(url, requestParams('POST', body)).then(res => res.json())
    }

    console.log(result)
}

satelliteUpdateButton.onclick = satelliteOpportunities

async function test() {
    satelliteIdInput.value = 29079
    maxLookAngleInput.value = 45
    minLookAngleInput.value = 10
    beginInput.value = '2022-03-11T17:15'
    endInput.value = '2022-03-11T17:20'
    
    satelliteOpportunities()
}

test()