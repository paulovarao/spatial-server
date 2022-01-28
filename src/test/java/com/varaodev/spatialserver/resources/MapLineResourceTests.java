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

public class MapLineResourceTests extends ResourceTests {

	@Override
	protected String domain() {
		return "lines";
	}
	
	@Test
	void buffer() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/buffer");
		
		Map<String, Object> input = new LinkedHashMap<>();
		List<String> lines = new ArrayList<>();
		lines.add("LINESTRING (0 0, 0 1, 1 1)");
		input.put("lines", lines);
		input.put("distanceInKm", 10.0);
		input.put("numberOfAzimuths", 16);
		
		RequestBuilder builder = mockBuilder.content(toJson(input));
		performMock(builder, status().isOk());
	}
	
	@Test
	void operationParams() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = 
				get(endpoint + "/params/buffer").contentType(MediaType.APPLICATION_JSON);
		
		performMock(mockBuilder, status().isOk());
	}

}
