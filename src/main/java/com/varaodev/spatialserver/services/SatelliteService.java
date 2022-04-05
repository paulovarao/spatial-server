package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.SatellitePosition;
import com.varaodev.spatialserver.model.StdPolygon;

@Service
public class SatelliteService {
	
	public StdPolygon fieldOfRegard(List<SatellitePosition> positions, 
			double maxLookAngle, double minLookAngle) {
		nullParamCheck(positions, "Position list");
		nullParamCheck(maxLookAngle, "Look Angle (max)");
		nullParamCheck(minLookAngle, "Look Angle (min)");
		
		lookAngleCheck(maxLookAngle, minLookAngle);
		
		List<StdPolygon> result = new ArrayList<>();
		
		RangeIterator rangeIterator = new RangeIterator(maxLookAngle);
		RangeIterator blindRangeIterator = new RangeIterator(minLookAngle);
		
		double longitudeReference = positions.get(0).getLongitude();
		
		for (int i = 1; i < positions.size(); i++) {
			SatellitePosition p1 = positions.get(i-1);
			SatellitePosition p2 = positions.get(i);
			
			// Check if first position longitude was reached
			boolean crossedReference = p2.getLongitude() <= longitudeReference 
					&& p1.getLongitude() > longitudeReference;

			// Split polygons to avoid field of regard overlap
			if (crossedReference) {
				StdPolygon polygon = processRanges(rangeIterator, blindRangeIterator);
				result.add(polygon);
				
				rangeIterator = new RangeIterator(maxLookAngle);
				blindRangeIterator = new RangeIterator(minLookAngle);
			}
			
			rangeIterator.update(p1, p2);
			blindRangeIterator.update(p1, p2);
		}
		
		StdPolygon polygon = processRanges(rangeIterator, blindRangeIterator);
		result.add(polygon);
		
		List<MapPolygon> polygons = result.stream().flatMap(sp -> sp.getPolygons().stream())
				.collect(Collectors.toList());
		
		return new StdPolygon(polygons);
	}
	
	private StdPolygon processRanges(RangeIterator rangeIterator, RangeIterator blindRangeIterator) {
		blindRangeIterator.addOffset();
		
		Geometry range = null, blindRange = null;
		List<MapPolygon> rangePol = rangeIterator.rangeRegion().getPolygons();
		List<MapPolygon> blindPol = blindRangeIterator.rangeRegion().getPolygons();
		
		// Avoid topology exception thrown when overlaps occurs
		for (int i = 0; i < rangePol.size(); i++) {
			Geometry rp = rangePol.get(i).wktGeometry(); 
			range = i == 0 ? rp : range.union(rp);
			
			if (blindPol.size() > 0) {
				Geometry bp = blindPol.get(i).wktGeometry();
				blindRange = i == 0 ? bp : blindRange.union(bp);				
			}
		}
		
		Geometry fieldOfRegard = blindRange == null ? range : range.difference(blindRange);
		
		return new StdPolygon(fieldOfRegard);
	}
	
	private void lookAngleCheck(double maxLookAngle, double minLookAngle) {
		if (maxLookAngle <= 0) 
			throw new IllegalArgumentException("Invalid Look Angle (max):"
					+ " must be greater than zero.");
		
		if (maxLookAngle > 60) 
			throw new IllegalArgumentException("Invalid Look Angle (max):"
					+ " must be less than or equal to 60 degrees.");
		
		if (minLookAngle < 0) 
			throw new IllegalArgumentException("Invalid Look Angle (min):"
					+ " must be greater than or equal to zero.");
		
		if (minLookAngle >= maxLookAngle) 
			throw new IllegalArgumentException("Invalid Look Angle (min):"
					+ " must be less than look angle max (" + maxLookAngle + ").");
	}
	
	static class RangeIterator extends MapService {

		private double lookAngle;
		private List<MapPoint> rightPoints = new ArrayList<>();
		private List<MapPoint> leftPoints = new ArrayList<>();
		
		public RangeIterator(double lookAngle) {
			this.lookAngle = lookAngle;
		}
		
		public void update(SatellitePosition point1, SatellitePosition point2) {
			if (lookAngle > 0) {
				boolean listIsEmpty = rightPoints.isEmpty();
				
				double inclination = point1.toMapPoint().inclinationAngle(point2.toMapPoint());
				if (listIsEmpty) addPoints(point1.rangePoints(inclination, lookAngle));
				
				List<MapPoint> rangePoints = point2.rangePoints(inclination, lookAngle);
				addPoints(rangePoints);				
			}
		}
		
		public void addOffset() {
			if (lookAngle > 0) {
				updateOffsetPoints(rightPoints);
				updateOffsetPoints(leftPoints);
			}
		}
		
		public StdPolygon rangeRegion() {
			if (rightPoints.isEmpty()) {
				Geometry geom = new GeometryFactory().createMultiPolygon();
				return new StdPolygon(geom);
			}
			
			List<MapPoint> rangePoints = new ArrayList<>(rightPoints);
			
			int size = leftPoints.size(), offset = size -1;
			List<MapPoint> leftPointsDec = IntStream.range(0, size).boxed()
					.map(i -> leftPoints.get(offset - i)).collect(Collectors.toList());
			rangePoints.addAll(leftPointsDec);
			rangePoints.add(rangePoints.get(0));
			
			MapPoints points = new MapPoints(rangePoints);
			return standardPolygon(points);
		}
		
		private void updateOffsetPoints(List<MapPoint> points) {
			int lastIndex = points.size()-1;
			
			// offset is applied to guarantee range regions will not be connected
			MapPoint p1 = points.get(lastIndex-1), p2 = points.get(lastIndex);			
			MapPoint opEnd = offset(p2, p1);
			points.set(lastIndex, opEnd);
			
			p1 = points.get(0); p2 = points.get(1);
			MapPoint opBegin = offset(p1, p2);
			points.set(0, opBegin);
		}
		
		private void addPoints(List<MapPoint> rangePoints) {
			rightPoints.add(rangePoints.get(0));
			leftPoints.add(rangePoints.get(1));
		}
		
		private MapPoint offset(MapPoint target, MapPoint reference) {
			double deltaLon = target.getX() - reference.getX();
			double deltaLat = target.getY() - reference.getY();
			
			double factor = offsetFactor(deltaLon, deltaLat);
			
			deltaLon *= factor; deltaLat *= factor;
			
			return target.offset(deltaLon, deltaLat);
		}
		
		private double offsetFactor(double deltaLon, double deltaLat) {
			double dLon = Math.abs(deltaLon);
			double dLat = Math.abs(deltaLat);
			double ref = dLon == 0 ? dLat : dLat == 0 ? dLon : Math.min(dLon, dLat);
			
			return 0.5 / ref;
		}
		
	}

}
