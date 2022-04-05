package com.varaodev.spatialserver.services;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
		String data = "-150,26.45,500;170,42.8,500;130,42.8,500;90,26.45,500;50,0,500;10,-26.45,500;-30,-42.8,500;-70,-42.8,500;-110,-26.45,500;-150,0,500;170,26.45,500;130,42.8,500;90,42.8,500;50,26.45,500;10,0,500;-30,-26.45,500;-70,-42.8,500;-110,-42.8,500;-150,-26.45,500;170,0,500;130,26.45,500;90,42.8,500;50,42.8,500;10,26.45,500;-30,0,500;-70,-26.45,500;-110,-42.8,500;-150,-42.8,500;170,-26.45,500;130,0,500";
		String[] array = data.split(";");
		List<String> list = Arrays.asList(array);
		
		System.out.println(list.size());
		
		List<SatellitePosition> positions = list.stream()
				.map(s -> getPosition(s)).collect(Collectors.toList());
		return positions; // .subList(5, 8);
//		SatellitePosition p1 = new SatellitePosition(-179.9, 74.0, 500.0, null);
//		SatellitePosition p2 = new SatellitePosition(180.1, 74.05, 490.0, null);
//		return new ArrayList<>(List.of(p1, p2));
	}
	
	@Test
	void fieldOfRegard() {
		List<SatellitePosition> positions = samples();
		StdPolygon result = service.fieldOfRegard(positions, 45.0, 0.0);
		System.out.println(result);
	}
	
	private SatellitePosition getPosition(String data) {
		String[] array = data.split(",");
		Double longitude = Double.parseDouble(array[0]);
		Double latitude = Double.parseDouble(array[1]);
		Double altitude = Double.parseDouble(array[2]);
		return new SatellitePosition(longitude, latitude, altitude, null);
	}

}
