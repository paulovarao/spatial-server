package com.varaodev.spatialserver.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MapPointsTests {
	
	List<MapPoint> samples = List.of(
			new MapPoint(0,0),
			new MapPoint(1,0),
			new MapPoint(1,1),
			new MapPoint(0,1),
			new MapPoint(0,0)
			);
	
	List<MapPoint> nonSimple = List.of(
			new MapPoint(0,1),
			new MapPoint(0,3),
			new MapPoint(2,1),
			new MapPoint(2,1),
			new MapPoint(-1, 2) //
			);
	
	List<MapPoint> edge = List.of(
			new MapPoint(179,1),
			new MapPoint(178,3),
			new MapPoint(-179, 2),
			new MapPoint(-178,1)
			);
	
//	@Test
	void wktGeometry() {
		MapPoints points = new MapPoints(samples.subList(0, 3));
		System.out.println(points.wktGeometry());
		System.out.println(points.getLineString());
		System.out.println(points.getLinearRing());
		
		points = new MapPoints(samples);
		System.out.println(points.wktGeometry());
		System.out.println(points.getLineString());
		System.out.println(points.getLinearRing());
	}
	
//	@Test
	void fromWkt() {
		List<String> samples = List.of("MULTIPOINT ((0 0), (1 0), (1 1))",
				"LINEARRING (0 0, 1 0, 1 1, 0 0)");

		System.out.println(new MapPoints(samples.get(0)).wktGeometry());
		
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPoints(samples.get(1));
		});
	}
	
//	@Test
	void simple() {
		assertThrows(IllegalArgumentException.class, () -> {
			MapPoints points = new MapPoints(nonSimple);
			System.out.println(points.getLinearRing().isSimple());
			System.out.println(points);
			points.simplified();
		});
		
		List<MapPoint> ns = new ArrayList<>(nonSimple);
		ns.set(4, new MapPoint(-0.01, 1.01));
		MapPoints points = new MapPoints(ns).simplifiedMapPoints();;
		System.out.println(points.getLinearRing().isSimple());
		System.out.println(points);
		
		ns.set(4, new MapPoint(0, 1));
		points = new MapPoints(ns).simplifiedMapPoints();
		System.out.println(ns);
		System.out.println(points.getLinearRing().isSimple());
		System.out.println(points);
	}
	
	@Test
	void viewInMap() {
		MapPoints points = new MapPoints(edge);
		System.out.println(points);
		System.out.println(points.regroupedPoints());
	}
	
//	@Test
	void toWktString() {
		MapPoints points = new MapPoints(samples);
		System.out.println(points);
	}

}
