package com.varaodev.spatialserver.test;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.SatellitePosition;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		SatellitePosition p1 = new SatellitePosition(-25.0, -10.0, 500.0, null);
		double inclination = Math.PI/36;
		List<MapPoint> points1 = p1.rangePoints(inclination, 35);
		List<MapPoint> points2 = p1.rangePoints(inclination, 10);
		
		System.out.println(new MapPoints(points1).getLineString());
		System.out.println(new MapPoints(points2).getLineString());
		
	}
	
}
