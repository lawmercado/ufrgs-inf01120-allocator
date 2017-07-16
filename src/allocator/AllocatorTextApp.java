package allocator;

import java.time.LocalDate;
import java.util.Scanner;

import allocator.action.*;
import allocator.action.allocation.*;
import allocator.action.io.*;
import allocator.algorithm.impl.OurAllocateAlgorithm;
import allocator.service.*;
import allocator.service.io.ExcelIOService;
import allocator.service.io.XMLIOService;
import allocator.service.allocation.ScholarSemestralAllocationService;

public class AllocatorTextApp extends Allocator {

	public final int ERROR_STATUS_CODE = 1;

	public static void main(String[] args) {
		Allocator app = new AllocatorTextApp();

		app.showUI();
	}

	@Override
	public void showUI() {
		Scanner scan = new Scanner(System.in);
		boolean exit = false;

		String filePath, extension;
		LocalDate semesterBegin;

		FileIOService fios;
		IOAction inputAction, outputAction;

		AllocationService as;
		AllocationAction allocAction;

		try {
			do {
				System.out.println();
				System.out.println("Inform the begin date of the semester (year, month and day): ");
				int ano = scan.nextInt();
				int mes = scan.nextInt();
				int dia = scan.nextInt();

				semesterBegin = LocalDate.of(ano, mes, dia);

				// Consume the buffer
				scan.nextLine();

				System.out.println();
				System.out.println("Inform the complete path of the input file: ");
				filePath = scan.nextLine();

				extension = filePath.substring(filePath.lastIndexOf('.') + 1, filePath.length());

				switch (extension) {
				case "xlsx": {
					fios = new ExcelIOService(this.getScholarDataService());
					inputAction = new ExcelInputAction(fios);
					outputAction = new ExcelOutputAction(fios);

					break;
				}

				case "xml": {
					fios = new XMLIOService(this.getScholarDataService());
					inputAction = new XMLInputAction(fios);
					outputAction = new XMLOutputAction(fios);

					break;
				}

				default:
					throw new Exception("Not a supported type of file for input");

				}

				this.run(inputAction, filePath);

				as = new ScholarSemestralAllocationService(new OurAllocateAlgorithm(), this.getScholarDataService(),
						semesterBegin);
				allocAction = new ScholarSemestralAllocationAction(as);

				this.run(allocAction);

				filePath = filePath.replace("." + extension, "_saida." + extension);

				System.out.println("Result of the process saved in: " + filePath);

				this.run(outputAction, filePath);

				System.out.println();

				System.out.println("'I want to repeat this process': Say true or false for this afirmation");
				exit = !scan.nextBoolean();

			} while (!exit);

			scan.close();

		} catch (Exception e) {
			e.printStackTrace();

			System.exit(ERROR_STATUS_CODE);

		}

	}

}
