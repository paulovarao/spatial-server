package com.varaodev.spatialserver.model;

import java.time.Instant;
import java.util.List;

public class SatellitePosition {
	
	private Double longitude;
	private Double latitude;
	private Double altitude;
	private Instant time;
	
	public SatellitePosition() {}

	public SatellitePosition(Double longitude, Double latitude, Double altitude, Instant time) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.altitude = altitude;
		this.time = time;
	}

	public Double getLongitude() {
		return longitude;
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	
	public Double getAltitude() {
		return altitude;
	}
	
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}
	
	public Instant getTime() {
		return time;
	}
	
	public void setTime(Instant time) {
		this.time = time;
	}
	
	public MapPoint toMapPoint() {
		return new MapPoint(longitude, latitude);
	}
	
	public List<MapPoint> rangePoints(double inclination, double lookAngle) {
		double rangeAngle = satelliteRange(lookAngle);
		return toMapPoint().orthogonalPoints(rangeAngle, inclination);
	}
	
	private double satelliteRange(double lookAngle) {
        double laRad = lookAngle * MapPoint.DEGREES_TO_RADIANS;
        double radius = toMapPoint().radius();
        double angle = Math.asin(Math.sin(laRad) * (radius + altitude) / radius);
        return angle - laRad; // equivalent to 180 - larad - (180 - angle)
    }

	@Override
	public String toString() {
		return toMapPoint().wktGeometry().toString();
	}

}
