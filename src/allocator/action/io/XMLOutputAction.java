package allocator.action.io;

import java.io.FileNotFoundException;

import allocator.action.IOAction;
import allocator.service.FileIOService;

public class XMLOutputAction implements IOAction {

	private FileIOService fios;
	
	public XMLOutputAction(FileIOService fios) {
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
