package com.sf.honeymorning.alarm.entity;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DayOfTheWeekBitTest {

	@Test
	@DisplayName("요일을 비트별로 반환한다")
	void testBit() {
		//given
		//when
		int mondayBit = DayOfTheWeek.MONDAY.getShiftedBit();
		int tuesdayBit = DayOfTheWeek.TUESDAY.getShiftedBit();
		int wednesdayBit = DayOfTheWeek.WEDNESDAY.getShiftedBit();
		int thursdayBit = DayOfTheWeek.THURSDAY.getShiftedBit();
		int fridayBit = DayOfTheWeek.FRIDAY.getShiftedBit();
		int saturdayBit = DayOfTheWeek.SATURDAY.getShiftedBit();
		int sundayBit = DayOfTheWeek.SUNDAY.getShiftedBit();

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
		DayOfTheWeek[] expectedDayOfTheWeeks = {DayOfTheWeek.MONDAY, DayOfTheWeek.WEDNESDAY, DayOfTheWeek.THURSDAY, DayOfTheWeek.FRIDAY};
		Integer expectedVale = Arrays.stream(expectedDayOfTheWeeks).map(DayOfTheWeek::getShiftedBit)
			.reduce(0, Integer::sum);

		//when
		int actualValue = DayOfTheWeek.toBit(expectedDayOfTheWeeks);

		//then
		assertThat(actualValue).isEqualTo(expectedVale);
	}

}