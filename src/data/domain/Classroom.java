package data.domain;

import java.util.HashMap;
import java.util.Map;

public class Classroom implements Allocable {

	private String building;
	private String room;
	private Map<Resource, Integer> availResources;

	public Classroom(String building, String room,  Map<Resource, Integer> availResources) {
		this.building = building;
		this.room = room;
		this.availResources = availResources;
	}

	@Override
	public  Map<Resource, Integer> getResources() {
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
