package com.varaodev.spatialserver.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.Rounded;

@SpringBootTest
public class MapPointServiceTests {
	
	@Autowired
	MapPointService service;
	
	List<MapPoint> points = List.of(
			new MapPoint(0,0),
			new MapPoint(1,0),
			new MapPoint(1,1)
			);
	
	@Test
	void rotation() {
		List<Point> results = service.rotation(points, points.get(1), 90.0, -1);
		
		assertThat(new MapPoint(results.get(0).toString())).isEqualTo(new MapPoint(1,1));
		assertThat(new MapPoint(results.get(1).toString())).isEqualTo(new MapPoint(1,0));
		assertThat(new MapPoint(results.get(2).toString())).isEqualTo(new MapPoint(2,0));
		
		// Test exceptions
		assertThrows(NullPointerException.class, () -> {
			service.rotation(null, points.get(0), 90.0, 1);
		});
		
		assertThrows(NullPointerException.class, () -> {
			service.rotation(points, null, 90.0, 1);
		});
		
		assertThrows(NullPointerException.class, () -> {
			service.rotation(points, points.get(0), null, 1);
		});
		
		assertThrows(NullPointerException.class, () -> {
			service.rotation(points, points.get(0), 90.0, null);
		});
	}
	
	@Test
	void distanceInKm() {
		service.distance(points).stream().map(d -> Rounded.round(d, 0))
			.forEach(d -> assertThat(d).isEqualTo(111));
		
		// Test exceptions
		assertThrows(NullPointerException.class, () -> {
			service.distance(null);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			service.distance(List.of(points.get(0)));
		});
	}

}
