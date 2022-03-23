package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPoints;
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
		
		Geometry fieldOfRegard = null;
		Geometry blindRegion = null;
		
		RangeIterator rangeIterator = new RangeIterator();
		RangeIterator blindRangeIterator = new RangeIterator();
		
		for (int i = 1; i < positions.size(); i++) {
			SatellitePosition p1 = positions.get(i-1);
			SatellitePosition p2 = positions.get(i);
			
			rangeIterator.update(p1, p2);
			Geometry range = rangeIterator.getRange(maxLookAngle);
			fieldOfRegard = i == 1 ? range : fieldOfRegard.union(range);
			
			if (minLookAngle > 0) {
				blindRangeIterator.update(p1, p2);
				Geometry blindRange = blindRangeIterator.getRange(minLookAngle);
				blindRegion = i == 1 ? blindRange : blindRegion.union(blindRange);
			}
		}
		if (blindRegion != null) fieldOfRegard = fieldOfRegard.difference(blindRegion);
		
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
		
		private SatellitePosition point1;
		private SatellitePosition point2;
		private List<MapPoint> previousRangePoints;
		private List<MapPoint> rangePoints;
		private double inclination;
		
		public void update(SatellitePosition point1, SatellitePosition point2) {
			this.point1 = point1;
			this.point2 = point2;
			inclination = point1.toMapPoint().inclinationAngle(point2.toMapPoint());			
		}
		
		public Geometry getRange(double lookAngle) {
			if (previousRangePoints == null) 
				previousRangePoints = point1.rangePoints(inclination, lookAngle);
			
			rangePoints = point2.rangePoints(inclination, lookAngle);
			StdPolygon region = rangeRegion(rangePoints, previousRangePoints);
			previousRangePoints = new ArrayList<>(rangePoints);
			return region.wktGeometry();
		}
		
		private StdPolygon rangeRegion(List<MapPoint> rangePoints1, List<MapPoint> rangePoints2) {
			List<MapPoint> regionPoints = new ArrayList<>(List.of(rangePoints1.get(0), rangePoints1.get(1), 
					rangePoints2.get(1), rangePoints2.get(0), rangePoints1.get(0)));
			MapPoints regionSet = new MapPoints(regionPoints);
			return standardPolygon(regionSet);
		}
		
	}

}
