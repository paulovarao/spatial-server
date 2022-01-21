package com.varaodev.spatialserver.model;

import java.util.List;

import org.locationtech.jts.geom.Polygon;

import com.varaodev.spatialserver.serializers.ObjectSerializer;

public class MapPolygon implements WktModel<Polygon>, ObjectSerializer {

	private MapPoints shell;
	private List<MapPoints>[] holes;
	
	@Override
	public Polygon wktGeometry() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
