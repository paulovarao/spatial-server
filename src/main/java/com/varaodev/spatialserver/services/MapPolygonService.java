package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.Mosaic;

@Service
public class MapPolygonService extends MapService {

	public List<MapPolygon> mosaic(List<MapPolygon> polygons, Double widthInKm, 
			Double minimumLengthInKm, Double maximumLengthInKm, Double azimuthInDegrees, Double overlapInKm) {
		nullParamCheck(polygons, "Polygon list");
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
	
	// union
	// intersection
	// area

}
