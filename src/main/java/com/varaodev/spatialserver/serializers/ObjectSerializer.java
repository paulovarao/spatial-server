package com.varaodev.spatialserver.serializers;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public interface ObjectSerializer {
	
	default String toJson(Object obj) {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			return exceptionDetails(e);
		}
	}
	
	default String exceptionDetails(Exception e) {
		Map<String, Object> error = new LinkedHashMap<>();
		error.put("error", e.getMessage());
		error.put("exception", e.getClass().getSimpleName());
		return toJson(error);
	}

}
