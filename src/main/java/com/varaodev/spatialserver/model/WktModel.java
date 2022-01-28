package com.varaodev.spatialserver.model;

import static com.varaodev.spatialserver.exceptions.ExceptionGenerator.nullParamCheck;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public interface WktModel<M extends Geometry> {
	
	M wktGeometry();
	String validGeometryType();
	
	default GeometryFactory factory() {
		return new GeometryFactory(new PrecisionModel(1000000));
	}
	
	@SuppressWarnings("unchecked")
	default M geometry(String wkt) {
		nullParamCheck(wkt, "WKT input");
		
		WKTReader reader = new WKTReader(factory());
		try {
			Geometry geometry = reader.read(wkt);
			geometryCheck(geometry);
			return (M) geometry;
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private void geometryCheck(Geometry geometry) {
		String type = validGeometryType();
		if (!type.equals(geometry.getGeometryType()))
			throw new IllegalArgumentException(
					"WKT input is not a valid geometry (" + type + "): " + geometry.getGeometryType());
	}

}
