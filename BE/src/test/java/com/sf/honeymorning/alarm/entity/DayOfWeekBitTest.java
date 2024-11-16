package com.sf.honeymorning.alarm.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DayOfWeekBitTest {

	@Test
	@DisplayName("요일을 비트별로 반환한다")
	void testBit() {
		//given
		//when
		int mondayBit = DayOfWeek.MONDAY.getBit();
		int tuesdayBit = DayOfWeek.TUESDAY.getBit();
		int wednesdayBit = DayOfWeek.WEDNESDAY.getBit();
		int thursdayBit = DayOfWeek.THURSDAY.getBit();
		int fridayBit = DayOfWeek.FRIDAY.getBit();
		int saturdayBit = DayOfWeek.SATURDAY.getBit();
		int sundayBit = DayOfWeek.SUNDAY.getBit();

		//then
		assertThat(mondayBit).isEqualTo(1);
		assertThat(tuesdayBit).isEqualTo(2);
		assertThat(wednesdayBit).isEqualTo(4);
		assertThat(thursdayBit).isEqualTo(8);
		assertThat(fridayBit).isEqualTo(16);
		assertThat(saturdayBit).isEqualTo(32);
		assertThat(sundayBit).isEqualTo(64);
	}

	@Test
	@DisplayName("요일별로 각 비트가 활성화 되도록 반환한다")
	void testToBit() {
		//given
		DayOfWeek[] expectedDays = {DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY};
		Integer expectedVale = Arrays.stream(expectedDays).map(DayOfWeek::getBit)
			.reduce(0, Integer::sum);

		//when
		int actualValue = DayOfWeek.toBit(expectedDays);

		//then
		assertThat(actualValue).isEqualTo(expectedVale);
	}

}