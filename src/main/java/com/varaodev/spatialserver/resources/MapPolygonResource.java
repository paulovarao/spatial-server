package com.varaodev.spatialserver.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varaodev.spatialserver.model.MapPolygon;
import com.varaodev.spatialserver.model.StdPolygon;
import com.varaodev.spatialserver.services.MapPolygonService;

@CrossOrigin
@RestController
@RequestMapping(value = "/polygons")
public class MapPolygonResource extends OperationsResource<MapPolygonService> {
	
	@PostMapping("/mosaic")
	public ResponseEntity<List<String>> mosaic(@RequestBody Input input) {
		List<MapPolygon> resultPolygons = service.mosaic(input.getPolygons(), input.getWidthInKm(),
				input.getMinimumLengthInKm(), input.getMaximumLengthInKm(), input.getAzimuthInDegrees(),
				input.getOverlapInKm());
		List<String> results = resultPolygons.stream().map(MapPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/difference")
	public ResponseEntity<List<String>> difference(@RequestBody Input input) {
		StdPolygon result = service.difference(input.getPolygons());
		return ResponseEntity.ok().body(List.of(result.toString()));
	}
	
	@PostMapping("/intersection")
	public ResponseEntity<List<String>> intersection(@RequestBody Input input) {
		StdPolygon result = service.intersection(input.getPolygons());
		return ResponseEntity.ok().body(List.of(result.toString()));
	}
	
	@PostMapping("/union")
	public ResponseEntity<List<String>> union(@RequestBody Input input) {
		StdPolygon result = service.union(input.getPolygons());
		return ResponseEntity.ok().body(List.of(result.toString()));
	}
	
	@PostMapping("/simple")
	public ResponseEntity<List<String>> simple(@RequestBody Input input) {
		List<MapPolygon> resultPolygons = service.simple(input.getPolygons());
		List<String> results = resultPolygons.stream().map(MapPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/area")
	public ResponseEntity<List<Double>> area(@RequestBody Input input) {
		List<Double> results = service.area(input.getPolygons());
		return ResponseEntity.ok().body(results);
	}
	
	static class Input {
		private List<MapPolygon> polygons;
		
		private Double widthInKm;
		private Double minimumLengthInKm;
		private Double maximumLengthInKm;
		private Double azimuthInDegrees;
		private Double overlapInKm;
		
		public List<MapPolygon> getPolygons() {
			return polygons;
		}
		
		public void setPolygons(List<MapPolygon> polygons) {
			this.polygons = polygons;
		}
		
		public Double getWidthInKm() {
			return widthInKm;
		}
		
		public void setWidthInKm(Double widthInKm) {
			this.widthInKm = widthInKm;
		}
		
		public Double getMinimumLengthInKm() {
			return minimumLengthInKm;
		}
		
		public void setMinimumLengthInKm(Double minimumLengthInKm) {
			this.minimumLengthInKm = minimumLengthInKm;
		}
		
		public Double getMaximumLengthInKm() {
			return maximumLengthInKm;
		}
		
		public void setMaximumLengthInKm(Double maximumLengthInKm) {
			this.maximumLengthInKm = maximumLengthInKm;
		}
		
		public Double getAzimuthInDegrees() {
			return azimuthInDegrees;
		}
		
		public void setAzimuthInDegrees(Double azimuthInDegrees) {
			this.azimuthInDegrees = azimuthInDegrees;
		}
		
		public Double getOverlapInKm() {
			return overlapInKm;
		}
		
		public void setOverlapInKm(Double overlapInKm) {
			this.overlapInKm = overlapInKm;
		}
	}

}
