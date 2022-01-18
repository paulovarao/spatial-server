package com.varaodev.spatialserver.resources.deserializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.varaodev.spatialserver.model.MapPoint;

public class MapPointDeserializer extends StdDeserializer<MapPoint> {

	private static final long serialVersionUID = 1L;
	
	public MapPointDeserializer() { 
        this(null);
    }

	protected MapPointDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public MapPoint deserialize(JsonParser p, DeserializationContext ctxt) 
			throws IOException, JsonProcessingException {
		String input = p.readValueAsTree().toString();
		return new MapPoint(input.replace("\"", ""));
	}

}
