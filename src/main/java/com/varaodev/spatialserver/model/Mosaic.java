package com.varaodev.spatialserver.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;

public class Mosaic implements Rotator {
	
	private MapPolygon polygon;
	private Double widthKm;
	private Double minLengthKm;
	private Double maxLengthKm;
	private Double azimuthDeg;
	private Double overlapKm;
	
	public Mosaic() {}

	public Mosaic(MapPolygon polygon, Double widthKm, Double minLengthKm, Double maxLengthKm, 
			Double azimuthDeg, Double overlapKm) {
		this.polygon = polygon;
		this.widthKm = widthKm;
		this.minLengthKm = minLengthKm;
		this.maxLengthKm = maxLengthKm;
		this.azimuthDeg = azimuthDeg;
		this.overlapKm = overlapKm;
	}
	
	@Override
	public MapPolygon getPolygon() {
		return polygon;
	}

	public Double getWidthKm() {
		return widthKm;
	}

	public Double getMinLengthKm() {
		return minLengthKm;
	}

	public Double getMaxLengthKm() {
		return maxLengthKm;
	}

	public Double getAzimuthDeg() {
		return azimuthDeg;
	}

	public Double getOverlapKm() {
		return overlapKm;
	}

	public Mosaic copy() {
		Mosaic mosaic = new Mosaic();
		mosaic.polygon = this.polygon;
		mosaic.azimuthDeg = this.azimuthDeg;
		mosaic.maxLengthKm = this.maxLengthKm;
		mosaic.minLengthKm = this.minLengthKm;
		mosaic.overlapKm = this.overlapKm;
		mosaic.widthKm = this.widthKm;
		return mosaic;
	}
	
	public List<MapPolygon> tiles() {
		validator();
		
		List<MapPoints> tilePoints = new ArrayList<>();
		
		Generator generator = new Generator();
		
		double yMax = generator.getShellPoints().stream().mapToDouble(c -> c.y).max().getAsDouble();
		double yMin = generator.getShellPoints().stream().mapToDouble(c -> c.y).min().getAsDouble();
		
		// iterate on longitude strip values
		for (Double x : generator.longitudeValues()) {
			// Buid a strip that fully covers target polygon in latitude
			MapPoint corner1 = new MapPoint(x, yMin);
			MapPoint corner2 = new MapPoint(x + generator.widthDeg, yMax);
			MapPolygon boxPolygon = boxPolygon(corner1, corner2);
			
			// Find intersection between rotated geometry and largest strip that may cover it
			Geometry stripIntersection = generator.rotated.wktGeometry()
					.intersection(boxPolygon.wktGeometry());
			
			// Process strip intersection to add tiles to list of polygons 
			if (!stripIntersection.isEmpty()) {
				tilePoints.addAll( generator.stripIntersections(stripIntersection, x) );
			}
		}
		return tilePoints.stream()
				.flatMap(mps -> mps.regroupedPoints().stream()
						.map(mp -> new MapPolygon(new MapPoints(mp), null)) )
				.collect(Collectors.toList());
	}
	
	// Private methods
	private MapPolygon boxPolygon(MapPoint corner1, MapPoint corner2) {
		MapPoints stripPoints = corner1.box(corner2);
		return new MapPolygon(stripPoints, null);
	}
	
	private void validator() {
		if (polygon == null)
			throw new IllegalArgumentException("Invalid polygon value: can't be null");
		
		if (widthKm == null || widthKm <= 0)
			throw new IllegalArgumentException("Invalid width value");
		
		if (minLengthKm == null || minLengthKm <= 0)
			throw new IllegalArgumentException("Invalid minimun length value");
		
		if (maxLengthKm == null || maxLengthKm < minLengthKm)
			throw new IllegalArgumentException("Invalid maximum length value");
		
		if (overlapKm == null || overlapKm >= minLengthKm)
			throw new IllegalArgumentException("Invalid overlap value");
		
		if (azimuthDeg == null)
			throw new IllegalArgumentException("Invalid azimuthg value");
	}
	
	private class Generator {
		
		private MapPolygon rotated;
		private MapPoint centroid;
		private double widthDeg;
		private double lengthDeg;
		private double minLengthDeg;
		private double maxLengthDeg;
		private double overlapDeg;
		
		Generator() {
			centroid = new MapPoint(getPolygon().wktGeometry().getCentroid().getCoordinate());
			
			// convert linear arguments to angular
			widthDeg = centroid.convertLinearToAngularDistanceInDegrees(widthKm);
			minLengthDeg = centroid.convertLinearToAngularDistanceInDegrees(minLengthKm);
			maxLengthDeg = centroid.convertLinearToAngularDistanceInDegrees(maxLengthKm);
			overlapDeg = centroid.convertLinearToAngularDistanceInDegrees(overlapKm);
			
			// Rotate geometry of azimuth angle
			rotated = rotated(centroid, azimuthDeg, 1);
		}
		
		List<MapPoint> getShellPoints() {
			return rotated.getShell().getPoints();
		}
		
		List<MapPoints> stripIntersections(Geometry stripIntersection, double x1) {
			
			List<MapPoints> tiles = new ArrayList<>();
			
			// Iterate over geometries of intersection (case concave polygon)
			for (int n = 0; n < stripIntersection.getNumGeometries(); n++) {
				Geometry g = stripIntersection.getGeometryN(n);
				
				List<Coordinate> cList = Arrays.asList(g.getCoordinates());
				double y1 = cList.stream().mapToDouble(c -> c.y).min().getAsDouble();
				double y2 = cList.stream().mapToDouble(c -> c.y).max().getAsDouble();

				// consider overlap as margin for covering
				y1 -= overlapDeg; y2 += overlapDeg;

				// add tiles to list of polygons
				tiles.addAll( polygonTiles(x1, y1, y2) );
			}
			return tiles;
		}
		
		List<MapPoints> polygonTiles(double x1, double y1, double y2) {
						
			List<MapPoints> list = new ArrayList<>();
			
			for (Double y : latitudeValues(y1, y2)) {
				// Build resulting tile
				MapPoint corner1 = new MapPoint(x1, y);
				MapPoint corner2 = corner1.offset(widthDeg, lengthDeg);
				MapPolygon box = boxPolygon(corner1, corner2);
				
				// Rotate resulting tile to match desired azimuth
				List<MapPoint> points = box.getShell().getPoints().stream()
						.map(mp -> mp.mapRotation(centroid, azimuthDeg, -1))
						.collect(Collectors.toList());
				list.add( new MapPoints(points) );
			}
			
			return list;
		}
		
		Double[] latitudeValues(double y1, double y2) {
			// calculates latitude range (delta)
			double deltaY = y2 - y1;
			
			double twoRowsDeltaY = maxLengthDeg*2-overlapDeg;
			
			// calculates the number of rows (latitudes)
			int numberOfRows = deltaY <= maxLengthDeg ? 1 :
				deltaY <= twoRowsDeltaY ? 2 :
					((Double) Math.ceil(deltaY/maxLengthDeg) ).intValue();
			
			lengthDeg = numberOfRows > 1 ? deltaY/numberOfRows + overlapDeg : 
				deltaY > minLengthDeg ? deltaY : minLengthDeg;
			
			double firstY = lengthDeg <= minLengthDeg ? (y2+y1)/2 - lengthDeg/2 : y1;
				
			return valuesArray(firstY, numberOfRows, lengthDeg);
		}
		
		Double[] longitudeValues() {
			// calculates longitude range (delta)
			double xMax = getShellPoints().stream().mapToDouble(p -> p.x).max().getAsDouble();
			double xMin = getShellPoints().stream().mapToDouble(p -> p.x).min().getAsDouble();
			double deltaX = xMax - xMin;
			
			double twoColumnsDeltaX = widthDeg*2-overlapDeg;
			double threeOrMoreColumnsDeltaX = widthDeg-overlapDeg;
			
			// calculates the number of columns (longitudes)
			int numberOfColumns = deltaX <= (widthDeg + overlapDeg) ? 1 :
				deltaX <= twoColumnsDeltaX ? 2 :
					((Double) Math.ceil(deltaX/threeOrMoreColumnsDeltaX) ).intValue();
			
			// calculates real longitude range considering overlap
			double realDeltaX = numberOfColumns == 1 ? widthDeg : 
				numberOfColumns == 2 ? twoColumnsDeltaX :
					threeOrMoreColumnsDeltaX * numberOfColumns;
			
			double deltaXError = (realDeltaX - deltaX) / 2;
			
			double firstX = xMin - deltaXError;
			
			return valuesArray(firstX, numberOfColumns, widthDeg);
		}
		
		Double[] valuesArray(double firstValue, int size, double lengthDeg) {
			return IntStream.range(0, size).boxed()
					.map(i -> i == 0 ? firstValue : firstValue + i*(lengthDeg - overlapDeg))
					.toArray(Double[]::new);
		}
		
	}
	
	

}
