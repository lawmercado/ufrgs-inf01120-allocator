package allocator.service;

import java.io.FileNotFoundException;

public interface FileIOService {

	public void populateFromFile(String filePath) throws FileNotFoundException;

	public void saveToFile(String filePath) throws FileNotFoundException;

}
