package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class StdPolygon implements WktModel<MultiPolygon> {
	
	private List<MapPolygon> polygons;

	public StdPolygon(List<MapPolygon> polygons) {
		this.polygons = polygons == null ? new ArrayList<>() : polygons;
	}
	
	public StdPolygon(Geometry geometry) {
		validateGeometry(geometry);
		List<MapPolygon> polygons = IntStream.range(0, geometry.getNumGeometries()).boxed()
				.map(i -> new MapPolygon(geometry.getGeometryN(i).toString()))
				.collect(Collectors.toList());
		this.polygons = polygons;
	}

	public List<MapPolygon> getPolygons() {
		return polygons;
	}

	@Override
	public MultiPolygon wktGeometry() {
		Polygon[] polArray = polygons.stream().map(p -> p.wktGeometry()).toArray(Polygon[]::new);
		return factory().createMultiPolygon(polArray);
	}

	@Override
	public String validGeometryType() {
		return Geometry.TYPENAME_MULTIPOLYGON;
	}
	
	@Override
	public String toString() {
		return wktGeometry().toString();
	}
	
	private void validateGeometry(Geometry geometry) {
		String type = geometry.getGeometryType();
		if(!type.equals(Geometry.TYPENAME_POLYGON) && !type.equals(Geometry.TYPENAME_MULTIPOLYGON))
			throw new IllegalArgumentException("Failed to instantiate StdPolygon:"
					+ " geometry type must be polygonal.");
	}

}
