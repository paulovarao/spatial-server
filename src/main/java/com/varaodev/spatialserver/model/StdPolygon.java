package com.varaodev.spatialserver.model;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;

public class StdPolygon implements WktModel<MultiPolygon> {
	
	private List<MapPolygon> polygons;

	public StdPolygon(List<MapPolygon> polygons) {
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

}
