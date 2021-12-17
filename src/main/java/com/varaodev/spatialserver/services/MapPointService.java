package com.varaodev.spatialserver.services;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPoint;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.*;

@Service
public class MapPointService {
	
	private final String POINT_LIST_NAME = "Point list";
	private final String DISTANCE_KM_NAME = "Distance";
	
	public List<MultiPoint> rectangularBuffer(List<MapPoint> points, Double widthKm,
			Double lengthKm, Double azimuthDeg) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(widthKm, "Width");
		nullParamCheck(lengthKm, "Length");
		nullParamCheck(azimuthDeg, "Azimuth angle");
		
		List<MultiPoint> results = new ArrayList<>();
		for (MapPoint point : points) {
			List<MapPoint> buffered = point.pointRectangularBuffer(widthKm, lengthKm, azimuthDeg); 
			MultiPoint wktPoint = wktMultiPoint(buffered);
			results.add(wktPoint);
		}
		return results;
	}
	
	public List<MultiPoint> lineBuffer(List<MapPoint> points, Double distanceKm) {
		nullParamCheck(points, POINT_LIST_NAME);
		listSizeCheck(points);
		nullParamCheck(distanceKm, DISTANCE_KM_NAME);
		
		List<MultiPoint> results = new ArrayList<>();
		for (int i = 1; i < points.size(); i++) {
			MapPoint p0 = points.get(i);
			MapPoint p1 = points.get(i-1);
			List<MapPoint> buffered = p0.lineRectangularBuffer(p1, distanceKm); 
			MultiPoint wktPoint = wktMultiPoint(buffered);
			results.add(wktPoint);
		}
		return results;
	}
	
	public List<MultiPoint> circularBuffer(List<MapPoint> points, Double distanceKm,
			Integer numberOfAzimuths) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(distanceKm, DISTANCE_KM_NAME);
		nullParamCheck(numberOfAzimuths, "Number of azimuths");
		
		List<MultiPoint> results = new ArrayList<>();
		for (MapPoint point : points) {
			List<MapPoint> buffered = point.circularBuffer(distanceKm, numberOfAzimuths);
			MultiPoint wktPoint = wktMultiPoint(buffered);
			results.add(wktPoint);
		}
		return results;
	}
	
	public List<Point> rotation(List<MapPoint> points, MapPoint centroid,
			Double angleDeg, Integer rotationSense) {
		nullParamCheck(points, POINT_LIST_NAME);
		nullParamCheck(centroid, "Centroid");
		nullParamCheck(angleDeg, "Rotation angle");
		nullParamCheck(rotationSense, "Rotation sense");
		
		List<Point> results = new ArrayList<>();
		for (MapPoint point : points) {
			MapPoint rotated = point.mapRotation(centroid, angleDeg, rotationSense);
			Point wktPoint = rotated.wktGeometry();
			results.add(wktPoint);
		}
		return results;
	}
	
	public List<Double> distanceInKm(List<MapPoint> points) {
		nullParamCheck(points, DISTANCE_KM_NAME);
		listSizeCheck(points);
		
		List<Double> results = new ArrayList<>();
		for (int i = 1; i < points.size(); i++) {
			MapPoint p0 = points.get(i); MapPoint p1 = points.get(i-1); 
			results.add(p0.distanceKm(p1));
		}
		return results;
	}
	
	// private methods
	private MultiPoint wktMultiPoint(List<MapPoint> points) {
		GeometryFactory factory = new GeometryFactory();
		Point[] array = points.stream().map(MapPoint::wktGeometry).toArray(Point[]::new);
		return factory.createMultiPoint(array);
	}

}
