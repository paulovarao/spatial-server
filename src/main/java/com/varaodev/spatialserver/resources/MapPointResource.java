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
	public ResponseEntity<List<String>> rotation(@RequestBody Input input) {
		// PointsInput input = new PointsInput(inputBody);
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
		results.put("result", "List.Wkt");
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/distance")
	public ResponseEntity<List<Double>> distanceInKm(@RequestBody Input input) {
		// PointsInput input = new PointsInput(inputBody);
		List<Double> results = service.distanceInKm(input.getPoints());
		return ResponseEntity.ok().body(results);
	}
	
	@GetMapping("/distance/params")
	public ResponseEntity<Map<String,String>> distanceInputParams() {
		Map<String,String> results = new LinkedHashMap<>();
		results.put("points", "List.Wkt.Point");
		results.put("result", "List.Double");
		return ResponseEntity.ok().body(results);
	}

	@Override
	public List<String> availableOperationsList() {
		return List.of(
				"rotation",
				"distance"
				);
	}
	
	static class Input {
		private List<MapPoint> points;
		private MapPoint centroid;
		private Double angleDeg;
		private Integer rotationSense;
		
		public List<MapPoint> getPoints() {
			return points;
		}
		
		public void setPoints(List<MapPoint> points) {
			this.points = points;
		}
		
		public MapPoint getCentroid() {
			return centroid;
		}
		
		public void setCentroid(MapPoint centroid) {
			this.centroid = centroid;
		}
		
		public Double getAngleDeg() {
			return angleDeg;
		}
		
		public void setAngleDeg(Double angleDeg) {
			this.angleDeg = angleDeg;
		}
		
		public Integer getRotationSense() {
			return rotationSense;
		}
		
		public void setRotationSense(Integer rotationSense) {
			this.rotationSense = rotationSense;
		}
	}

}
