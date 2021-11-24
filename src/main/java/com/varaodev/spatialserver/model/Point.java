package com.varaodev.spatialserver.model;

import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Point extends Coordinate implements JsonModel, Rounded {

	private static final long serialVersionUID = 1L;
	
	public Point() {
		super();
	}

	public Point(Coordinate c) {
		super(c);
	}

	public Point(double x, double y) {
		super(x, y);
	}

	public Point(double x, double y, double z) {
		super(x, y, z);
	}

	@Override
	@JsonIgnore
	public double getM() {
		return 0.0;
	}
	
	@Override
	public String toString() {
		return toJson();
	}

}
