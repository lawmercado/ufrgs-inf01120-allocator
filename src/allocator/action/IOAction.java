package allocator.action;

import java.io.FileNotFoundException;

public interface IOAction {
	
	public void execute(String filePath) throws FileNotFoundException;
	
}
