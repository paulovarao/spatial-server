package com.varaodev.spatialserver.resources;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "/geometry/")
public class GeometryTypesResource {
	
	public static final String[] WKT_TYPES = {"Point", "Line", "Polygon"};
	
	public static boolean isWkt(String type) {
		for (String wkt : WKT_TYPES) {
			if (type.contains(wkt)) return true;
		}
		return false;
	}
	
	@GetMapping("/types")
	public ResponseEntity<List<String>> wktTypes() {
		return ResponseEntity.ok().body(Arrays.asList(WKT_TYPES));
	}

}
