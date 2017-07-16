package allocator;

import java.time.LocalDate;

import allocator.action.*;
import allocator.action.allocation.*;
import allocator.action.io.*;
import allocator.algorithm.AllocationAlgorithm;
import allocator.algorithm.impl.OurAllocateAlgorithm;
import allocator.service.*;
import allocator.service.io.XMLIOService;
import allocator.service.ScholarDataService;
import allocator.service.allocation.ScholarSemestralAllocationService;

public class AllocatorAppXML {

	public static void main(String[] args) {
		Allocator allocator = new Allocator();
		
		ScholarDataService sds = allocator.getScholarDataService();
		
		AllocationAlgorithm algorithm = new OurAllocateAlgorithm();
		LocalDate semesterBegin = LocalDate.of(2016, 1, 1);
		
		AllocationService semestralAllocationService = new ScholarSemestralAllocationService(algorithm, sds, semesterBegin);
		
		AllocationAction semestralAllocationAction = new ScholarSemestralAllocationAction(semestralAllocationService);
		
		FileIOService xmlIOService = new XMLIOService(sds);
		
		IOAction inputXmlAction = new XMLInputAction(xmlIOService);
		
		allocator.run(inputXmlAction, "");
		
		allocator.run(semestralAllocationAction);
		
		IOAction outputXmlAction = new XMLOutputAction(xmlIOService);
		
		allocator.run(outputXmlAction, "");

	}

}
