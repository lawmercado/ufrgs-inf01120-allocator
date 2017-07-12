package allocator.service;

import java.io.FileNotFoundException;

import allocator.data.service.ScholarDataService;

public interface FileIOService {

	public void populateFromFile(ScholarDataService sds, String filePath) throws FileNotFoundException;

	public void write(String filePath);

}
