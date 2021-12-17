package com.varaodev.spatialserver.exceptions;

import java.util.List;

public class ExceptionGenerator {
	
	public static void nullParamCheck(Object param, String paramName) {
		if (param == null)
			throw new NullPointerException(paramName + " is null.");
	}
	
	public static void listSizeCheck(List<?> list) {
		if (list.size() < 2)
			throw new IllegalArgumentException("Invalid point list:"
					+ " must have at least 2 points.");
	}

}
