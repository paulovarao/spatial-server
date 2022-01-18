package com.varaodev.spatialserver.test;

import java.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.varaodev.spatialserver.model.MapPoint;

public class Test {

	public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
		
		String json = "[\"POINT (0 0)\", \"POINT (1 0)\"]";
		
        MapPoint[] example = new ObjectMapper().readValue(json, MapPoint[].class);
        
		System.out.println(Arrays.asList(example));
	}
	
}
