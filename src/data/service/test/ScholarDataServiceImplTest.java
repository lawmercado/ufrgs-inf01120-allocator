package data.service.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;

import data.domain.*;
import data.service.impl.*;

public class ScholarDataServiceImplTest {

	private ScholarDataServiceImpl sds;

	@Before
	public void setUp() {
		this.sds = new ScholarDataServiceImpl();
	}

	@Test
	public void testInsertDiscipline() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());

		List<Discipline> disciplines = this.sds.getDisciplines();

		assertEquals(disciplines.get(0).getId(), discipline.getId());
		assertEquals(disciplines.get(0).getName(), discipline.getName());

	}

	@Test
	public void testInsertGroup() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());

		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());

		List<Group> groups = this.sds.getGroups(discipline.getId());

		assertEquals(groups.get(0).getId(), group.getId());
		assertEquals(groups.get(0).getTeacher(), group.getTeacher());
		assertEquals(groups.get(0).getNumStudents(), group.getNumStudents());

	}
	
	@Test
	public void testInsertLesson() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());

		List<Allocable> lessons = this.sds.getLessons(discipline.getId(), group.getId());

		assertEquals(lessons.size(), 0);

		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);

		lessons = this.sds.getLessons(discipline.getId(), group.getId());
					
		assertEquals(lessons.size(), 1);

	}
	
	@Test
	public void testInsertClassroom() {
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 80);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		List<DayOfWeek> lessonDaysOfWeek = new ArrayList<DayOfWeek>();
		lessonDaysOfWeek.add(DayOfWeek.TUESDAY);
		lessonDaysOfWeek.add(DayOfWeek.THURSDAY);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), lessonDaysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 0);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), lessonDaysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		
		assertEquals(availClassrooms.size(), 1);
		
	}
	
	@Test
	public void testInsertReservation() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);

		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 80);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 1);
		
		this.sds.insertReservation(classroom.getBuilding(), classroom.getRoom(), group.getDiscipline().getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));

		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 0);
	}
	
	@Test
	public void testLessonHasReservation() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);

		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 80);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 1);
		
		this.sds.insertReservation(classroom.getBuilding(), classroom.getRoom(), group.getDiscipline().getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));

		boolean lessonHasReservation = this.sds.lessonHasReservation(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		
		assertEquals(lessonHasReservation, true);
	}
	
	@Test
	public void testLessonHasReservationFalse() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);

		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.PLACES, 80);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 1);
		
		boolean lessonHasReservation = this.sds.lessonHasReservation(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		
		assertEquals(lessonHasReservation, false);
	}
	
	@Test
	public void testGetRelatedGroups() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Discipline discipline2 = new Discipline("INF01121", "Técnicas de Construção de Programas 2");

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertDiscipline(discipline2.getId(), discipline2.getName());
		
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);
		Group relatedGroup = new Group(discipline2, "B", "ÉRIKA COTA", 40);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		
		Lesson lesson = new Lesson(group, LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, reqResources);
		
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		this.sds.insertGroup(discipline2.getId(), relatedGroup.getId(), relatedGroup.getTeacher(), relatedGroup.getNumStudents());
		
		this.sds.insertLesson(discipline.getId(), group.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		this.sds.insertLesson(discipline2.getId(), relatedGroup.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		
		List<Group> relatedGroups = this.sds.getRelatedGroups(group.getDiscipline().getId(), group.getId(), lesson.getBegin(), lesson.getDaysOfWeek());
		
		assertEquals(relatedGroups.get(0).getId(), relatedGroup.getId());
		assertEquals(relatedGroups.get(0).getDiscipline().getId(), relatedGroup.getDiscipline().getId());
	}
	
	@Test
	public void testGetRelatedGroupsWhenHasNoGroups() {
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Discipline discipline2 = new Discipline("INF01121", "Técnicas de Construção de Programas 2");

		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		this.sds.insertDiscipline(discipline2.getId(), discipline2.getName());
		
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);
		Group relatedGroup = new Group(discipline2, "B", "ÉRIKA COTA 2", 40);

		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		this.sds.insertGroup(discipline2.getId(), relatedGroup.getId(), relatedGroup.getTeacher(), relatedGroup.getNumStudents());
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);

		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		
		Lesson lesson = new Lesson(group, LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, reqResources);
		
		this.sds.insertLesson(discipline.getId(), group.getId(), lesson.getBegin(), lesson.getDuration(), lesson.getDaysOfWeek(), lesson.getResources());
		
		List<Group> relatedGroups = this.sds.getRelatedGroups(group.getDiscipline().getId(), group.getId(), lesson.getBegin(), lesson.getDaysOfWeek());
		
		assertEquals(relatedGroups.size(), 0);
	}
}
