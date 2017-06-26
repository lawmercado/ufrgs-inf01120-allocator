package data.util;

import java.time.LocalTime;

public class Util {

	public static LocalTime getTimeFromString(String timeString) {
		String[] timeInfo = timeString.split(":");
		
		return LocalTime.of(Integer.parseInt(timeInfo[0]), Integer.parseInt(timeInfo[1]));		
	}
	
}
