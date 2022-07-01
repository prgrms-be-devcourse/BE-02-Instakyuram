package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WorkFlowTest {

	@Test
	@DisplayName("아아아아")
	void work() {
		//given
		int val = 300;
		WorkTest test = new WorkTest(val);
		//when

		//then
		Assertions.assertThat(test.getVal()).isEqualTo(val);
	}
}

class WorkTest {
	private final int val;

	public WorkTest(int val) {
		this.val = val * 300;
	}

	public int getVal() {
		return val;
	}
}
