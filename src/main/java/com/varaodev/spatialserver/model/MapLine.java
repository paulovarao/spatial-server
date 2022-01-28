package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Polygon;

import com.varaodev.spatialserver.serializers.ObjectSerializer;

public class MapLine implements WktModel<LineString>, ObjectSerializer {
	
	private MapPoints points;

	public MapLine(MapPoints points) {
		this.points = points;
	}

	public MapLine(String wkt) {
		LineString line = geometry(wkt);
		List<MapPoint> list = Arrays.asList(line.getCoordinates()).stream().map(c -> new MapPoint(c))
				.collect(Collectors.toList());
		this.points = new MapPoints(list);
	}

	public MapPoints getPoints() {
		return points;
	}
	
	public MapPoints buffer(double distanceKm, int numAzimuths) {
		List<MapPoints> bufferList = new ArrayList<>();
		List<MapPoint> list = points.getPoints();
		for (int i = 1; i < list.size(); i++) {
			MapPoint p1 = list.get(i-1);
			MapPoint p2 = list.get(i);
			
			// Point buffer (first point)
			if (i == 1) bufferList.add( p1.circularBuffer(distanceKm, numAzimuths) );
			
			// Line buffer
			bufferList.add( p1.lineRectangularBuffer(p2, distanceKm) );
			
			// Point buffer
			bufferList.add( p2.circularBuffer(distanceKm, numAzimuths) );
		}
		return union(bufferList);
	}

	@Override
	public LineString wktGeometry() {
		MapPoint[] pointArray = points.getPoints().stream().toArray(MapPoint[]::new);
		return factory().createLineString(pointArray);
	}

	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_LINESTRING;
	}
	
	@Override
	public String toString() {
		return wktGeometry().toString();
	}
	
	private MapPoints union(List<MapPoints> list) {
		Geometry geom = getPolygon(list.get(0));
		for (MapPoints mp : list) geom = geom.union(getPolygon(mp));
		List<MapPoint> mapPoints = Arrays.asList(geom.getCoordinates()).stream()
				.map(c -> new MapPoint(c)).collect(Collectors.toList());
		return new MapPoints(mapPoints);
	}
	
	private Polygon getPolygon(MapPoints points) {
		return new MapPolygon(points, null).wktGeometry();
	}

}
