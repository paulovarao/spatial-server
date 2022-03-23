package com.varaodev.spatialserver.services;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.varaodev.spatialserver.model.SatellitePosition;
import com.varaodev.spatialserver.model.StdPolygon;

@SpringBootTest
public class SatelliteServiceTests {
	
	@Autowired
	SatelliteService service;
	
	List<SatellitePosition> samples() {
		SatellitePosition p1 = new SatellitePosition(-25.0, -10.0, 500.0, null);
		SatellitePosition p2 = new SatellitePosition(-25.1, -10.5, 490.0, null);
		return new ArrayList<>(List.of(p1, p2));
	}
	
	@Test
	void fieldOfRegard() {
		List<SatellitePosition> positions = samples();
		StdPolygon result = service.fieldOfRegard(positions, 35.0, 10.0);
		System.out.println(result);
	}

}
