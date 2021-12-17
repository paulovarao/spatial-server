package com.varaodev.spatialserver.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MapPointResourceTests extends ResourceTests {

	@Override
	protected String domain() {
		return "points";
	}
	
	@Test
	void rotation() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/rotation");
		
		JsonObject input = new JsonObject();
		JsonArray points = new JsonArray();
		points.add("POINT (0 0)");
		points.add("POINT (1 0)");
		points.add("POINT (1 1)");
		input.add("points", points);
		input.addProperty("centroid", "POINT (0 1)");
		input.addProperty("angleDeg", 90.0);
		input.addProperty("rotationSense", -1);
		
		RequestBuilder builder = mockBuilder.content(input.toString());
		performMock(builder, status().isOk());
	}
	
	@Test
	void distanceInKm() throws Exception {
		MockHttpServletRequestBuilder mockBuilder = defaultBuilder("/distance");
		
		RequestBuilder builder = mockBuilder.content("[\"POINT (0 0)\", \"POINT (1 0)\"]");
		performMock(builder, status().isOk());
		
		// Error testing
		builder = mockBuilder.content("[]");
		performMock(builder, status().isBadRequest());
		
		builder = mockBuilder.content("aksdj");
		performMock(builder, status().isBadRequest());
	}
	
	private MockHttpServletRequestBuilder defaultBuilder(String resource) {
		return get(endpoint + resource).contentType(MediaType.APPLICATION_JSON);
	}

}
