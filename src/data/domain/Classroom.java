package data.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.service.Allocable;
import data.service.Resource;

public class Classroom implements Allocable {

	private String building;
	private String room;
	private List<Resource> availResources;

	public Classroom(String building, String room, List<Resource> availResources) {
		this.building = building;
		this.room = room;
		this.availResources = availResources;
	}

	@Override
	public List<Resource> getResources() {
		return this.availResources;
	}

	@Override
	public Map<String, String> getInfo() {
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("building", this.building);
		properties.put("room", this.room);

		return properties;
	}
	
	public String getBuilding() {
		return this.building;
	}

	public String getRoom() {
		return this.room;
	}

}
