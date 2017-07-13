package allocator.action.allocation;

import allocator.action.AllocationAction;
import allocator.service.AllocationService;

public class ScholarSemestralAllocationAction implements AllocationAction {

	private AllocationService as;
	
	public ScholarSemestralAllocationAction(AllocationService as) {
		this.as = as;
	}
	
	@Override
	public void execute() {
		this.as.execute();
	}
	
}
