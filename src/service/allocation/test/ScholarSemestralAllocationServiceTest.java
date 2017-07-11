package service.allocation.test;

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

import algorithm.AllocationAlgorithm;
import algorithm.OurAllocateAlgorithm;
import service.allocation.*;
import data.domain.*;
import data.service.ScholarDataService;
import data.service.impl.ScholarDataServiceImpl;

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
		this.sds.insertDiscipline(discipline.getId(), discipline.getName());
		
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		this.sds.insertGroup(discipline.getId(), group.getId(), group.getTeacher(), group.getNumStudents());
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		daysOfWeek.add(DayOfWeek.TUESDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
			
		this.sds.insertLesson(discipline.getId(), group.getId(), LocalTime.of(10, 30), LocalTime.of(1, 40),
				daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		this.sds.insertClassroom(classroom1.getBuilding(), classroom1.getRoom(), availResources1);
		
		this.sas.execute();
		
		List<Allocable> availClassrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		assertEquals(availClassrooms.size(), 0);
		
	}
}



