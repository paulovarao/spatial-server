package com.varaodev.spatialserver.test;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.varaodev.spatialserver.model.MapPoint;

public class Test {

	public static void main(String[] args) throws JsonProcessingException {
		
		Map<String, Object> obj = new LinkedHashMap<>();
		
		obj.put("attr1", 1);
		obj.put("attr2", "value2");
		
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(obj);
		
		System.out.println(obj.toString());
		System.out.println(json);
		
		MapPoint point = new MapPoint();
		
		System.out.println(point.toString());
	}
	
}
