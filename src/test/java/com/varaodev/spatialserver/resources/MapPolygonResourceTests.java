package com.varaodev.spatialserver.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class MapPolygonResourceTests extends ResourceTests {

	@Override
	protected String domain() {
		return "polygons";
	}
	
	@Test
	void mosaic() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> polygons = new ArrayList<>();
		polygons.add("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
		input.put("polygons", polygons);
		input.put("widthInKm", 7.0);
		input.put("minimumLengthInKm", 4.0);
		input.put("maximumLengthInKm", 60.0);
		input.put("azimuthInDegrees", 0);
		input.put("overlapInKm", 0.35);
		
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/mosaic");
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void intersection() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> polygons = new ArrayList<>();
		polygons.add("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))");
		polygons.add("POLYGON ((-1 -1, -1 1, 1 1, 1 -1, -1 -1))");
		input.put("polygons", polygons);
		
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/intersection");
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void union() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> polygons = new ArrayList<>();
		polygons.add("POLYGON ((0 0, 0 2, 2 2, 2 0, 0 0))");
		polygons.add("POLYGON ((-1 -1, -1 1, 1 1, 1 -1, -1 -1))");
		input.put("polygons", polygons);
		
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/union");
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void simple() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> polygons = new ArrayList<>();
		polygons.add("POLYGON ((-2 -2, -2 2, 2 2, 2 -2, -2 -2),(0 0, 0 1, 1 1, 1 0, -0.01 0.01, 0 0))");
		polygons.add("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
		input.put("polygons", polygons);
		
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/simple");
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void area() throws Exception {
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> polygons = new ArrayList<>();
		polygons.add("POLYGON ((0 0, 0 1, 1 1, 1 0, 0 0))");
		input.put("polygons", polygons);
		
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/area");
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void operationParams() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = 
				get(endpoint + "/params/mosaic").contentType(MediaType.APPLICATION_JSON);
		
		performMock(mockBuilder, status().isOk());
	}

}
