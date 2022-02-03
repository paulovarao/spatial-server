package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.Mosaic;
import com.varaodev.spatialserver.model.StdPolygon;

@Service
public class MapPolygonService extends MapService {
	
	private final String POLYGON_LIST_NAME = "Polygon list";

	public List<MapPolygon> mosaic(List<MapPolygon> polygons, Double widthInKm, 
			Double minimumLengthInKm, Double maximumLengthInKm, Double azimuthInDegrees, Double overlapInKm) {
		nullParamCheck(polygons, POLYGON_LIST_NAME);
		nullParamCheck(widthInKm, "Width");
		nullParamCheck(minimumLengthInKm, "Minimum length");
		nullParamCheck(maximumLengthInKm, "Maximum length");
		nullParamCheck(azimuthInDegrees, "Azimuth");
		nullParamCheck(overlapInKm, "Overlap");
		
		List<MapPolygon> results = new ArrayList<>();
		for (MapPolygon polygon : polygons) {
			Mosaic mosaic = new Mosaic(polygon, widthInKm, minimumLengthInKm, maximumLengthInKm, 
					azimuthInDegrees, overlapInKm);
			List<MapPolygon> tiles = mosaic.tiles();
			results.addAll(tiles);
		}
		return results;
	}

	public StdPolygon intersection(List<MapPolygon> polygons) {
		nullParamCheck(polygons, POLYGON_LIST_NAME);
		
		Geometry result = polygons.isEmpty() ? new StdPolygon(polygons).wktGeometry() 
				: polygons.get(0).wktGeometry();
		
		for (int i = 1; i < polygons.size(); i++) {
			result = result.intersection(polygons.get(i).wktGeometry());
		}
		
		return new StdPolygon(result);
	}

	public StdPolygon union(List<MapPolygon> polygons) {
		nullParamCheck(polygons, POLYGON_LIST_NAME);
		
		Geometry g0 = new StdPolygon(new ArrayList<>()).wktGeometry();
		Geometry result = polygons.stream().map(p -> (Geometry) p.wktGeometry())
				.reduce(g0, (i, g) -> i.union(g));
		
		return new StdPolygon(result);
	}
	
	public List<MapPolygon> simple(List<MapPolygon> polygons) {
		nullParamCheck(polygons, POLYGON_LIST_NAME);
		
		List<MapPolygon> simplified = polygons.stream().map(MapPolygon::simple)
				.collect(Collectors.toList());
		return simplified;
	}
	
	public List<Double> area(List<MapPolygon> polygons) {
		nullParamCheck(polygons, POLYGON_LIST_NAME);
		
		List<Double> results = polygons.stream().map(MapPolygon::area).collect(Collectors.toList());
		return results;
	}

}
