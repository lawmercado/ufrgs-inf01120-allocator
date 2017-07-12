package allocator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import allocator.AvailableAction;
import allocator.action.*;
import allocator.action.allocation.*;
import allocator.algorithm.AllocationAlgorithm;
import allocator.algorithm.OurAllocateAlgorithm;
import allocator.data.service.*;
import allocator.data.service.impl.ScholarDataServiceImpl;

public class ScholarSemestralAllocator {

	private Map<AvailableAction, Action> actions;
	
	private ScholarDataService sds;
	
	public ScholarSemestralAllocator(LocalDate semesterBegin) {
		this.sds = new ScholarDataServiceImpl();
		this.actions = new HashMap<AvailableAction, Action>();
		
		AllocationAlgorithm algorithm = new OurAllocateAlgorithm(); 
		
		this.actions.put(AvailableAction.SEMESTRAL_ALLOCATE, new ScholarSemestralAllocationAction(algorithm, this.sds, semesterBegin));
	}
	
	public void runAction(AvailableAction actionId) throws IllegalArgumentException {
		
	}
}
