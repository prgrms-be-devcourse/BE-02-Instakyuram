package com.kdt.instakyuram;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PrTest {

	@Test
	@DisplayName("일부러 틀려버리기!")
	void failTest(){
	    //given

	    //when

	    //then
		Assertions.assertThat(1).isEqualTo(10);
	}
}
