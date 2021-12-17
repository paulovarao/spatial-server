package com.varaodev.spatialserver.resources;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface OperationsResource {
	
	List<String> availableOperationsList();
	
	@GetMapping("/operations")
	default ResponseEntity<List<String>> availableOperations() {
		return ResponseEntity.ok().body(availableOperationsList());
	}

}
