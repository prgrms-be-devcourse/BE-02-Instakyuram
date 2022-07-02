package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrTest {
	@Test
	@DisplayName("성공")
	void success() {
		//given

		//when

		//then
		Assertions.assertThat(10).isEqualTo(10);
	}
}
