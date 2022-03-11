package com.varaodev.spatialserver.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.varaodev.spatialserver.model.MapPolygon;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		MapPolygon polygon = new MapPolygon("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))");
		
		System.out.println(polygon.area());
		
	}
	
}
