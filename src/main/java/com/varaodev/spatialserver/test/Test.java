package com.varaodev.spatialserver.test;

import java.util.List;

import com.varaodev.spatialserver.model.MapPoint;

public class Test {

	public static void main(String[] args) {
		MapPoint mp1 = new MapPoint(0, 0);
		
		List<MapPoint> list = mp1.pointRectangularBuffer(100, 100, 45);
		
		System.out.println(list);
		
//		list.stream().forEach(p -> System.out.println(mp1.distanceKm(p)));
		
		System.out.println(list.get(1).distanceKm(list.get(0)));
		System.out.println(list.get(1).distanceKm(list.get(2)));
	}

}
