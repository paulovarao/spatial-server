package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;

public class MapPoints implements WktModel<MultiPoint>, Simplifiable, Viewable {
	
	private List<MapPoint> points;

	public MapPoints(List<MapPoint> points) {
		this.points = new ArrayList<>(points);
	}
	
	public MapPoints(String wkt) {
		Geometry geometry = geometry(wkt);
		points = getMapPoints(geometry);
	}

	@Override
	public List<MapPoint> getPoints() {
		return points;
	}
	
	public LineString getLineString() {
		Coordinate[] array = points.toArray(new Coordinate[0]);
		return factory().createLineString(array);
	}
	
	public MapPoints simplifiedMapPoints() {
		return new MapPoints(simplified());
	}
	
	@Override
	public MultiPoint wktGeometry() {
		List<Point> pointList = points.stream().map(MapPoint::wktGeometry)
				.collect(Collectors.toList());
		return factory().createMultiPoint(pointList.toArray(new Point[0]));
	}
	
	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_MULTIPOINT;
	}
	
	@Override
	public String toString() {
		return wktGeometry().toString();
	}

}
