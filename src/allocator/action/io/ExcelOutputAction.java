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
	public void execute(String filePath) throws FileNotFoundException {
		this.fios.saveToFile(filePath);
		
	}

}
