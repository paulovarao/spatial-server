package com.varaodev.spatialserver.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varaodev.spatialserver.geo.SimpleEarth;
import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPolygon;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		// QGIS = 41.365
		
		MapPoint p1 = new MapPoint(0.004, 0.104);
		MapPoint p2 = new MapPoint(0.304, -0.106);
		
		System.out.println(p1.toSpacePoint());
		System.out.println(p2.toSpacePoint());
		
		System.out.println(p1.distanceKm(p2));
		
		double a = SimpleEarth.EQUATOR_AXIS_KM;
		double b = SimpleEarth.POLAR_AXIS_KM;
		
		double c = Math.sqrt(a*a - b*b);
		double e = c/a;
		
		System.out.println(e);
		
		MapPolygon polygon = new MapPolygon("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))");
		
		System.out.println(polygon.area());
		
	}
	
}
