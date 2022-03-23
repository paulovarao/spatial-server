package com.varaodev.spatialserver.resources;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.varaodev.spatialserver.model.SatellitePosition;
import com.varaodev.spatialserver.model.StdPolygon;
import com.varaodev.spatialserver.services.SatelliteService;

@CrossOrigin
@RestController
@RequestMapping(value = "/satellite/")
public class SatelliteResources {
	
	@Autowired
	private SatelliteService service;
	
	@GetMapping("/tle/{catNumber}")
	@ResponseBody
	public String getTle(@PathVariable String catNumber) {
		String uri = "https://celestrak.com/NORAD/elements/gp.php?CATNR=" + catNumber;
		RestTemplate template = new RestTemplate();
		String result = template.getForObject(uri, String.class);
		return result;
	}
	
	@PostMapping("/field-of-regard")
	public ResponseEntity<List<String>> fieldOfRegard(@RequestBody Input input) {
		StdPolygon resultPolygon = service.fieldOfRegard(input.getPositions(), input.getMaxLookAngle(),
				input.getMinLookAngle());
		List<String> results = new ArrayList<>(List.of(resultPolygon.toString()));
		return ResponseEntity.ok().body(results);
	}
	
	static class Input {
		private List<SatellitePosition> positions;
		private Double maxLookAngle;
		private Double minLookAngle;
		
		public List<SatellitePosition> getPositions() {
			return positions;
		}
		
		public void setPositions(List<SatellitePosition> positions) {
			this.positions = positions;
		}
		
		public Double getMaxLookAngle() {
			return maxLookAngle;
		}
		
		public void setMaxLookAngle(Double maxLookAngle) {
			this.maxLookAngle = maxLookAngle;
		}
		
		public Double getMinLookAngle() {
			return minLookAngle;
		}
		
		public void setMinLookAngle(Double minLookAngle) {
			this.minLookAngle = minLookAngle;
		}
	}

}
