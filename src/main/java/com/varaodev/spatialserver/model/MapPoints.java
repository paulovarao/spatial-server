package com.varaodev.spatialserver.model;

import java.util.List;

public class MapPoints {
	
	private List<MapPoint> points;

	public MapPoints(List<MapPoint> points) {
		this.points = points;
	}

	public List<MapPoint> getPoints() {
		return points;
	}

}
