package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.listSizeCheck;
import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.StdPolygon;

@Service
public class MapPointService extends MapService {
	
	private final String POINT_LIST_NAME = "Point list";
	private final String DISTANCE_KM_NAME = "Distance";
	
	public List<StdPolygon> rectangularBuffer(List<MapPoint> points, Double widthInKm,
			Double lengthInKm, Double azimuthInDegrees) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(widthInKm, "Width");
		nullParamCheck(lengthInKm, "Length");
		nullParamCheck(azimuthInDegrees, "Azimuth angle");
		
		List<StdPolygon> results = new ArrayList<>();
		for (MapPoint point : points) {
			MapPoints buffered = point.pointRectangularBuffer(widthInKm, lengthInKm, azimuthInDegrees); 
			results.add(standardPolygon(buffered));
		}
		return results;
	}
	
	public List<StdPolygon> lineBuffer(List<MapPoint> points, Double distanceInKm) {
		nullParamCheck(points, POINT_LIST_NAME);
		listSizeCheck(points);
		nullParamCheck(distanceInKm, DISTANCE_KM_NAME);
		
		List<StdPolygon> results = new ArrayList<>();
		for (int i = 1; i < points.size(); i++) {
			MapPoint p0 = points.get(i);
			MapPoint p1 = points.get(i-1);
			MapPoints buffered = p0.lineRectangularBuffer(p1, distanceInKm); 
			results.add(standardPolygon(buffered));
		}
		return results;
	}
	
	public List<StdPolygon> circularBuffer(List<MapPoint> points, Double distanceInKm,
			Integer numberOfAzimuths) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(distanceInKm, DISTANCE_KM_NAME);
		nullParamCheck(numberOfAzimuths, "Number of azimuths");
		
		List<StdPolygon> results = new ArrayList<>();
		for (MapPoint point : points) {
			MapPoints buffered = point.circularBuffer(distanceInKm, numberOfAzimuths);
			results.add(standardPolygon(buffered));
		}
		return results;
	}
	
	public List<Point> rotation(List<MapPoint> points, MapPoint centroid,
			Double angleInDegrees, Integer rotationSense) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(centroid, "Centroid");
		nullParamCheck(angleInDegrees, "Rotation angle");
		nullParamCheck(rotationSense, "Rotation sense");
		
		List<Point> results = new ArrayList<>();
		for (MapPoint point : points) {
			MapPoint rotated = point.mapRotation(centroid, angleInDegrees, rotationSense);
			Point wktPoint = rotated.wktGeometry();
			results.add(wktPoint);
		}
		return results;
	}
	
	public List<Double> distance(List<MapPoint> points) { // in km
		nullParamCheck(points, POINT_LIST_NAME);
		listSizeCheck(points);
		
		List<Double> results = new ArrayList<>();
		for (int i = 1; i < points.size(); i++) {
			MapPoint p0 = points.get(i); MapPoint p1 = points.get(i-1); 
			results.add(p0.distanceKm(p1));
		}
		return results;
	}
	
	public List<Double> radius(List<MapPoint> points) {
		nullParamCheck(points, POINT_LIST_NAME);
		
		return points.stream().map(MapPoint::radius).collect(Collectors.toList());
	}

}
