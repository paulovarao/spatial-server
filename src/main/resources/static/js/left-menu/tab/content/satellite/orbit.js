async function getLatestTLE(catNumber) {
    const url = `${rootUrl}/satellite/tle/${catNumber}`
    const tle = await fetch(url).then(handleErrors)
        .then(response => response.text())
        .then(res => res.split('\r\n'))
        .then(data => {
            if (data == 'No GP data found') errorAlert(`No satellite found for id ${catNumber}`)
            else {
                const tleData = {}
                tleData.satellite = data[0].trim()
                tleData.line1 = data[1]
                tleData.line2 = data[2]
                return tleData
            }
        })
        .catch(errorAlert)
    return tle
}

function getSatelliteCoordinates(tle, time) {
    const satrec = satellite.twoline2satrec(tle.line1, tle.line2)
    const gmst = satellite.gstime(time);
    const positionAndVelocity = satellite.propagate(satrec, time);
    const positionEci = positionAndVelocity.position;
    const positionGd = satellite.eciToGeodetic(positionEci, gmst);

    const longitude = satellite.degreesLong(positionGd.longitude)
    const latitude = satellite.degreesLong(positionGd.latitude)

    return {
        time, longitude, latitude, altitude: positionGd.height
    }
}
