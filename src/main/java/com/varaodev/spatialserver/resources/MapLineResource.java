package com.varaodev.spatialserver.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.varaodev.spatialserver.model.MapLine;
import com.varaodev.spatialserver.model.StdPolygon;
import com.varaodev.spatialserver.services.MapLineService;

@CrossOrigin
@RestController
@RequestMapping(value = "/lines")
public class MapLineResource extends OperationsResource<MapLineService> {
	
	@PostMapping("/buffer")
	public ResponseEntity<List<String>> buffer(@RequestBody Input input) {
		List<StdPolygon> resultPolygons = service.buffer(input.getLines(), input.getDistanceInKm(),
				input.getNumberOfAzimuths());
		List<String> results = resultPolygons.stream().map(StdPolygon::toString)
				.collect(Collectors.toList());
		return ResponseEntity.ok().body(results);
	}
	
	static class Input {
		private List<MapLine> lines;
		
		private Double distanceInKm;
		private Integer numberOfAzimuths;

		public List<MapLine> getLines() {
			return lines;
		}

		public void setLines(List<MapLine> lines) {
			this.lines = lines;
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
	}

}
