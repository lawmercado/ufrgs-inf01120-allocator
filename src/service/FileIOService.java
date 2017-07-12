package service;

import java.io.FileNotFoundException;

import data.service.ScholarDataService;

public interface FileIOService {

	public void populateFromFile(ScholarDataService sds, String filePath) throws FileNotFoundException;

	public void write(String path);

}
