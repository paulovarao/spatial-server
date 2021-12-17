package com.varaodev.spatialserver.resources;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.services.MapPointService;

@CrossOrigin
@RestController
@RequestMapping(value = "/points")
public class MapPointResource implements OperationsResource {
	
	@Autowired
	private MapPointService service;
	
	@PostMapping("/rotation")
	public ResponseEntity<List<String>> rotation(@RequestBody Map<String, Object> inputBody) {
		PointsInput input = new PointsInput(inputBody);
		List<Point> resultPoints = service.rotation(input.getPoints(), input.getCentroid(), 
				input.getAngleDeg(), input.getRotationSense());
		List<String> results = resultPoints.stream().map(Point::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@GetMapping("/rotation/params")
	public ResponseEntity<Map<String,String>> rotationInputParams() {
		Map<String,String> results = new LinkedHashMap<>();
		results.put("points", "List.Wkt.Point");
		results.put("centroid", "Wkt.Point");
		results.put("angleDeg", "Double");
		results.put("rotationSense", "Integer");
		return ResponseEntity.ok().body(results);
	}
	
	@GetMapping("/distance")
	public ResponseEntity<List<Double>> distanceInKm(@RequestBody List<String> wktArray) {
		PointsInput input = new PointsInput(wktArray);
		List<Double> results = service.distanceInKm(input.getPoints());
		return ResponseEntity.ok().body(results);
	}

	@Override
	public List<String> availableOperationsList() {
		return List.of(
				"rotation",
				"distance"
				);
	}
	
	// Input class
	private class PointsInput {
		
		private List<MapPoint> points;
		private MapPoint centroid;
		private Double angleDeg;
		private Integer rotationSense;
		
		@SuppressWarnings("unchecked")
		public PointsInput(Map<String, Object> input) {
			List<String> wktArray = (List<String>) input.get("points");
			String centroidPoint = (String) input.get("centroid");
			Double angleDeg = Double.parseDouble(input.get("angleDeg").toString());
			Integer rotationSense = Integer.parseInt(input.get("rotationSense").toString());
			
			this.points = points(wktArray);
			this.centroid = new MapPoint(centroidPoint);
			this.angleDeg = angleDeg;
			this.rotationSense = rotationSense;
		}
		
		public PointsInput(List<String> input) {
			this.points = points(input);
		}

		public List<MapPoint> getPoints() {
			return points;
		}

		public MapPoint getCentroid() {
			return centroid;
		}

		public Double getAngleDeg() {
			return angleDeg;
		}

		public Integer getRotationSense() {
			return rotationSense;
		}
		
		private List<MapPoint> points(List<String> wktArray) {
			return wktArray.stream().map(w -> new MapPoint(w)).collect(Collectors.toList());
		}
	}

}
