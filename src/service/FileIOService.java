package service;

import data.service.ScholarDataService;

public interface FileIOService {

	public void populateFromFile(ScholarDataService sds, String filePath);

	public void write(String path);

}
