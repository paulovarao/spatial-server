package com.varaodev.spatialserver.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;

public class SpacePoint extends Point {
	
	private static final long serialVersionUID = 1L;
	
	public SpacePoint() {
		super();
	}

	public SpacePoint(Coordinate c) {
		super(c);
	}

	public SpacePoint(double x, double y, double z) {
		super(x, y, z);
	}

	public MapPoint toMapPoint() {
		Double th = Math.atan2(getY(), getX());
		Double rh = Math.acos(getZ() / magnitude());
		return new MapPoint(th, Math.PI/2 - rh).toDegree();
	}
	
	// angles in radians
	public List<SpacePoint> azimuthRingBuffer(List<Double> angles) { 
		LinkedHashMap<Double, Double> map = new LinkedHashMap<>();
		int size = angles.size();
		for (int i = 0; i <= size; i++) {
			int index = i == size ? 0 : i;
			map.put(i*Math.PI*2/size, angles.get(index));
		}
		return buffer(map);
	}
	
	public List<SpacePoint> buffer(LinkedHashMap<Double, Double> azimuthAngleMap) { 
		List<SpacePoint> list = azimuthAngleMap.entrySet().stream()
				.map(e -> rotation(	azimuthPlaneVector(e.getKey()), e.getValue()) )
				.collect(Collectors.toList());
		return list;
	}
	
	public SpacePoint azimuthPlaneVector(Double azimuteRad) {
		SpacePoint axis = normalizedVector();
		SpacePoint zAxis = new SpacePoint(0.0,0.0,1.0);
		zAxis = axis.equals(zAxis) ? new SpacePoint(-1.0,0.0,0.0) : zAxis;
		SpacePoint ortho = axis.orthogonal(zAxis).normalizedVector();
		// clockwise rotation
		return ortho.rotation(axis.opposite(), azimuteRad).normalizedVector();
	}
	
	public double arcDistance(SpacePoint point) {
		return distance3D(point);
	}

	public SpacePoint orthogonal(SpacePoint p) {
		SpacePoint point = normalizedVector();
		SpacePoint ps = p.normalizedVector();
		Double xo = point.y * ps.getZ() - point.z * ps.getY();
		Double yo = point.z * ps.getX() - point.x * ps.getZ();
		Double zo = point.x * ps.getY() - point.y * ps.getX();
		return new SpacePoint(xo, yo, zo).normalizedVector();
	}
	
	public SpacePoint rotation(SpacePoint axis, double angleRad) {
		SpacePoint normAxis = axis.normalizedVector();
		Double ax = normAxis.getX(); Double ay = normAxis.getY(); Double az = normAxis.getZ();
		Double vx = getX(); Double vy = getY(); Double vz = getZ();
		
		Double k1 = Math.sin(angleRad);
		Double k2 = Math.cos(angleRad);
		Double k3 = 1 - k2;
		
		Double x = (k2 + ax*ax*k3)*vx + (ax*ay*k3 - az*k1)*vy + (ax*az*k3 + ay*k1)*vz;
		Double y = (ay*ax*k3 + az*k1)*vx + (k2 + ay*ay*k3)*vy + (ay*az*k3 - ax*k1)*vz;
		Double z = (az*ax*k3 - ay*k1)*vx + (az*ay*k3 + ax*k1)*vy + (k2 + az*az*k3)*vz;
		
		return new SpacePoint(x, y, z);
	}
	
	public double angle(SpacePoint p) { // in radians
		double num = getX()*p.x + getY()*p.y + getZ()*p.z;
		double den = magnitude()*p.magnitude();
		return Math.acos(num/den);
	}
	
	public SpacePoint normalizedVector() {
		Double mag = magnitude();
		return new SpacePoint(getX()/mag, getY()/mag, getZ()/mag);
	}
	
	public Double magnitude() {
		return Math.sqrt(getX()*getX() + getY()*getY() + getZ()*getZ());
	}

	public SpacePoint opposite() {
		return new SpacePoint(-getX(), -getY(), -getZ());
	}

}
