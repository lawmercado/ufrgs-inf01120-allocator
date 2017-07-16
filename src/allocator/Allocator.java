package allocator;

import java.io.FileNotFoundException;

import allocator.action.AllocationAction;
import allocator.action.IOAction;
import allocator.service.ScholarDataService;
import allocator.service.data.ScholarDataServiceImpl;

public abstract class Allocator {

	private ScholarDataService sds = new ScholarDataServiceImpl();
	
	public ScholarDataService getScholarDataService() {
		return this.sds;
	}
	
	public void run(AllocationAction action) throws FileNotFoundException {
		action.execute();
	}
	
	public void run(IOAction action, String filePath) throws FileNotFoundException {
		action.execute(filePath);
	}
	
	public abstract void showUI();
}
