package com.varaodev.spatialserver.test;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.StdPolygon;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		List<MapPolygon> polygons = new ArrayList<>();
		polygons.add(new MapPolygon("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))"));
		polygons.add(new MapPolygon("POLYGON ((-1 -1, -1 1, 1 1, 1 -1, -1 -1))"));
		
		Geometry result = polygons.isEmpty() ? new StdPolygon(polygons).wktGeometry() 
				: polygons.get(0).wktGeometry();
		
		for (int i = 1; i < polygons.size(); i++) {
			result = result.intersection(polygons.get(i).wktGeometry());
		}
		
//		Geometry g0 = new StdPolygon(new ArrayList<>()).wktGeometry();
//		Geometry result = polygons.stream().map(p -> (Geometry) p.wktGeometry())
//				.reduce(g0, (i, g) -> i.union(g));
		
		System.out.println(result);
		
		System.out.println(new StdPolygon(new ArrayList<>()));
		
	}
	
}
