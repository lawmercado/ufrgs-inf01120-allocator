package allocator.data.domain;

public class Discipline {
	
	private String id;

	private String name;
	
	public Discipline(String id, String name)
	{
		this.id = id;
		this.name = name;
	}
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	
}
