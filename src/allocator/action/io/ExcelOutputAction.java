package allocator.action.io;

import java.io.FileNotFoundException;

import allocator.action.IOAction;
import allocator.service.FileIOService;

public class ExcelOutputAction implements IOAction {

	private FileIOService fios;
	
	public ExcelOutputAction(FileIOService fios) {
		this.fios = fios;
	}
	
	@Override
	public void execute(String filePath) {
		try {
			this.fios.saveToFile(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
