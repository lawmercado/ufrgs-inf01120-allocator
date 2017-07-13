package allocator;

import java.time.LocalDate;

import allocator.action.*;
import allocator.action.allocation.*;
import allocator.action.io.*;
import allocator.algorithm.AllocationAlgorithm;
import allocator.algorithm.impl.OurAllocateAlgorithm;
import allocator.service.*;
import allocator.service.io.ExcelIOService;
import allocator.service.ScholarDataService;
import allocator.service.allocation.ScholarSemestralAllocationService;

public class AllocatorApp {

	public static void main(String[] args) {
		Allocator allocator = new Allocator();
		
		ScholarDataService sds = allocator.getScholarDataService();
		
		FileIOService excelIOService = new ExcelIOService(sds);
		
		IOAction inputExcelAction = new ExcelInputAction(excelIOService);
		
		allocator.run(inputExcelAction, "");
		
		AllocationAlgorithm algorithm = new OurAllocateAlgorithm();
		LocalDate semesterBegin = LocalDate.of(2016, 1, 1);
		
		AllocationService semestralAllocationService = new ScholarSemestralAllocationService(algorithm, sds, semesterBegin);
		
		AllocationAction semestralAllocationAction = new ScholarSemestralAllocationAction(semestralAllocationService);
		
		allocator.run(semestralAllocationAction);
		
		IOAction outputExcelAction = new ExcelOutputAction(excelIOService);
		
		allocator.run(outputExcelAction, "");

	}

}
