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
	
	public static String getBuildingFromAllocable(Allocable allocable) throws Exception {
		Map<String, String> info = allocable.getInfo();
		
		if(info.containsKey("building")) {
			return info.get("building");
		}
		
		throw new Exception("No building information is available in the Allocable type object!");
	}
	
	public static String getRoomFromAllocable(Allocable allocable) throws Exception {
		Map<String, String> info = allocable.getInfo();
		
		if(info.containsKey("room")) {
			return info.get("room");
		}
		
		throw new Exception("No room information is available in the Allocable type object!");
	}

}
