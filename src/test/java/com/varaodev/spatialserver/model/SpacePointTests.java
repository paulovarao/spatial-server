package com.varaodev.spatialserver.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpacePointTests {
	
	SpacePoint x = new SpacePoint(1, 0, 0);
	SpacePoint y = new SpacePoint(0, 1, 0);
	SpacePoint z = new SpacePoint(0, 0, 1);
	
	SpacePoint sample = new SpacePoint(1, 2, 3);
	
	@Test
	void toMapPoint() {
		assertThat((MapPoint) x.toMapPoint().rounded(3)).isEqualTo(new MapPoint(0, 0));
		assertThat((MapPoint) y.toMapPoint().rounded(3)).isEqualTo(new MapPoint(90, 0));
		assertThat((MapPoint) z.toMapPoint().rounded(3)).isEqualTo(new MapPoint(0, 90));
	}
	
	@Test
	void azimuthRingBuffer() {
		List<Double> angles = List.of(Math.PI/2, Math.PI/2, Math.PI/2, Math.PI/2);
		List<SpacePoint> list = x.azimuthRingBuffer(angles).stream()
				.map(p -> (SpacePoint) p.rounded(3))
				.collect(Collectors.toList());
		assertThat(list.get(0)).isEqualTo(z);
		assertThat(list.get(1)).isEqualTo(y);
		assertThat(list.get(2)).isEqualTo(z.opposite());
		assertThat(list.get(3)).isEqualTo(y.opposite());
	}
	
	@Test
	void buffer() {
		LinkedHashMap<Double, Double> azimuthMap = new LinkedHashMap<>();
		for (int i = 0; i < 4; i++) azimuthMap.put(i*Math.PI/2, Math.PI/2);
		List<SpacePoint> list = x.buffer(azimuthMap).stream()
				.map(p -> (SpacePoint) p.rounded(3))
				.collect(Collectors.toList());
		assertThat(list.get(0)).isEqualTo(z);
		assertThat(list.get(1)).isEqualTo(y);
		assertThat(list.get(2)).isEqualTo(z.opposite());
		assertThat(list.get(3)).isEqualTo(y.opposite());
	}
	
	@Test
	void azimuthPlaneVector() {
		assertThat((SpacePoint) x.azimuthPlaneVector(Math.PI/2).rounded(3)).isEqualTo(z);
		assertThat((SpacePoint) y.azimuthPlaneVector(Math.PI/2).rounded(3)).isEqualTo(z);
		assertThat((SpacePoint) z.azimuthPlaneVector(Math.PI/2).rounded(3))
			.isEqualTo(x.opposite());
	}
	
	@Test
	void arcDistance() {
		assertThat(Rounded.round(x.arcDistance(y), 4)).isEqualTo(Rounded.round(Math.PI/2, 4));
	}
	
	@Test
	void orthogonal() {
		assertThat(x.orthogonal(z)).isEqualTo(y.opposite());
	}
	
	@Test
	void rotation() {
		assertThat((SpacePoint) x.rotation(z, Math.PI/2).rounded(3)).isEqualTo(y);
	}
	
	@Test
	void angle() {
		assertThat(x.angle(y)).isEqualTo(Math.PI/2);
	}
	
	@Test
	void normalizedVector() {
		assertThat(new SpacePoint(0, 0, 50).normalizedVector()).isEqualTo(z);
		assertThat(new SpacePoint(3, 4, 0).normalizedVector())
			.isEqualTo(new SpacePoint(0.6, 0.8, 0));
	}
	
	@Test
	void magnitude() {
		assertThat(new SpacePoint(1, 2, 3).magnitude()).isEqualTo(Math.sqrt(14));
	}
	
	@Test
	void opposite() {
		SpacePoint p2 = new SpacePoint(-1, -2, -3);
		assertThat(sample.opposite()).isEqualTo(p2);
		assertThat(new SpacePoint().opposite()).isEqualTo(new SpacePoint());
	}

}
