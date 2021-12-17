package com.varaodev.spatialserver.model;

public interface Rounded {
	
	public static double round(double number, int decimalPlaces) {
		Double factor = Math.pow(10, decimalPlaces);
		return Double.isFinite(number) ? Math.round(number * factor) / factor : number; 
	}
	
	@SuppressWarnings("unchecked")
	default <M extends PointModel> M rounded(int decimalPlaces) {
		M c = (M) this;
		c.setX( round(c.getX(), decimalPlaces) );
		c.setY( round(c.getY(), decimalPlaces) );
		c.setZ( round(c.getZ(), decimalPlaces) );
		return c;
	}

}
