package com.varaodev.spatialserver.test;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.Mosaic;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		List<MapPoint> mp1 = List.of(new MapPoint(0,0), new MapPoint(0,0.01), new MapPoint(0.01,0.01),
				new MapPoint(0.01, 0), new MapPoint(0,0));
		
		MapPolygon p1 = new MapPolygon(new MapPoints(mp1), null);
		
		Mosaic mosaic = new Mosaic(p1, 7.0, 4.0, 49.0, 0.0, 0.35);
		
		List<MapPolygon> tiles = mosaic.tiles();
		
		System.out.println(tiles);
	}
	
}
