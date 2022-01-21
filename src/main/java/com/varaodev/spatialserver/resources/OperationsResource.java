package com.varaodev.spatialserver.resources;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public abstract class OperationsResource<S> {
	
	@Autowired
	protected S service;
	
	@GetMapping("/operations")
	public ResponseEntity<List<String>> availableOperations() {
		return ResponseEntity.ok().body(availableOperationsList());
	}
	
	@GetMapping("/params/{operation}")
	public ResponseEntity<Map<String,String>> operationParams(@PathVariable String operation) {
		Map<String,String> results = operationParameters(operation);
		return ResponseEntity.ok().body(results);
	}
	
	protected List<String> availableOperationsList() {
		Method[] declaredMethods = service.getClass().getDeclaredMethods();
		
		return Arrays.asList(declaredMethods).stream()
				.filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0)
				.map(m -> stringFormat(m.getName())).sorted().collect(Collectors.toList());
	}
	
	protected Map<String,String> operationParameters(String operation) {
		Method[] declaredMethods = service.getClass().getDeclaredMethods();
		
		Method op = Arrays.asList(declaredMethods).stream()
				.filter(m -> stringFormat(m.getName()).equals(operation)).findAny().get();
		
		Parameter[] params = op.getParameters();
		
		Map<String,String> results = new LinkedHashMap<>();
		for (Parameter p : params) results.put(p.getName(), paramType(p.getParameterizedType()));
		results.put("result", paramType(op.getGenericReturnType()));
		
		return results;
	}
	
	private String stringFormat(String string) {
		String regex = "([A-Z][a-z]+)";
		String replacement = "-$1";
		String output = string.replaceAll(regex, replacement).toLowerCase();
		return output;
	}
	
	private String paramType(Type type) {
		String typeName = type.toString();
		String genericRegex = "(?:[<])([\\w\\.]+)(?:[>])";
		Pattern pattern = Pattern.compile(genericRegex);
		Matcher matcher = pattern.matcher(typeName);
		if (matcher.find()) {
			String genericType = matcher.group(1);
			String genericTypeWithBracets = matcher.group(0);
			
			return simpleType(typeName.replace(genericTypeWithBracets, "")) + "." + simpleType(genericType);
		}
		return simpleType(typeName);
	}
	
	private String simpleType(String type) {
		String[] array = type.split("\\.");
		String simpleType = array[array.length-1];
		return GeometryTypesResource.isWkt(simpleType) ? "Wkt" : simpleType;
	}

}
