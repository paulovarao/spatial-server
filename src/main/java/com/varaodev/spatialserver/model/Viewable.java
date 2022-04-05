package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Viewable {
	
	List<MapPoint> getPoints();
	
	default List<List<MapPoint>> regroupedPoints() {
		
		// add points to represent 180 degree meridian Edges in 2D map
		Formatter formatter = isRing() ? new RingFormatter(getPoints()) : new Formatter(getPoints());

		// group points in different edge sides as elements of a list
		List<List<MapPoint>> result = formatter.rearrangedPoints();
		
		return result;
	}
	
	// Private methods
	private boolean isRing() {
		List<MapPoint> points = getPoints();
		MapPoint first = points.get(0);
		MapPoint last = points.get(points.size()-1);
		return first.distance(last) < 1E-12;
	}
	
	static class RingFormatter extends Formatter {
		
		private Set<Set<Double>> validEdgeSet;

		public RingFormatter(List<MapPoint> points) {
			super(points);
			
			// if there is only one limit cross, 
			// then points are equivalent to pole region.
			if (edgeLatitudes.size() == 1) addPoleCornerPoints();
			
			// if there were edges, reorder points to begin in first limit
			if (viewPoints.size() > points.size()) reorderViewPoints();
		}
		
		@Override
		public List<List<MapPoint>> rearrangedPoints() {
			List<List<MapPoint>> groupList = super.rearrangedPoints();
			if(groupList.size() == 1) return groupList;
			
			List<List<MapPoint>> rearrangedPoints = new ArrayList<>();
			
			validEdgeSet = edgeSet(edgeLatitudes);
			
			while(groupList.size() > 0) {
				List<MapPoint> group = new ArrayList<>(groupList.get(0));
				groupList.remove(0);
				Set<Double> limitSet = limitSet(group);
				
				if (validEdgeSet.contains(limitSet)) rearrangedPoints.add(ringList(group));
				else {
					for (int i = 0; i < groupList.size(); i++) {
						List<MapPoint> g = new ArrayList<>(groupList.get(i));
						List<Double> ls = new ArrayList<>(limitSet(g));
						ls.addAll(limitSet);
						Set<Set<Double>> edgeSet = edgeSet(ls);
						
						if (validEdgeSet.containsAll(edgeSet)) {
							group.addAll(g);
							rearrangedPoints.add(ringList(group));
							groupList.remove(i);
							break;
						}
					}
				}
			}
			
			return rearrangedPoints;
		}
		
		private Set<Double> limitSet(List<MapPoint> group) {
			Set<Double> limitSet = new HashSet<>();
			limitSet.add(group.get(0).getY());
			limitSet.add(group.get(group.size()-1).getY());
			return limitSet;
		}
		
		private Set<Set<Double>> edgeSet(Collection<Double> edgeLatitudes) {
			Set<Set<Double>> resultSet = new HashSet<>();

			List<Double> edgeLatList = new ArrayList<>(edgeLatitudes);
			Collections.sort(edgeLatList);
			
			int setSize = edgeLatList.size() / 2;			
			for (int i = 0; i < setSize; i++) {
				Set<Double> edgeSet = new HashSet<>(edgeLatList.subList(2*i, 2*i+2));
				resultSet.add(edgeSet);
			}
			return resultSet;
		}
		
		private List<MapPoint> ringList(List<MapPoint> pointList) {
			// add first element to list
			List<MapPoint> ringList = new ArrayList<>(pointList);
			MapPoint first = ringList.get(0), last = ringList.get(ringList.size()-1);
			if (!first.equals(last)) ringList.add(first);
			return ringList;
		}

		private void reorderViewPoints() {
			// remove last element if is ring
			viewPoints.remove(viewPoints.size()-1);
			
			// start from limitPoint if ring
			int limitIndex = viewPoints.indexOf(limitPoint) + 1;
			
			// change order of list, beginning with first limit point
			List<MapPoint> reorderedPoints = new ArrayList<>();
			reorderedPoints.addAll(viewPoints.subList(limitIndex, viewPoints.size()));
			reorderedPoints.addAll(viewPoints.subList(0, limitIndex));
			viewPoints = reorderedPoints;
		}
		
		private void addPoleCornerPoints() {
			int limitIndex = viewPoints.indexOf(limitPoint) + 1;
			
			// define if it's south or north pole based on limitPoint latitude
			Double cornerLat = limitPoint.y < 0 ? -90.0 : 90.0;
			
			// calculate corners and middle point for 2D map
			MapPoint corner1 = new MapPoint(limitPoint.x, cornerLat);
			MapPoint middle = new MapPoint(0, cornerLat);
			MapPoint corner2 = corner1.invertLongitude();
			List<MapPoint> poleCornerPoints =  new ArrayList<>(List.of(corner1,middle,corner2));
			
			List<MapPoint> newList = new ArrayList<>(viewPoints.subList(0, limitIndex));
			newList.addAll(poleCornerPoints);
			newList.addAll(viewPoints.subList(limitIndex, viewPoints.size()));
			viewPoints = newList;
		}
	}
	
	
	static class Formatter {
		
		protected MapPoint limitPoint;
		protected List<Double> edgeLatitudes = new ArrayList<>();
		
		protected List<MapPoint> viewPoints = new ArrayList<>();
		
		public Formatter(List<MapPoint> points) {
			updateViewPoints(points);
		}
		
		public List<List<MapPoint>> rearrangedPoints() {
			List<List<MapPoint>> groupList = new ArrayList<>();
						
			List<MapPoint> group = new ArrayList<>();
			group.add(viewPoints.get(0));
			
			for (int i = 1; i < viewPoints.size(); i++) {
				MapPoint previous = viewPoints.get(i-1), current = viewPoints.get(i);
				
				if (previous.crossedMapLonLimit(current)) {
					// add a new group every time there is an edge cross
					groupList.add(new ArrayList<>(group));
					
					// reset group list
					group = new ArrayList<>();
				}
				group.add(current);
			}
			groupList.add(new ArrayList<>(group));
			return groupList;
		}

		protected void updateViewPoints(List<MapPoint> pointList) {
			
			// start filling new list with first point
			MapPoint previous = pointList.get(0);
			viewPoints.add(previous);
			
			// sweep list of points
			for (int i = 1; i < pointList.size(); i++) {
				MapPoint current = pointList.get(i);
				if (previous.crossedMapLonLimit(current)) {
					// calculates point at limit edge
					limitPoint = previous.limitPoint(current);
					MapPoint limitInverted = limitPoint.invertLongitude();
					
					edgeLatitudes.add(limitPoint.getY());
					
					// add limitPoint to list if not repeated
					if (!limitPoint.equals(previous)) viewPoints.add(limitPoint);
					
					// add limitInverted point to list if not repeated
					if (!limitInverted.equals(current)) viewPoints.add(limitInverted);
				}
				// add current point if not repeated
				if (!current.equals(previous)) viewPoints.add(current);
				previous = current;
			}
		}
	}

}
