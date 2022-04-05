async function satelliteOpportunities(inputs) {

    const addTime = (date, seconds) => new Date(date.getTime() + seconds * 1000)
    
    const getPosition = timeMilli => getSatelliteCoordinates(tle, new Date(timeMilli))

    const stepInSeconds = 1

    // adds two points at the beginning and the end to avoid high inclination points at 180 deg longitude
    const begin = addTime(new Date(`${inputs.begin}Z`), -2 * stepInSeconds)
    const end = addTime(new Date(`${inputs.end}Z`), 2 * stepInSeconds)

    const tle = await getLatestTLE(inputs.satelliteId)
    
    const coordinates = []
    const timeStepMilli = stepInSeconds * 1000
    
    const length = Math.floor((end.getTime() - begin.getTime()) / timeStepMilli);
    for (let i = 0; i < length; i++) coordinates.push( getPosition(begin.getTime() + i * timeStepMilli) )

    if (coordinates[coordinates.length-1] != end) coordinates.push(getSatelliteCoordinates(tle, end))

    // console.log(coordinates.reduce((r, c) => 
    //     r == '' ? `${c.longitude},${c.latitude},${c.altitude}` : r + `;${c.longitude},${c.latitude},${c.altitude}`
    //     , ''))

    return await fieldOfRegard(coordinates, inputs.maxLookAngle, inputs.minLookAngle)
}

async function updateSatelliteFOV(event) {
    // try {
        const inputs = requestInputs()
        const errorFound = checkInputs(inputs)
        if (!errorFound) {
            const satelliteOpp = await satelliteOpportunities(inputs)
            updateResult(event, satelliteOpp)
        }
    // } catch (error) {
    //     errorAlert(error)
    // }
}

function checkInputs(inputs) {
    const nullParamCheck = param => param == null || param == undefined || param === ''

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
    else if (periodIntervalCheck(inputs)) error = 'Invalid Time interval'

    const errorFound = error != null
    if (errorFound) errorAlert(error)
    return errorFound
}