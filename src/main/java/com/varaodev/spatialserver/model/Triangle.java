package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;

public class Triangle implements WktModel<Polygon> {
	
	private MapPoints points;
	
	public Triangle(MapPoints points) {
		if (points == null || points.getPoints() == null)
			throw new IllegalArgumentException("Invalid Triangle: points can't be null.");
		
		if (points.getPoints().size() != 3)
			throw new IllegalArgumentException("Invalid Triangle: size must be 3.");
		
		if (!hasValidCoordinates(points.getPoints()))
			throw new IllegalArgumentException("Invalid Triangle: coordinates must be different.");
	}
	
	public Triangle(String wkt) {
		Polygon polygon = geometry(wkt);
		LinearRing ring = polygon.getExteriorRing();
		List<MapPoint> points = Arrays.asList(ring.getCoordinates()).stream()
				.map(c -> new MapPoint(c)).collect(Collectors.toList());
		this.points = new MapPoints(points);
	}
	
	public MapPoints getPoints() {
		return points;
	}
	
	public double area() {
		List<Double> distances = distancesBetweenPoints();
		// Calculates the area of a triangle given the 03 sides/distances of it
		double p = distances.stream().reduce(0.0, (a,d) -> a+d)/2;
		double k = distances.stream().map(d -> p - d).reduce(1.0, (a,d) -> a*d);
		return Math.sqrt(p*k);
	}

	@Override
	public Polygon wktGeometry() {
		return factory().createPolygon(points.getLinearRing());
	}

	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_POLYGON;
	}
	
	private List<Double> distancesBetweenPoints() {
		List<Double> result = new ArrayList<>();
		List<MapPoint> list = points.getPoints();
		for (int i = 0; i < 3; i++) {
			MapPoint p0 = list.get(i);
			MapPoint p1 = i == 2 ? list.get(0) : list.get(i+1);
			result.add(p0.distanceKm(p1));
		}
		return result;
	}
	
	private boolean hasValidCoordinates(List<MapPoint> points) {
		boolean b1 = !points.get(0).equals(points.get(1));
		boolean b2 = !points.get(0).equals(points.get(2));
		boolean b3 = !points.get(1).equals(points.get(2));
		return  b1 && b2 && b3;
	}

}
