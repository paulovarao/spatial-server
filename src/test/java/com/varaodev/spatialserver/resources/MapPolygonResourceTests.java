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
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/mosaic");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> lines = new ArrayList<>();
		lines.add("POLYGON ((0 0, 0 0.01, 0.01 0.01, 0.01 0, 0 0))");
		input.put("polygons", lines);
		input.put("widthInKm", 7.0);
		input.put("minimumLengthInKm", 4.0);
		input.put("maximumLengthInKm", 60.0);
		input.put("azimuthInDegrees", 0);
		input.put("overlapInKm", 0.35);
		
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
