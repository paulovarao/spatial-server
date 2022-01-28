package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.List;

public interface Viewable {
	
	List<MapPoint> getPoints();
	
	default List<List<MapPoint>> regroupedPoints() {
		// add points to represent 180 degree meridian Edges in 2D map
		List<MapPoint> pointList = includeLimitPoints();

		// if there were edges, reorder points to begin in first limit
		if (pointList.size() != getPoints().size()) {
			// remove last element if is ring
			if (isRing()) pointList.remove(pointList.size()-1);
			
			pointList = reorderedList(pointList);
		}
		
		// group points in different edge sides as elements of a list
		return rearrangedPoints(pointList);
	}
	
	// Private methods
	private List<MapPoint> includeLimitPoints() {
		List<MapPoint> pointList = getPoints();
		
		// start filling new list with first point
		MapPoint previous = pointList.get(0);
		List<MapPoint> points = new ArrayList<>(List.of(previous));
		
		// compute the number of limit crosses
		int n = 0;
		for (int i = 1; i < pointList.size(); i++) 
			if (pointList.get(i-1).crossedMapLonLimit(pointList.get(i))) n++;
		
		// sweep list of points
		for (int i = 1; i < pointList.size(); i++) {
			MapPoint current = pointList.get(i);
			if (previous.crossedMapLonLimit(current)) {
				// calculates point at limit edge
				MapPoint limitPoint = previous.limitPoint(current);
				MapPoint limitInverted = limitPoint.invertLongitude();
				
				// add limitPoint to list if not repeated
				if (!limitPoint.equals(previous)) points.add(limitPoint);
				
				// if there is only one limit cross and points form a ring, 
				// then points are equivalent to pole region.
				// extra points must be added
				if (n == 1 && isRing()) points.addAll(poleCornerPointsList(limitPoint));
				
				// add limitInverted point to list if not repeated
				if (!limitInverted.equals(current)) points.add(limitInverted);
			}
			// add current point if not repeated
			if (!current.equals(previous)) points.add(current);
			previous = current;
		}
		return points;
	}
	
	private List<MapPoint> poleCornerPointsList(MapPoint limitPoint) {
		// define if it's south or north pole based on limitPoint latitude
		Double cornerLat = limitPoint.y < 0 ? -90.0 : 90.0;
		
		// calculate corners and middle point for 2D map
		MapPoint corner1 = new MapPoint(limitPoint.x, cornerLat);
		MapPoint middle = new MapPoint(0, cornerLat);
		MapPoint corner2 = corner1.invertLongitude();
		return new ArrayList<>(List.of(corner1,middle,corner2));
	}
	
	private List<MapPoint> reorderedList(List<MapPoint> pointList) {
		// find index of first limitPoint in list
		int index = firstLimitIndex(pointList);
		
		// change order of list, beginning with first limit point
		List<MapPoint> points = new ArrayList<>( pointList.subList(index, pointList.size()) );
		points.addAll(pointList.subList(0, index));
		return points;
	}
	
	private List<List<MapPoint>> rearrangedPoints(List<MapPoint> pointList) {
		List<List<MapPoint>> groupList = new ArrayList<>();
		List<MapPoint> subList = new ArrayList<>(List.of(pointList.get(0)));
		
		for (int i = 1; i < pointList.size(); i++) {
			subList = new ArrayList<>(subList);
			if (!pointList.get(i-1).crossedMapLonLimit(pointList.get(i)))
				subList.add(pointList.get(i));
			else {
				groupList.add(formattedList(subList));
				subList = List.of(pointList.get(i));
			}
		}
		groupList.add(formattedList(subList));
		return groupList;
	}
	
	private Integer firstLimitIndex(List<MapPoint> pointList) {
		for (int i = 0; i < pointList.size()-1; i++) {
			MapPoint curr = pointList.get(i);
			MapPoint next = pointList.get(i+1);
			if (Math.abs(curr.x) == 180 && !curr.crossedMapLonLimit(next))
					return i;
		}
		return null;
	}
	
	private List<MapPoint> formattedList(List<MapPoint> pointList) {
		if (isRing()) {
			// add first element to list
			List<MapPoint> newList = new ArrayList<>(pointList);
			if (!newList.get(0).equals(newList.get(newList.size()-1))) 
				newList.add(newList.get(0));
			return newList;
		}
		return pointList;
	}
	
	private boolean isRing() {
		List<MapPoint> list = getPoints();
		MapPoint first = list.get(0);
		MapPoint last = list.get(list.size()-1);
		return first.distance(last) < 1E-12;
	}

}
