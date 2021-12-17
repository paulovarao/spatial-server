package com.varaodev.spatialserver.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class WktModelTests {
	
	@Test
	void mapPoint() {
		String wkt = "POINT (0 0)";
		MapPoint point = new MapPoint(0, 0);
		assertThat(point.wktGeometry().toString()).isEqualTo(wkt);
		assertThat(point).isEqualTo(new MapPoint(wkt));
		
		// Test Exceptions
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPoint("MULTIPOINT((0 0), (1 1))");
		});
		
		assertThrows(IllegalArgumentException.class, () -> {
			new MapPoint("safjn");
		});
		
		String str = null;
		assertThrows(NullPointerException.class, () -> {
			new MapPoint(str);
		});
	}

}
