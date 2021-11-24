package com.varaodev.spatialserver.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public interface JsonModel {			
	
	public static <T> T getModel(String jsonObject, Class<T> objectClass) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			return (T) objectMapper.readValue(jsonObject, objectClass);
		} catch (JsonProcessingException e) {
			// e.printStackTrace();
			return getModel(new JsonObject().toString(), objectClass);
		}
	}
	
	@SuppressWarnings("unchecked")
	default <T> String toJson() {
		ObjectMapper objectMapper = new ObjectMapper();
		ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
		try {
			return ow.writeValueAsString((T) this);
		} catch (JsonProcessingException e) {
			return exceptionDetails(e);
		}
	}
	
	default JsonObject toJsonObject() {
		return JsonParser.parseString(toJson()).getAsJsonObject();
	}
	
	default String exceptionDetails(Exception e) {
		JsonObject error = new JsonObject();
		error.addProperty("error", e.getMessage());
		error.addProperty("exception", e.getClass().getSimpleName());
		return error.toString();
	}

}
