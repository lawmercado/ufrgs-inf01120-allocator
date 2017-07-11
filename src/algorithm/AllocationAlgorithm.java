package algorithm;

import java.util.List;

import data.domain.Allocable;

public interface AllocationAlgorithm {
	
	public Allocable run(Allocable what, List<Allocable> where);

}
