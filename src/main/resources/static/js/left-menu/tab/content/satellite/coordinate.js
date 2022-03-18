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
