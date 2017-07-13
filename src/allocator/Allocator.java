package allocator;

import allocator.action.AllocationAction;
import allocator.action.IOAction;
import allocator.service.ScholarDataService;
import allocator.service.data.ScholarDataServiceImpl;

public class Allocator {

	private ScholarDataService sds;
	
	public Allocator() {
		this.sds = new ScholarDataServiceImpl();
	}
	
	public ScholarDataService getScholarDataService() {
		return this.sds;
	}
	
	public void run(AllocationAction action) {
		action.execute();
	}
	
	public void run(IOAction action, String filePath) {
		action.execute(filePath);
	}
	
}
