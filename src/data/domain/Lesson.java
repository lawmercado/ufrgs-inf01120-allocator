package data.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class Lesson implements Allocable {

	private LocalTime begin;
	private LocalTime duration;
	private List<DayOfWeek> daysOfWeek;
	private Map<Resource, Integer> reqResources;
	
	public Lesson(LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek, Map<Resource, Integer> reqResources) {
		this.begin = begin;
		this.duration = duration;
		this.daysOfWeek = daysOfWeek;
		this.reqResources = reqResources;
	}
	
	@Override
	public  Map<Resource, Integer> getResources() {
		return this.reqResources;
	}

	@Override
	public Map<String, String> getInfo() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("begin", this.begin.toString());
		properties.put("duration", this.duration.toString());
		properties.put("daysOfWeek", this.daysOfWeek.toString());
				
		return properties;
	}
	
	public static LocalTime getBeginTimeFromInfo(Map<String, String> info) throws Exception {
		String beginString = info.get("begin");
		
		if(beginString.equals(null)) {
			throw new Exception("No begin information is available in the general information mapping!");
		}
		
		
		String[] beginInfo = beginString.split(":");
		
		return LocalTime.of(Integer.parseInt(beginInfo[0]), Integer.parseInt(beginInfo[1]));
	}
	
	public static LocalTime getDurationTimeFromInfo(Map<String, String> info) throws Exception {
		String durationString = info.get("duration");
		
		if(durationString.equals(null)) {
			throw new Exception("No duration information is available in the general information mapping!");
		}
		
		String[] durationInfo = durationString.split(":");
		
		return LocalTime.of(Integer.valueOf(durationInfo[0]), Integer.valueOf(durationInfo[1]));
	}
	
	public static List<DayOfWeek> getDaysOfWeekFromInfo(Map<String, String> info) throws Exception {
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		
		String dowString = info.get("daysOfWeek");
		
		if(dowString.equals(null)) {
			throw new Exception("No daysOfWeek information is available in the general information mapping!");
		}
		
		dowString = dowString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
		
		String[] dowStringInfo = dowString.split(",");
		
		for(int i = 0; i < dowStringInfo.length; i++) {
			daysOfWeek.add(DayOfWeek.valueOf(dowStringInfo[i]));
		}
		
		return daysOfWeek;
	}
	
}
