package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.TopologyException;
import org.locationtech.jts.simplify.DouglasPeuckerSimplifier;

public interface Simplifiable {
	
	List<MapPoint> getPoints();
	
	// method for simplifying geometry: reduce number of points and/or transform to simple
	default List<MapPoint> simplified() {
		
		if (getLinearRing().isSimple() && !hasRepeatedPoints()) return getPoints();
		
		Geometry simplified = simpleGeometry();

		if (!simplified.isSimple())
			throw new IllegalArgumentException("Could not simplify geometry points.");

		double ratio = intersection(simplified);
		if (ratio <= 0.95)
			throw new IllegalArgumentException("Simplified geometry is significantly different from "
					+ "original: intersection ratio " + ratio);

		return getMapPoints(simplified);
	}
	
	default List<MapPoint> getMapPoints(Geometry geometry) {
		List<Coordinate> coordinates = Arrays.asList(geometry.getCoordinates());
		return coordinates.stream().map(c -> new MapPoint(c)).collect(Collectors.toList());
	}
	
	default LinearRing getLinearRing() {
		return new GeometryFactory().createLinearRing(getRing().toArray(new MapPoint[0]));
	}
	
	default List<MapPoint> getRing() {
		List<MapPoint> list = getPoints();
		int lastIndex = list.size()-1;
		MapPoint firstElement = list.get(0);
		MapPoint lastElement = list.get(lastIndex);
		if (!firstElement.equals(lastElement)) list.add(firstElement);
		return list;
	}
	
	// Private methods
	private Geometry simpleGeometry() {
		Geometry ring = getLinearRing();
		double distance = referenceDistance();
		for (int i = 0; i < 1000; i++) {
			ring = DouglasPeuckerSimplifier.simplify(ring, distance);
			if (ring.isSimple()) break;
			distance += distance;
		}
		return ring;
	}
	
	private boolean hasRepeatedPoints() {
		List<MapPoint> list = getPoints();
		if (list != null && !list.isEmpty()) {
			for (int i = 1; i < list.size(); i++) {
				MapPoint p = list.get(i);
				if (p.equals(list.get(i-1))) return true;
			}
		}
		return false;
	}
	
	private double referenceDistance() {
		List<Double> list = new ArrayList<>();
		List<MapPoint> points = getPoints();
		for (int i = 1; i < points.size(); i++) {
			MapPoint p1 = points.get(i-1);
			MapPoint p2 = points.get(i);
			list.add(p1.distance(p2));
		}
		
		// reference distance corresponds to minimum distance between points times factor
		return list.stream().mapToDouble(d -> d).filter(d -> d != 0).min().getAsDouble() / 2;
	}
	
	private double intersection(Geometry simplified) {
		Geometry ring = getLinearRing();
		try {
			return intersectionRatio(ring, simplified);
		} catch (TopologyException e) {
			return intersectionRatio(ring.getEnvelope(), 
					simplified.getEnvelope());
		}
	}
	
	private double intersectionRatio(Geometry main, Geometry other) {
		Geometry intersection = main.intersection(other);
		return intersection.getArea()/main.getArea();
	}

}
