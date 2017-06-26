package data.util.test;

import static org.junit.Assert.*;

import java.time.LocalTime;

import org.junit.Test;

import data.util.Util;

public class UtilTest {

	@Test
	public void testGetTimeFromString() {
		String timeString = "10:10";
		
		assertEquals(LocalTime.of(10, 10), Util.getTimeFromString(timeString));
	}

}
