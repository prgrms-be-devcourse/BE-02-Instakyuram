package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrTest {
	@Test
	@DisplayName("테스트 실패 하기")
	void failTest() {
		//given
		int input = 200;
		pr test = new pr(input);
		//when
		int gettingVal = test.getVal();
		//then

		Assertions.assertThat(gettingVal).isEqualTo(input);
	}
}

class pr {
	private final int val;

	public pr(int val) {
		this.val = val * 10;
	}

	public int getVal() {
		return val;
	}
}
