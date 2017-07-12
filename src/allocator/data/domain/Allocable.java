package allocator.data.domain;

import java.util.Map;

public interface Allocable {

	public Map<Resource, Integer> getResources();
	public Map<String, String> getInfo();
	
}
