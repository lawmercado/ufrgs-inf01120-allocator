package allocator.service.io;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import allocator.data.domain.*;
import allocator.service.FileIOService;
import allocator.service.ScholarDataService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExcelIOService implements FileIOService {

	private final int SHEET_OF_DEMANDS_INDEX = 0;
	private final int SHEET_OF_CLASSROOMS_INDEX = 2;
	
	private final int DEFAULT_RESOURCE_QUANTITY = 1;
	private final int DEFAULT_DURATION_TIME = 120;
	
	private ScholarDataService sds;
	
	public ExcelIOService(ScholarDataService sds) {
		this.sds = sds;
	}
	
	@Override
	public void populateFromFile(String filePath) throws FileNotFoundException {
		FileInputStream excelFile = new FileInputStream(new File(filePath));
		
		try {
			Workbook workbook = new XSSFWorkbook(excelFile);
	        
			// Clears the domain data stored
			this.sds.clear();
			
	        this.populateDemandsFromWorkbook(workbook);
	        
	        this.populateClassroomsFromWorkbook(workbook);
			
	        workbook.close();
	        
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	public void saveToFile(String filePath) throws FileNotFoundException {
		XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Allocated places");
        
        int rowCount = 0;
        
        Row row = sheet.createRow(rowCount++);
        
        row.createCell(CellInfoWrite.DISCIPLINE.ordinal()).setCellValue("Discipline");
        row.createCell(CellInfoWrite.GROUP.ordinal()).setCellValue("Group");
        row.createCell(CellInfoWrite.TEACHER.ordinal()).setCellValue("Teacher");
        row.createCell(CellInfoWrite.START_TIME.ordinal()).setCellValue("Start time");
        row.createCell(CellInfoWrite.DURATION.ordinal()).setCellValue("Duration");
        row.createCell(CellInfoWrite.DAYS_OF_WEEK.ordinal()).setCellValue("Days of week");
        row.createCell(CellInfoWrite.PLACE.ordinal()).setCellValue("Place");
        
        List<ScholarReservation> reservations = this.sds.getReservations();
        
        Iterator<ScholarReservation> itrReservations = reservations.iterator();
        
        while(itrReservations.hasNext()) {
        	ScholarReservation reservation = itrReservations.next();
        	
        	row = sheet.createRow(rowCount++);
        	
        	String discipline = reservation.getLesson().getGroup().getDiscipline().getId() + " - " + reservation.getLesson().getGroup().getDiscipline().getName();
        	
        	row.createCell(CellInfoWrite.DISCIPLINE.ordinal()).setCellValue(discipline);
            row.createCell(CellInfoWrite.GROUP.ordinal()).setCellValue(reservation.getLesson().getGroup().getId());
            row.createCell(CellInfoWrite.TEACHER.ordinal()).setCellValue(reservation.getLesson().getGroup().getTeacher());
            row.createCell(CellInfoWrite.START_TIME.ordinal()).setCellValue(reservation.getLesson().getBegin().toString());
            row.createCell(CellInfoWrite.DURATION.ordinal()).setCellValue(reservation.getLesson().getDuration().toString());
            
            String daysOfWeek = reservation.getLesson().getDaysOfWeek().toString().replace("]", "").replace("[", "");
            
            row.createCell(CellInfoWrite.DAYS_OF_WEEK.ordinal()).setCellValue(daysOfWeek);
            
            String place = reservation.getClassroom().getBuilding() + " - " + reservation.getClassroom().getRoom();
            row.createCell(CellInfoWrite.PLACE.ordinal()).setCellValue(place);
        }
        
        FileOutputStream outputStream = new FileOutputStream(filePath);
        
        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private void populateDemandsFromWorkbook(Workbook workbook) {
		Sheet sheetDemands = workbook.getSheetAt(SHEET_OF_DEMANDS_INDEX);
		
		List<Lesson> lessons = this.getLessonsFromSheet(sheetDemands);
		List<Group> groups = this.getUniqueGroupsFromLessons(lessons);
		List<Discipline> disciplines = this.getUniqueDisciplinesFromGroups(groups);
		
		Iterator<Discipline> itrDisciplines = disciplines.iterator();
		
		while(itrDisciplines.hasNext()) {
			Discipline discipline = itrDisciplines.next();
			
			this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		}
		
		Iterator<Group> itrGroups = groups.iterator();
		
		while(itrGroups.hasNext()) {
			Group group = itrGroups.next();
			
			this.sds.insertGroup(group.getDiscipline().getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		}
		
		Iterator<Lesson> itrLessons = lessons.iterator();
		
		while(itrLessons.hasNext()) {
			Lesson lesson = itrLessons.next();
			
			if(this.isCompositeGroup(lesson.getGroup().getId())) {
				String[] compositeGroups = lesson.getGroup().getId().split(",");
				for(int i = 0; i < compositeGroups.length; i++) {
					compositeGroups[i] = compositeGroups[i].trim();
					
					this.sds.insertLesson(lesson.getGroup().getDiscipline().getId(), compositeGroups[i], lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
				}
			}
			else
			{
				this.sds.insertLesson(lesson.getGroup().getDiscipline().getId(), lesson.getGroup().getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
			}
		}
	}
	
	private List<Group> getUniqueGroupsFromLessons(List<Lesson> lessons) {
		List<Group> groups = new ArrayList<Group>();
		Iterator<Lesson> itrLesson = lessons.iterator();
        
        while(itrLesson.hasNext()) {
        	Lesson currLesson = itrLesson.next();
        	
        	if(!this.isCompositeGroup(currLesson.getGroup().getId())) {
        		groups.add(currLesson.getGroup());
        	}
        }
        
        return groups;
	}
	
	private List<Discipline> getUniqueDisciplinesFromGroups(List<Group> groups) {
		List<Discipline> disciplines = new ArrayList<Discipline>();
		Iterator<Group> itrGroups = groups.iterator();
        
        while(itrGroups.hasNext()) {
        	Discipline discipline = itrGroups.next().getDiscipline();
        	
        	Iterator<Discipline> itrDisciplines = disciplines.iterator();
        	
        	boolean disciplineFound = false;
        	
        	while(itrDisciplines.hasNext()) {
        		Discipline searchedDiscipline = itrDisciplines.next(); 
        		
        		if(searchedDiscipline.getId().equals(discipline.getId())) {
        			disciplineFound = true;
        		}
        	}
        	
        	if(!disciplineFound) {
        		disciplines.add(discipline);
        	}
        }
        
        return disciplines;
	}
	
	private List<Lesson> getLessonsFromSheet(Sheet sheet) {
		Lesson currLesson;
        
        List<Lesson> similarLessons = new ArrayList<Lesson>();
        
        List<Lesson> lessons = new ArrayList<Lesson>();
        
        Iterator<Row> itrDemands = sheet.iterator();
        
        // Jump the line of the column labels
 		if(itrDemands.hasNext()) {
 			itrDemands.next();
 		}
        
 		while (itrDemands.hasNext()) {
 			Row row = itrDemands.next();
 			
 			String disciplineId = row.getCell(CellInfoDemand.ID.ordinal()).getStringCellValue().trim();
 			
 			if(!disciplineId.isEmpty()) {
 				currLesson = this.getLessonFromRow(row);
	            
	            if(!similarLessons.isEmpty()) {
	            	if(!isSameLesson(currLesson, similarLessons.get(similarLessons.size() - 1))) {
	            		lessons.add(this.mergeSimilarLessons(similarLessons));
	            		
	            		similarLessons.clear();
	            	}
	            	
	            }
	            
	            similarLessons.add(currLesson);
 			}
        }
 		
 		// In case that similarLessons contains valid lessons
 		if(!similarLessons.isEmpty()) {
 			lessons.add(this.mergeSimilarLessons(similarLessons));
 			similarLessons.clear();
 		}
 		
        return lessons;
	}
	
	private Lesson getLessonFromRow(Row row) {
		String disciplineId = row.getCell(CellInfoDemand.ID.ordinal()).getStringCellValue().trim();
		
		String disciplineName = row.getCell(CellInfoDemand.NAME.ordinal()).getStringCellValue().trim();
        
        String groupId = row.getCell(CellInfoDemand.ID2.ordinal()).getStringCellValue().trim();
        
        String groupTeacher = row.getCell(CellInfoDemand.TEACHER.ordinal()).getStringCellValue().trim();
        
        int groupNumStudents = (int) row.getCell(CellInfoDemand.NUMBER_OF_STUDENTS.ordinal()).getNumericCellValue();
        
        LocalTime beginTime = LocalTime.parse(row.getCell(CellInfoDemand.START_TIME.ordinal()).getStringCellValue().trim());
        
        int durationMinutes = DEFAULT_DURATION_TIME;
        Cell durationCell = row.getCell(CellInfoDemand.DURATION.ordinal());
        		
        if(durationCell.getCellTypeEnum() != CellType.BLANK) {
        	durationMinutes = Integer.parseInt(durationCell.getStringCellValue());
        }
        
        LocalTime durationTime = LocalTime.of(0, 0).plusMinutes(durationMinutes);
        
        List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
        daysOfWeek.add(DayOfWeek.of((int) row.getCell(CellInfoDemand.WEEKDAY.ordinal()).getNumericCellValue()));
        
        String featureId = "";
        
        try {
	        if(row.getCell(CellInfoDemand.FEATURE_IDS.ordinal()).getCellTypeEnum() == CellType.NUMERIC) {
	    		featureId = Integer.toString((int) row.getCell(CellInfoDemand.FEATURE_IDS.ordinal()).getNumericCellValue());
	    	} else {
	    		featureId = row.getCell(CellInfoDemand.FEATURE_IDS.ordinal()).getStringCellValue();
	    	}
	        
        } catch(NullPointerException e) {
        	featureId = "";
        }
        
        Map<Resource, Integer> reqResources = this.convertToResourceList(featureId);
        	
        reqResources.put(ScholarResource.PLACES, groupNumStudents);
        
        Discipline currDiscipline = new Discipline(disciplineId, disciplineName);
        Group currGroup = new Group(currDiscipline, groupId, groupTeacher, groupNumStudents);
        
        return new Lesson(currGroup, beginTime, durationTime, daysOfWeek, reqResources);
	}
	
	private boolean isSameLesson(Lesson reference, Lesson comparing) {
		if(reference.equals(null) || comparing.equals(null)) {
			return false;
		}
		
		boolean sameDiscipline = reference.getGroup().getDiscipline().getId().equals(comparing.getGroup().getDiscipline().getId());
		boolean sameGroup = reference.getGroup().getId().equals(comparing.getGroup().getId());
		boolean sameResources = reference.getResources().equals(comparing.getResources());
		
		return (sameDiscipline && sameGroup && sameResources);
	}
	
	private Lesson mergeSimilarLessons(List<Lesson> similarLessons) {
		Lesson lesson;
		Lesson currLesson;
		Lesson sampleLesson = similarLessons.get(0);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		
		Iterator<Lesson> itrSimilarLessons = similarLessons.iterator();
		
		while(itrSimilarLessons.hasNext()) {
			currLesson = itrSimilarLessons.next();
			
			daysOfWeek.addAll(currLesson.getDaysOfWeek());
		}
		
		lesson = new Lesson(sampleLesson.getGroup(), sampleLesson.getBegin(), sampleLesson.getDuration(), daysOfWeek, sampleLesson.getResources());
		
		return lesson;
		
	}
	
	private boolean isCompositeGroup(String groupId) {
		return groupId.contains(",");
	}
	
	private void populateClassroomsFromWorkbook(Workbook workbook) {
		Sheet sheetClassrooms = workbook.getSheetAt(SHEET_OF_CLASSROOMS_INDEX);
		
		List<Classroom> classrooms = this.getClassroomsFromSheet(sheetClassrooms);
		
		Iterator<Classroom> itrClassrooms = classrooms.iterator();
		
		while(itrClassrooms.hasNext()) {
			Classroom classroom = itrClassrooms.next(); 
					
			this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), classroom.getResources());
		}
	}
	
	private List<Classroom> getClassroomsFromSheet(Sheet sheet) {
		List<Classroom> classrooms = new ArrayList<Classroom>();
        
		Iterator<Row> itrClassrooms = sheet.iterator();
        
		// Jump the line of the column labels
 		if(itrClassrooms.hasNext()) {
 			itrClassrooms.next();
 		}
		
        while (itrClassrooms.hasNext()) {
        	Row row = itrClassrooms.next();
 			
 			String building = row.getCell(CellInfoClassroom.BUILDING.ordinal()).getStringCellValue().trim();
 			
 			if(!building.isEmpty()) {
 				classrooms.add(this.getClassroomFromRow(row));
 			}
        }
        
        return classrooms;
	}
	
	private Classroom getClassroomFromRow(Row row) {
		String buildingId = row.getCell(CellInfoClassroom.BUILDING.ordinal()).getStringCellValue();
		
		String roomId = row.getCell(CellInfoClassroom.ROOM.ordinal()).getStringCellValue();
		
		int numOfPlaces = (int) row.getCell(CellInfoClassroom.NUMBER_OF_PLACES.ordinal()).getNumericCellValue();
		
		Map<Resource, Integer> availResources = this.convertToResourceList(row.getCell(CellInfoClassroom.FEATURE_IDS7.ordinal()).getStringCellValue());
		availResources.put(ScholarResource.PLACES, numOfPlaces);
		
		return new Classroom(buildingId, roomId, availResources);
	}
	
	private Map<Resource, Integer> convertToResourceList(String featureId) {
		Map<Resource, Integer> resources = new HashMap<Resource, Integer>();
		
		if(!featureId.isEmpty()) {
			String[] features = featureId.split(",");
			
			for(int i = 0; i < features.length; i++) {
				features[i] = features[i].trim();
				
				resources.put(ScholarResource.fromValue(Integer.parseInt(features[i])), DEFAULT_RESOURCE_QUANTITY);
			}
			
		}
		
		return resources;
	}

}

enum CellInfoDemand {
	NAME,
	ID,
	NUMBER_OF_STUDENTS,
	TEACHER,
	ID2,
	ROOM_ID,
	DURATION,
	BUILDING_ID,
	WEEKDAY,
	START_TIME,
	REQUIRES_BULDING_ID,
	REQUIRES_ROOM_ID,
	FEATURE_IDS;
}

enum CellInfoClassroom {
	BUILDING,
	ROOM,
	FEATURE_IDS7,
	NUMBER_OF_PLACES,
	AVAILABLE_FOR_LOCATION,
	NOTE;
}

enum CellInfoWrite {
	DISCIPLINE,
	GROUP,
	TEACHER,
	START_TIME,
	DURATION,
	DAYS_OF_WEEK,
	PLACE;
}