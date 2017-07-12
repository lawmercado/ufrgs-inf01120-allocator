package allocator.algorithm;

import java.util.List;

import allocator.data.domain.Allocable;

public interface AllocationAlgorithm {
	
	public Allocable run(Allocable what, List<Allocable> where);

}
