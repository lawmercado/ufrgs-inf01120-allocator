package allocator.action.allocation;

import java.time.LocalDate;

import allocator.action.Action;
import allocator.algorithm.AllocationAlgorithm;
import allocator.data.service.ScholarDataService;
import allocator.service.AllocationService;
import allocator.service.allocation.ScholarSemestralAllocationService;

public class ScholarSemestralAllocationAction implements Action {

	private AllocationService as;
	
	public ScholarSemestralAllocationAction(AllocationAlgorithm alg, ScholarDataService sds, LocalDate semesterBegin) {
		this.as = new ScholarSemestralAllocationService(alg, sds, semesterBegin);
	}
	
	@Override
	public void execute() {
		this.as.execute();
	}

	
}
