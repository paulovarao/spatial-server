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

public class MapPointResourceTests extends ResourceTests {

	@Override
	protected String domain() {
		return "points";
	}
	
	@Test
	void elevation() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/elevation");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> points = new ArrayList<>();
		points.add("POINT (-45 -15)");
		points.add("POINT (-47 -17)");
		points.add("POINT (-44 -14)");
		input.put("points", points);
		
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void circularBuffer() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/circular-buffer");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> points = new ArrayList<>();
		points.add("POINT (0 0)");
		points.add("POINT (1 0)");
		points.add("POINT (1 1)");
		input.put("points", points);
		input.put("distanceInKm", 10.0);
		input.put("numberOfAzimuths", 4);
		
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void rotation() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/rotation");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> points = new ArrayList<>();
		points.add("POINT (0 0)");
		points.add("POINT (1 0)");
		points.add("POINT (1 1)");
		input.put("points", points);
		input.put("centroid", "POINT (0 1)");
		input.put("angleInDegrees", 90.0);
		input.put("rotationSense", -1);
		
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void distanceInKm() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/distance");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> points = new ArrayList<>();
		points.add("POINT (0 0)");
		points.add("POINT (1 0)");
		points.add("POINT (1 1)");
		input.put("points", points);
		
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
		
		// Error testing
		builder = mockBuilder.content("[]");
		performMock(builder, status().isBadRequest());
		
		builder = mockBuilder.content("aksdj");
		performMock(builder, status().isBadRequest());
	}
	
	@Test
	void operationParams() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = 
				get(endpoint + "/params/rotation").contentType(MediaType.APPLICATION_JSON);
		
		performMock(mockBuilder, status().isOk());
	}
	
	@Test
	void operations() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = 
				get(endpoint + "/operations").contentType(MediaType.APPLICATION_JSON);
		
		performMock(mockBuilder, status().isOk());
	}

}
