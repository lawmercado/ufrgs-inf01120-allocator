package allocator.service.allocation.test;

import static org.junit.Assert.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import allocator.algorithm.AllocationAlgorithm;
import allocator.algorithm.impl.OurAllocateAlgorithm;
import allocator.service.allocation.*;
import allocator.data.domain.*;
import allocator.service.ScholarDataService;
import allocator.service.data.ScholarDataServiceImpl;

public class ScholarSemestralAllocationServiceTest {

	private ScholarSemestralAllocationService sas;
	private AllocationAlgorithm algorithm;
	private ScholarDataService sds;
	private LocalDate semesterBegin;
		
	@Before
	public void setUp() 
	{
		this.algorithm = new OurAllocateAlgorithm();
		this.semesterBegin = LocalDate.of(2016,1,1);
		this.sds = new ScholarDataServiceImpl();
		this.sas = new ScholarSemestralAllocationService(algorithm, sds, semesterBegin);
	}
	

	@Test
	public void testExecute() 
	{
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);
		
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());			
		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 80);
		availResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		assertEquals(availClassrooms.size(), 1);
		
		this.sas.execute();
		
		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		List<ScholarReservation> reservations = this.sds.getReservations();
		assertEquals(reservations.size(), 1);
		assertEquals(availClassrooms.size(), 0);
		
	}

	@Test
	public void testExecuteWithRelatedGroups() 
	{
		//System.out.println("@@@TESTE 2@@@");
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Discipline discipline2 = new Discipline("INF01121", "Técnicas de Construção de Programas 2");
		
		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertDiscipline(discipline2.getId(), discipline2.getName());
		
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		Group relatedGroup = new Group(discipline2, "B", "ÉRIKA COTA", 20);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);
		
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, reqResources);
		
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		this.sds.insertGroup(discipline2.getId(), relatedGroup.getId(), relatedGroup.getTeacher(), relatedGroup.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		this.sds.insertLesson(discipline2.getId(), relatedGroup.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
	
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 100);
		availResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		assertEquals(availClassrooms.size(), 1);
		
		this.sas.execute();
		
		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		List<ScholarReservation> reservations = this.sds.getReservations();
		assertEquals(reservations.size(), 2);
		assertEquals(availClassrooms.size(), 0);
		
	}

	@Test
	public void testExecuteWithTwoRelatedGroups() 
	{
		//System.out.println("@@@TESTE 2@@@");
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Discipline discipline2 = new Discipline("INF01121", "Técnicas de Construção de Programas 2");
		Discipline discipline3 = new Discipline("INF01122", "Técnicas de Construção de Programas 3");
		
		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertDiscipline(discipline2.getId(), discipline2.getName());
		this.sds.insertDiscipline(discipline3.getId(), discipline3.getName());
		
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		Group relatedGroup = new Group(discipline2, "B", "ÉRIKA COTA", 20);
		Group relatedGroup2 = new Group(discipline3, "C", "ÉRIKA COTA", 20);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);
		
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, reqResources);
		
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		this.sds.insertGroup(discipline2.getId(), relatedGroup.getId(), relatedGroup.getTeacher(), relatedGroup.getNumStudents());
		this.sds.insertGroup(discipline3.getId(), relatedGroup2.getId(), relatedGroup2.getTeacher(), relatedGroup2.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		this.sds.insertLesson(discipline2.getId(), relatedGroup.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		this.sds.insertLesson(discipline3.getId(), relatedGroup2.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
	
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 120);
		availResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		assertEquals(availClassrooms.size(), 1);
		
		this.sas.execute();
		
		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 1));
		List<ScholarReservation> reservations = this.sds.getReservations();
		assertEquals(reservations.size(), 3);
		assertEquals(availClassrooms.size(), 0);
		
	}
}

