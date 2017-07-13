package allocator.action.io;

import java.io.FileNotFoundException;

import allocator.action.IOAction;
import allocator.service.FileIOService;

public class ExcelInputAction implements IOAction {

	private FileIOService fios;
	
	public ExcelInputAction(FileIOService fios) {
		this.fios = fios;
	}

	@Override
	public void execute(String filePath) {
		try {
			this.fios.populateFromFile(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}
