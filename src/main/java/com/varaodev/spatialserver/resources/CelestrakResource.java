package com.varaodev.spatialserver.resources;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@CrossOrigin
@RestController
@RequestMapping(value = "/satellite/")
public class CelestrakResource {
	
	@GetMapping("/tle/{catNumber}")
	@ResponseBody
	public String getTle(@PathVariable String catNumber) {
		String uri = "https://celestrak.com/NORAD/elements/gp.php?CATNR=" + catNumber;
		RestTemplate template = new RestTemplate();
		String result = template.getForObject(uri, String.class);
		return result;
	}

}
