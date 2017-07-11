package action.allocation;

import action.SystemAction;
import data.service.ScholarDataService;
import service.AllocationService;

public class ScholarSemestralAllocationAction extends SystemAction {

	ScholarDataService sds;
	AllocationService as;
	
	public ScholarSemestralAllocationAction(ScholarDataService sds, AllocationService as) {
		this.sds = sds;
		this.as = as;
	}
	
	@Override
	public void execute() {
		this.as.execute();
	}

	
}
