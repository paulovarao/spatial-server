package com.varaodev.spatialserver.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {
	
	@GetMapping("/map")
	public String mapDisplayTest() {
		return "map";
	}

}
