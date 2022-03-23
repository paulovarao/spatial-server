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

    getObject() {
        return { longitude: this.longitude, 
            latitude: this.latitude, 
            time: this.time, altitude: this.height }
    }

    async updateRadius() {
        const radiusArray = await radius([this.getPoint()])
        this.radius = radiusArray[0]
    }

    async getRange(lookAngle) {
        if (!this.radius) await this.updateRadius()
        
        const degreesToRadians = Math.PI / 180
        const laRad = lookAngle * degreesToRadians

        const angle = Math.asin(Math.sin(laRad) * (this.radius + this.height) / this.radius)
        return (angle - laRad) * this.radius // equivalent to 180 - larad - (180 - angle)
    }

    async rangeBuffer(lookAngle, azimuths) {
        const range = await this.getRange(lookAngle)
        const polygons = await circularBuffer([this.getPoint()], range, azimuths)
        return polygons[0]
    }
}
