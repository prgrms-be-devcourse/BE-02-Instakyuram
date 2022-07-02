package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrTest {
	@Test
	@DisplayName("성공")
	void success() {
		//given
		int input = 100;
		module module = new module(input);
		//when

		//then
		Assertions.assertThat(module.getVal()).isEqualTo(module);
	}
}

class module {
	private final int val;

	public module(int val) {
		this.val = val;
	}

	public int getVal() {
		return val * 30;
	}
}
