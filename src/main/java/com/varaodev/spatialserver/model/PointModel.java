package com.varaodev.spatialserver.model;

import org.locationtech.jts.geom.Coordinate;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class PointModel extends Coordinate implements JsonModel, Rounded {

	private static final long serialVersionUID = 1L;
	
	public PointModel() {
		super();
	}

	public PointModel(Coordinate c) {
		super(c);
	}

	public PointModel(double x, double y) {
		super(x, y);
	}

	public PointModel(double x, double y, double z) {
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
