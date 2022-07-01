package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrTest {

	@Test
	@DisplayName("일부러 틀려버리기!")
	void failTest() {
		//given
		int value = 100;
		testing expectedTest = new testing(value);
		//when

		//then
		Assertions.assertThat(expectedTest.getValue()).isEqualTo(value);
	}

}

class testing {
	private final int value;

	testing(int value) {
		this.value = value;
	}

	public int getValue() {
		return value * 3;
	}
}
