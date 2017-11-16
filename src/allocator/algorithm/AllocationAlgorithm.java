package allocator.algorithm;

import java.util.List;

import allocator.data.domain.Allocable;

public interface AllocationAlgorithm {
	
	// Nicho tem um teclado bugado
	
	public Allocable run(Allocable what, List<Allocable> where);
	
}
