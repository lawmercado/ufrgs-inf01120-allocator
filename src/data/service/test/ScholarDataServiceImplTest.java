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
		try {
			Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
			Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

			List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
			daysOfWeek.add(DayOfWeek.THURSDAY);
			daysOfWeek.add(DayOfWeek.TUESDAY);

			Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
			reqResources.put(ScholarResource.Lugares, group.getNumStudents());

			this.sds.insertDiscipline(discipline.getId(), discipline.getName());
			this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());

			List<Allocable> lessons = this.sds.getLessons(discipline.getId(), group.getId());

			assertEquals(lessons.size(), 0);

			this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 10), LocalTime.of(1, 10),
					daysOfWeek, reqResources);

			lessons = this.sds.getLessons(discipline.getId(), group.getId());

			assertEquals(lessons.size(), 1);

		} catch (Exception e) {
			fail("Failed due to unexpected error!");
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInsertClassroom() {
		Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
		availResources.put(ScholarResource.Lugares, 80);
		Classroom classroom = new Classroom("45425", "108", availResources);
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 10), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 0);
		
		this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
		
		availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 10), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		
		assertEquals(availClassrooms.size(), 1);
		
	}
	
	@Test
	public void testInsertReservation() {
		try {
			Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
			Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);

			List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
			daysOfWeek.add(DayOfWeek.THURSDAY);
			daysOfWeek.add(DayOfWeek.TUESDAY);

			Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
			reqResources.put(ScholarResource.Lugares, group.getNumStudents());

			this.sds.insertDiscipline(discipline.getId(), discipline.getName());
			this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
			
			this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 10), LocalTime.of(1, 10),
					daysOfWeek, reqResources);

			Map<Resource, Integer> availResources = new HashMap<Resource, Integer>();
			availResources.put(ScholarResource.Lugares, 80);
			Classroom classroom = new Classroom("45425", "108", availResources);
			
			this.sds.insertClassroom(classroom.getBuilding(), classroom.getRoom(), availResources);
			
			List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 10), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
			assertEquals(availClassrooms.size(), 1);
			
			this.sds.insertReservation(classroom.getBuilding(), classroom.getRoom(), group.getDiscipline().getId(), group.getId(), LocalTime.of(10, 10), LocalTime.of(1, 10), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));

			availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 10), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
			assertEquals(availClassrooms.size(), 0);
			
		} catch (Exception e) {
			fail("Failed due to unexpected error!");
			e.printStackTrace();
		}
	}
	
}
