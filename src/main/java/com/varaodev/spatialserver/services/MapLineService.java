package com.varaodev.spatialserver.services;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.varaodev.spatialserver.model.MapLine;
import com.varaodev.spatialserver.model.MapPoints;
import com.varaodev.spatialserver.model.StdPolygon;

@Service
public class MapLineService extends MapService {
	
	public List<StdPolygon> buffer(List<MapLine> lines, Double distanceInKm, Integer numberOfAzimuths) {
		nullParamCheck(lines, "Line list");
		nullParamCheck(distanceInKm, "Distance");
		nullParamCheck(numberOfAzimuths, "Number of azimuths");
		
		List<StdPolygon> results = new ArrayList<>();
		for (MapLine line : lines) {
			MapPoints buffered = line.buffer(distanceInKm, numberOfAzimuths); 
			results.add(standardPolygon(buffered));
		}
		
		return results;
	}

}
