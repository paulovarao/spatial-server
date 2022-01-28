package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;

public interface Triangularizable {
	
	MapPoints getShell();
	List<MapPoints> getHoles();
	
	// Method to calculate the approximated area of a polygon corresponding to a segment 
	// of a ellipsoid/spheroid
	default Double area() {
		double shell = polygonArea(getShell());
		
		double holes = 0.0;
		if (getHoles() != null) {
			holes = IntStream.range(0, getHoles().size()).boxed()
				.map(i -> polygonArea(getHoles().get(i)))
				.reduce(0.0, (a,s) -> a+s);			
		}
		
		return Rounded.round(shell - holes, 3);
	}
	
	// Private methods
	private double polygonArea(MapPoints points) {
		// Divide polygon into triangles (Groups of 03 points)
		List<Triangle> triangles = polygonTriangles(points);

		double sum = 0.0;
		for (Triangle t : triangles) sum += t.area();
		return sum;
	}
	
	private List<Triangle> polygonTriangles(MapPoints points) {
		GeometryFactory factory = new GeometryFactory();
		Geometry reference = factory.createPolygon(points.getLinearRing());
		Geometry notTriangulized = reference; // remaining area to collect triangles
		List<Triangle> validTriangles = new ArrayList<>(); // Collected triangles
		int n = 0;
		for (int i = 0; i < 10000; i++) {
			if (trianglesFound(reference, validTriangles)) break;
			
			List<MapPoint> pointList = getPoints(notTriangulized);

			// build triangle
			List<MapPoint> subList = new ArrayList<>( List.of(
					pointList.get(n),pointList.get(n+1),pointList.get(n+2),pointList.get(n)) );
			Polygon pol = factory.createPolygon(subList.toArray(new MapPoint[0]));
			Triangle triangle = new Triangle(pol.toString());

			// test triangle
			if (validTriangle(triangle, validTriangles, reference)) {
				// update collected triangles
				validTriangles.add(triangle);

				// update remaining area to collect triangles
				Geometry trianglesPolygon = union(validTriangles);
				notTriangulized = reference.difference(	trianglesPolygon );
				n = 0;
			} else n++;
		}
		
		return validTriangles;
	}
	
	private boolean validTriangle(Triangle triangle, List<Triangle> validTriangles,
			Geometry geometry) {
		// valid triangles geometry is empty polygon if valid triangles list is empty
		boolean newTriangle = validTriangles.isEmpty() ? true 
				: union(validTriangles).intersection(triangle.wktGeometry()).getArea() == 0;
		
		return geometry.contains(triangle.wktGeometry()) && !validTriangles.contains(triangle) 
				&& newTriangle;
	}
	
	private boolean trianglesFound(Geometry geometry, List<Triangle> validTriangles) {
		if (validTriangles.isEmpty()) return false;
		return geometry.equals( union(validTriangles) );
	}
	
	private Geometry union(List<Triangle> triangles) {
		Geometry geom = triangles.get(0).wktGeometry();
		for (Triangle t : triangles) geom = geom.union(t.wktGeometry());
		return geom;
	}
	
	private List<MapPoint> getPoints(Geometry geometry) {
		return Arrays.asList(geometry.getCoordinates()).stream().map(c -> new MapPoint(c))
				.collect(Collectors.toList());
	}

}
