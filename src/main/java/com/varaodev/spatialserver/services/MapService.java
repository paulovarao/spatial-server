package com.varaodev.spatialserver.services;

import java.util.List;
import java.util.stream.Collectors;

import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.StdPolygon;

public abstract class MapService {
	
	protected StdPolygon standardPolygon(MapPoints input) {
		List<MapPolygon> polygons = input.regroupedPoints().stream()
				.map(mp -> new MapPolygon(new MapPoints(mp), null))
				.collect(Collectors.toList());
		return new StdPolygon(polygons);
	}

}
