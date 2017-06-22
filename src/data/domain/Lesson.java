package data.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.service.Allocable;
import data.service.Resource;

import java.time.LocalTime;
import java.time.DayOfWeek;

public class Lesson implements Allocable {

	private LocalTime begin;
	private LocalTime duration;
	private List<DayOfWeek> daysOfWeek;
	private List<Resource> reqResources;
	
	public Lesson(LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek, List<Resource> reqResources) {
		this.begin = begin;
		this.duration = duration;
		this.daysOfWeek = daysOfWeek;
		this.reqResources = reqResources;
	}
	
	@Override
	public List<Resource> getResources() {
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
	
	
}
