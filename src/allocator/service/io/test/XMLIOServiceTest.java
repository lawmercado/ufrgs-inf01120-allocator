package allocator.service.io.test;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;


import allocator.data.domain.Allocable;
import allocator.data.domain.Classroom;
import allocator.data.domain.Discipline;
import allocator.data.domain.Group;
import allocator.data.domain.Lesson;
import allocator.data.domain.Resource;
import allocator.data.domain.ScholarReservation;
import allocator.data.domain.ScholarResource;
import allocator.service.data.ScholarDataServiceImpl;

import allocator.service.io.*;


public class XMLIOServiceTest  {

	private final String INPUT_VALID_TEST_FILE_PATH = "src/allocator/service/io/test/testFile.xml";
	private final String OUTPUT_VALID_TEST_FILE_PATH = "src/allocator/service/io/test/testFile_out.xml";
	
	private ScholarDataServiceImpl sds;
	private XMLIOService fios;
	@Before
	public void setUp(){
		this.sds = new ScholarDataServiceImpl();
		this.fios = new XMLIOService(this.sds);
	}
	@Test
	public void testPopulateFromFile() throws Exception{
		Discipline discipline = new Discipline("INF01120", "TÉCNICAS DE CONSTRUÇÃO DE PROGRAMAS");
		Integer groupSize = 45;
		Group group = new Group(discipline, "U", "ERIKA FERNANDES COTA", groupSize);
		
		LocalTime begin = LocalTime.of(15,30);
		LocalTime duration = LocalTime.of(2, 0);
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.TUESDAY);
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		Lesson lesson = new Lesson(group,begin, duration, daysOfWeek, reqResources );
		
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 40);
		availResources.put(ScholarResource.FORMAL_PRESENTATIONS, 1);
		availResources.put(ScholarResource.CLASSROOM, 1);
		Classroom classroom = new Classroom("43424(72)", "102", availResources);
		
		this.fios.populateFromFile(INPUT_VALID_TEST_FILE_PATH);
		
		List<Discipline> disciplines = this.sds.getDisciplines();
		List<Group> groups = new ArrayList<Group>();
		groups = this.sds.getGroups(disciplines.get(63).getId()) ;
		List<Allocable> lessons = new ArrayList<Allocable>();
		lessons = this.sds.getLessons(disciplines.get(63).getId(), groups.get(0).getId());
		
		//63 � o indice de tcp na lista de discplines
		assertEquals(discipline.getId(), disciplines.get(63).getId());
		assertEquals(discipline.getName(), disciplines.get(63).getName());
		assertEquals(group.getId(), groups.get(0).getId());
		assertEquals(group.getNumStudents(), groups.get(0).getNumStudents());
		assertEquals(group.getTeacher(), groups.get(0).getTeacher());
		
		for (int k = 0; k < lessons.size(); k++){
			
			LocalTime beginTime = Lesson.getBeginTimeFromAllocable(lessons.get(k));
			LocalTime durationTime = Lesson.getDurationTimeFromAllocable(lessons.get(k));
			List<DayOfWeek> weekDay = Lesson.getDaysOfWeekFromAllocable(lessons.get(k));
			Map<Resource, Integer> reqFeatures = lessons.get(k).getResources();
			assertEquals(lesson.getBegin(), beginTime);
			assertEquals(lesson.getDuration(), durationTime);
			assertEquals(lesson.getDaysOfWeek().get(k), weekDay.get(0));
			assertEquals(lesson.getResources(), reqFeatures);
			
			}
		
		
		
		
		
		LocalDate from = LocalDate.of(2017, 1, 1);
		LocalDate to = LocalDate.of(2017, 7, 1);
		
		List<Allocable> classrooms = this.sds.getAvailableClassrooms(begin, duration, daysOfWeek, from, to);
		boolean boolRoom = false;
		int i = 0;
		while (boolRoom == false || i < classrooms.size()){
		
			String room = Classroom.getRoomFromAllocable(classrooms.get(i));
			String building = Classroom.getBuildingFromAllocable(classrooms.get(i));
			Map<Resource, Integer> avaibleResources = classrooms.get(i).getResources();
			
			if (room.equals(classroom.getRoom())&&building.equals(classroom.getBuilding())&&avaibleResources.get(ScholarResource.PLACES).equals(classroom.getResources().get(ScholarResource.PLACES))){
				boolRoom = true;
				assertEquals(avaibleResources.get(ScholarResource.FORMAL_PRESENTATIONS), classroom.getResources().get(ScholarResource.FORMAL_PRESENTATIONS) );
				assertEquals(avaibleResources.get(ScholarResource.CLASSROOM), classroom.getResources().get(ScholarResource.CLASSROOM) );
			}
			i++;
		}
		assertTrue(boolRoom);
	}
	@Test
	public void testWrite(){
		
		String buildingId = "43425(73)";
		String roomId = "107";
		String courseId = "INF01120";
		String groupId = "U";
		Discipline discipline = new Discipline(courseId, "TÉCNICAS DE CONSTRUÇÃO DE PROGRAMAS");
		Group group = new Group(discipline, groupId, "ERIKA FERNANDES COTA", 45);		
		
		LocalTime begin = LocalTime.of(15,30);
		LocalTime duration = LocalTime.of(2, 0);
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.TUESDAY);
		daysOfWeek.add(DayOfWeek.THURSDAY);
		LocalDate from = LocalDate.of(2017, 1, 20);
		LocalDate to = LocalDate.of(2017, 7, 20);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		//Lesson lesson = new Lesson(group,begin, duration, daysOfWeek, reqResources );
		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		this.sds.insertLesson(discipline.getId(), group.getId(), begin, duration, daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 45);
		availResources.put(ScholarResource.FORMAL_PRESENTATIONS, 1);
		availResources.put(ScholarResource.CLASSROOM, 1);
		Classroom classroom = new Classroom(buildingId, roomId, availResources);
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), classroom.getResources());
		
		this.sds.insertReservation(buildingId, roomId, discipline.getId(), group.getId(), begin, duration, daysOfWeek, from, to);
		List<ScholarReservation> reservation = new ArrayList<ScholarReservation>();
		reservation = this.sds.getReservations();
		assertEquals(reservation.size(), 1);
		try {
			this.fios.saveToFile(OUTPUT_VALID_TEST_FILE_PATH);
		} catch (FileNotFoundException e) {
			fail();
			e.printStackTrace();
		}
	}
	
	
	
}
