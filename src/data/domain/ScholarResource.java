package data.domain;

public class ScholarResource implements Resource {
	
	private int id;
	private String description;
	
	public ScholarResource(int id, String description) {
		this.id = id;
		this.description = description;
	}
	
	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	

}
