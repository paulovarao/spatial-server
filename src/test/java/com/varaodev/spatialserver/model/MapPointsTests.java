package com.varaodev.spatialserver.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	
	// 02 edge crosses
	List<MapPoint> edge2 = List.of(
			new MapPoint(-178,2),
			new MapPoint(-178,3),
			new MapPoint(178, 2),
			new MapPoint(178,0),
			new MapPoint(-179,-1),
			new MapPoint(-179,0),
			new MapPoint(179,1)
			);
	
	// 01 edge cross (pole)
	List<MapPoint> edge3 = List.of(
			new MapPoint(-178,72),
			new MapPoint(-100,73),
			new MapPoint(-20, 74),
			new MapPoint(20,74),
			new MapPoint(100,73),
			new MapPoint(178,72),
			new MapPoint(-178,72)
			);
	
	// 03 edge crosses
	List<MapPoint> edge4 = List.of(
			new MapPoint(-175,11),
			new MapPoint(175,8),
			new MapPoint(175,6),
			new MapPoint(-177,4),
			new MapPoint(175,2),
			new MapPoint(175,0),
			new MapPoint(-175,3),
			new MapPoint(-175,5),
			new MapPoint(177,7),
			new MapPoint(-175,9)
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
		List<MapPoint> list = new ArrayList<>(edge4);
		list.add(list.get(0));
		MapPoints points = new MapPoints(list);
		System.out.println(points);
		
//		System.out.println(points.regroupedPoints());
		
		List<MapPolygon> polygons = points.regroupedPoints().stream()
				.map(mp -> new MapPolygon(new MapPoints(mp), null))
				.collect(Collectors.toList());
		System.out.println(new StdPolygon(polygons));
	}
	
//	@Test
	void toWktString() {
		MapPoints points = new MapPoints(samples);
		System.out.println(points);
	}

}
