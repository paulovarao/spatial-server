package com.varaodev.spatialserver.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapPolygonTests {
	
	List<MapPoint> sampleShell = List.of(
			new MapPoint(-2,-2),
			new MapPoint(-2,2),
			new MapPoint(2,2),
			new MapPoint(2,-2),
			new MapPoint(-2,-2)
			);
	
	List<MapPoint> sampleHole = List.of(
			new MapPoint(0,0),
			new MapPoint(0,1),
			new MapPoint(1,1),
			new MapPoint(1,0),
			new MapPoint(0,0)
			);
	
	List<MapPoint> sampleHole2 = List.of(
			new MapPoint(0,0),
			new MapPoint(0,1),
			new MapPoint(2,1),
			new MapPoint(1,0),
			new MapPoint(0,0)
			);
	
	@Test
	void validation() {
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPolygon(new MapPoints(sampleShell.subList(0, 2)), null);
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPolygon(new MapPoints(List.of()), null);
		});
		
		assertThrows(NullPointerException.class, () -> {
			new MapPolygon(null, null);
		});
	}
	
	@Test
	void area() {
		double lin = Math.PI * 6378 / 180;
		
		MapPolygon polygon = new MapPolygon(new MapPoints(sampleShell), null);
		Polygon pol = polygon.wktGeometry();
		
		System.out.println(polygon.area());
		System.out.println(pol.getArea() * lin * lin);
		
		polygon = new MapPolygon(new MapPoints(sampleShell), List.of(new MapPoints(sampleHole)));
		pol = polygon.wktGeometry();
		
		System.out.println(polygon.area());
		System.out.println(pol.getArea() * lin * lin);
	}

}
