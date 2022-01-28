package com.varaodev.spatialserver.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.locationtech.jts.geom.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varaodev.spatialserver.model.MapPoint;
import com.varaodev.spatialserver.model.StdPolygon;
import com.varaodev.spatialserver.services.MapPointService;

@CrossOrigin
@RestController
@RequestMapping(value = "/points")
public class MapPointResource extends OperationsResource<MapPointService> {
	
	@PostMapping("/rectangular-buffer")
	public ResponseEntity<List<String>> rectangularBuffer(@RequestBody Input input) {
		List<StdPolygon> resultPoints = service.rectangularBuffer(input.getPoints(), input.getWidthInKm(),
				input.getLengthInKm(), input.getAzimuthInDegrees());
		List<String> results = resultPoints.stream().map(StdPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/line-buffer")
	public ResponseEntity<List<String>> lineBuffer(@RequestBody Input input) {
		List<StdPolygon> resultPoints = service.lineBuffer(input.getPoints(), input.getDistanceInKm());
		List<String> results = resultPoints.stream().map(StdPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/circular-buffer")
	public ResponseEntity<List<String>> circularBuffer(@RequestBody Input input) {
		List<StdPolygon> resultPoints = service.circularBuffer(input.getPoints(), input.getDistanceInKm(), 
				input.getNumberOfAzimuths());
		List<String> results = resultPoints.stream().map(StdPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/rotation")
	public ResponseEntity<List<String>> rotation(@RequestBody Input input) {
		List<Point> resultPoints = service.rotation(input.getPoints(), input.getCentroid(), 
				input.getAngleInDegrees(), input.getRotationSense());
		List<String> results = resultPoints.stream().map(Point::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	@PostMapping("/distance")
	public ResponseEntity<List<Double>> distanceInKm(@RequestBody Input input) {
		List<Double> results = service.distance(input.getPoints());
		return ResponseEntity.ok().body(results);
	}
	
	static class Input {
		private List<MapPoint> points;
		
		private MapPoint centroid;
		private Double angleInDegrees;
		private Integer rotationSense;
		
		private Double distanceInKm;
		private Integer numberOfAzimuths;
		
		private Double widthInKm;
		private Double lengthInKm;
		
		private Double azimuthInDegrees;
		
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

		public Double getAngleInDegrees() {
			return angleInDegrees;
		}

		public void setAngleInDegrees(Double angleInDegrees) {
			this.angleInDegrees = angleInDegrees;
		}

		public Integer getRotationSense() {
			return rotationSense;
		}

		public void setRotationSense(Integer rotationSense) {
			this.rotationSense = rotationSense;
		}

		public Double getDistanceInKm() {
			return distanceInKm;
		}

		public void setDistanceInKm(Double distanceInKm) {
			this.distanceInKm = distanceInKm;
		}

		public Integer getNumberOfAzimuths() {
			return numberOfAzimuths;
		}

		public void setNumberOfAzimuths(Integer numberOfAzimuths) {
			this.numberOfAzimuths = numberOfAzimuths;
		}

		public Double getWidthInKm() {
			return widthInKm;
		}

		public void setWidthInKm(Double widthInKm) {
			this.widthInKm = widthInKm;
		}

		public Double getLengthInKm() {
			return lengthInKm;
		}

		public void setLengthInKm(Double lengthInKm) {
			this.lengthInKm = lengthInKm;
		}

		public Double getAzimuthInDegrees() {
			return azimuthInDegrees;
		}

		public void setAzimuthInDegrees(Double azimuthInDegrees) {
			this.azimuthInDegrees = azimuthInDegrees;
		}
	}

}
