package com.varaodev.spatialserver.resources;

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
	
	@GetMapping("/types")
	public ResponseEntity<List<String>> rotationInputParams() {
		List<String> results = List.of(
				"Point"
				);
		return ResponseEntity.ok().body(results);
	}

}
