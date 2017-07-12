package allocator.data.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.time.LocalTime;
import java.time.DayOfWeek;

public class Lesson implements Allocable {
	
	private Group group;
	private LocalTime begin;
	private LocalTime duration;
	private List<DayOfWeek> daysOfWeek;
	private Map<Resource, Integer> reqResources;
	
	public Lesson(Group group, LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek, Map<Resource, Integer> reqResources) {
		this.group = group;
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
	
	public Group getGroup() {
		return this.group;
	}
	
	public LocalTime getBegin() {
		return this.begin;
	}

	public LocalTime getDuration() {
		return this.duration;
	}

	public List<DayOfWeek> getDaysOfWeek() {
		return this.daysOfWeek;
	}

	public static LocalTime getBeginTimeFromAllocable(Allocable allocable) throws IllegalArgumentException {
		Map<String, String> info = allocable.getInfo(); 
		
		if(info.containsKey("begin")) {
			String beginString = info.get("begin");
			
			return LocalTime.parse(beginString);
		}
		
		throw new IllegalArgumentException("No begin information is available in the Allocable type object!");
	}
	
	public static LocalTime getDurationTimeFromAllocable(Allocable allocable) throws IllegalArgumentException {
		Map<String, String> info = allocable.getInfo();
		
		if(info.containsKey("duration")) {
			String durationString = info.get("duration");
			
			return LocalTime.parse(durationString);
		}
		
		throw new IllegalArgumentException("No duration information is available in the Allocable type object!");
	}
	
	public static List<DayOfWeek> getDaysOfWeekFromAllocable(Allocable allocable) throws IllegalArgumentException {
		Map<String, String> info = allocable.getInfo();
		
		if(info.containsKey("daysOfWeek")) {
			List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
			
			String dowString = info.get("daysOfWeek");
			
			dowString = dowString.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
			
			String[] dowStringInfo = dowString.split(",");
			
			for(int i = 0; i < dowStringInfo.length; i++) {
				daysOfWeek.add(DayOfWeek.valueOf(dowStringInfo[i]));
			}
			
			return daysOfWeek;
		}
		
		throw new IllegalArgumentException("No daysOfWeek information is available in the Allocable type object!");
	}
	
}
