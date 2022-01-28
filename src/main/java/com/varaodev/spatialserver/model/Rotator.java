package com.varaodev.spatialserver.model;

import java.util.List;
import java.util.stream.Collectors;

public interface Rotator {
	
	MapPolygon getPolygon();
	
	default MapPolygon rotated(MapPoint centroid, double azimuthDeg, int rotationSense) {
		
		// rotate each shell point individually
		MapPoints shellPoints = rotatedPoints(getPolygon().getShell(), centroid, 
				azimuthDeg, rotationSense);

		// rotate each hole point individually for each hole
		List<MapPoints> holes = getPolygon().getHoles();
		if (holes != null && !holes.isEmpty()) {
			List<MapPoints> holePoints = holes.stream()
					.map(h -> rotatedPoints(h, centroid, azimuthDeg, rotationSense))
					.collect(Collectors.toList());
			return new MapPolygon(shellPoints, holePoints);
			
		}
		return new MapPolygon(shellPoints, null);
	}
	
	private MapPoints rotatedPoints(MapPoints polygonPoints, MapPoint centroid,
			double azimuthDeg, int rotationSense) {
		return new MapPoints( polygonPoints.getPoints().stream()
				.map(p -> p.mapRotation(centroid, azimuthDeg, rotationSense) )
				.collect(Collectors.toList()) );
	}

}
