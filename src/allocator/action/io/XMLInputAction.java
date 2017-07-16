package allocator.action.io;

import java.io.FileNotFoundException;

import allocator.action.IOAction;
import allocator.service.FileIOService;

public class XMLInputAction implements IOAction {

	private FileIOService fios;
	
	public XMLInputAction(FileIOService fios) {
		this.fios = fios;
	}

	@Override
	public void execute(String filePath) throws FileNotFoundException {
		this.fios.populateFromFile(filePath);
	}
	

}
