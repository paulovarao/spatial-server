package com.varaodev.spatialserver.resources;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.varaodev.spatialserver.model.MapPoint;

public class ElevationResourceTests extends ResourceTests {

	@Override
	protected String domain() {
		return "elevations";
	}
	
	@Test
	void elevation() throws Exception {		
		List<String> wktPoints = new ArrayList<>();
		wktPoints.add("POINT (-45 -15)");
		wktPoints.add("POINT (-47 -17)");
		wktPoints.add("POINT (-44 -14)");
		
		List<String> points = wktPoints.stream().map(w -> new MapPoint(w))
				.map(p -> p.latLonValue()).collect(Collectors.toList());
		String locations = IntStream.range(0, points.size()).boxed()
				.map(i -> i == 0 ? points.get(i) : "|" + points.get(i))
				.reduce("", (a,s) -> a+s);
		
		String resource = "/open-topo-data/";
		MockHttpServletRequestBuilder mockBuilder = defaultGetBuilder(resource + locations);
		performMock(mockBuilder, status().isOk());
	}
	
	protected MockHttpServletRequestBuilder defaultGetBuilder(String resource) {
		return get(endpoint + resource).contentType(MediaType.APPLICATION_JSON);
	}

}
