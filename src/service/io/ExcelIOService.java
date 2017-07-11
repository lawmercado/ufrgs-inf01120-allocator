package service.io;

import data.service.ScholarDataService;
import data.domain.*;

import service.FileIOService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	
	private ScholarDataService sds;
	
	@Override
	public void populateFromFile(ScholarDataService sds, String filePath) {
		this.sds = sds;
		
		try {
			FileInputStream excelFile = new FileInputStream(new File(filePath));
			Workbook workbook = new XSSFWorkbook(excelFile);
	        
	        this.populateDemandsFromWorkbook(workbook);
	        
	        this.populateClassroomsFromWorkbook(workbook);
			
	        workbook.close();
	        
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	}

	@Override
	public void write(String path) {

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
        	
        	if(!disciplines.contains(discipline)) {
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
        
        while (itrDemands.hasNext()) {
        	currLesson = this.getLessonFromRow(itrDemands.next());
            
            if(!similarLessons.isEmpty()) {
            	if(isSameLesson(currLesson, similarLessons.get(similarLessons.size() - 1))) {
            		similarLessons.add(currLesson);
            		
            	} else {
            		lessons.add(this.mergeSimilarLessons(similarLessons));
            		
            		similarLessons.clear();
            		similarLessons.add(currLesson);
            	}
            	
            } else {
            	similarLessons.add(currLesson);
            }

        }
        
        return lessons;
	}
	
	private Lesson getLessonFromRow(Row row) {
		String disciplineId = row.getCell(CellInfoDemand.ID.ordinal()).getStringCellValue();
		
		String disciplineName = row.getCell(CellInfoDemand.NAME.ordinal()).getStringCellValue();
        
        String groupId = row.getCell(CellInfoDemand.ID2.ordinal()).getStringCellValue();
        
        String groupTeacher = row.getCell(CellInfoDemand.TEACHER.ordinal()).getStringCellValue();
        
        int groupNumStudents = (int) row.getCell(CellInfoDemand.NUM_OF_SUDENTS.ordinal()).getNumericCellValue();
        
        LocalTime beginTime = LocalTime.parse(row.getCell(CellInfoDemand.START_TIME.ordinal()).getStringCellValue());
        
        LocalTime durationTime = LocalTime.of(0, 0).plusMinutes((int) row.getCell(CellInfoDemand.DURATION.ordinal()).getNumericCellValue());
        
        List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
        daysOfWeek.add(DayOfWeek.of((int) row.getCell(CellInfoDemand.NUM_OF_SUDENTS.ordinal()).getNumericCellValue()));
        
        Map<Resource, Integer> reqResources = this.convertToResourceList(row.getCell(CellInfoDemand.FEATURE_IDS.ordinal()).getStringCellValue());
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
		boolean sameBeginTime = reference.getBegin().equals(comparing.getBegin());
		boolean sameDurationTime = reference.getDuration().equals(comparing.getDuration());
		boolean sameResources = reference.getResources().equals(comparing.getResources());
		
		return (sameDiscipline && sameGroup && sameBeginTime && sameDurationTime && sameResources);
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
        
        while (itrClassrooms.hasNext()) {
        	classrooms.add(this.getClassroomFromRow(itrClassrooms.next()));
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
	NUM_OF_SUDENTS,
	TEACHER,
	ID2,
	ROOM_ID,
	DURATION,
	BUILDING_ID,
	WEEKDAY,
	START_TIME,
	REQUIRES_BUILDING_ID,
	REQUIRES_ROOM_ID,
	FEATURE_IDS,
	FEATURES_IDS,
	NAME2,
	ID3,
	HIDDEN,
	ID4,
	ID5,
	FEATURE_IDS6,
	NUMBER_OF_PLACES,
	AVAILABE_FOR_ALLOCATION,
	NOTE;
}

enum CellInfoClassroom {
	BUILDING,
	ROOM,
	FEATURE_IDS7,
	NUMBER_OF_PLACES,
	AVAILABLE_FOR_LOCATION,
	NOTE;
}