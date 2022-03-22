package com.varaodev.spatialserver.resources;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.varaodev.spatialserver.resources.dto.Elevation;

@CrossOrigin
@RestController
@RequestMapping(value = "/elevations/")
public class ElevationResource {
	
	@GetMapping("/open-elevation/{locations}")
	@ResponseBody
	public List<Elevation> openElevation(@PathVariable String locations) {
		String uri = "https://api.open-elevation.com/api/v1/lookup?locations=" + locations;
		RestTemplate template = new RestTemplate();
		@SuppressWarnings("unchecked")
		List<Elevation> result = (List<Elevation>) template.getForObject(uri, List.class);
		return result;
	}
	
	@GetMapping("/open-topo-data/{locations}")
	@ResponseBody
	public List<Elevation> openTopoData(@PathVariable String locations) {
		String uri = "https://api.opentopodata.org/v1/test-dataset?locations=" + locations;
		RestTemplate template = new RestTemplate();
		Output output = template.getForObject(uri, Output.class);
		return output.getResults();
	}
	
	static class Output {
		private List<Elevation> results;

		public List<Elevation> getResults() {
			return results;
		}

		public void setResults(List<Elevation> results) {
			this.results = results;
		}
	}

}
