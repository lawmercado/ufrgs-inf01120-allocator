package service;

import data.domain.Allocable;
import data.service.ScholarDataService;

public interface AllocationService {

	public void execute();
	
	public void allocate(Allocable what, Allocable where);
}
