package algorithm.test;

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

public class OurAllocateAlgorithmTest {

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
	public void testAlocate1() //CLASSROOMS PLACES = E RESOURCES !=
	{
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 80);
		availResources2.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);
		
		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom1.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom1.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAlocate2() //CLASSROOMS PLACES != E RESOURCES =
	{
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 70);
		availResources2.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);
		
		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom1.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom1.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAlocate3() //CLASSROOMPLACES = LESSONPLACES, PORÉM RESOURCES NÃO FECHAM
	{						   //SEMPRE DA ERRADO
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 80);
		availResources2.put(ScholarResource.INTRODUCTION_TEACHING_LAB, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);

		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		
		assertEquals(local, null);
		
	}
	
	@Test
	public void testAlocate4() //CLASSROOMPLACES = LESSONPLACES PORÉM NÃO HÁ NENHUM RESOURCE
	{						   //SEMPRE DA ERRADO
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 90);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);
		
		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		assertEquals(local, null);
		
	}
	
	@Test
	public void testAlocate5() //CLASSROOMPLACES NÃO FECHA COM LESSONPLACES PORÉM RESOURCE FECHA
	{						   //SEMPRE DA ERRADO
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 70);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 60);
		availResources2.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);
		
		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		assertEquals(local, null);
		
	}
	
	@Test
	public void testAlocate6() //CLASSROOMPLACES FECHA COM LESSONPLACES EM AMBAS, RESOURCE FECHA EM AMBAS, UMA COM MAIS RESOURCES E OUTRA COM MENOS RESOURCES
	{						   //ESCOLHER CLASSROOM COM MENOS RESOURCES EXTRAS
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 80);
		availResources2.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources2.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);

		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom1.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom1.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAlocate7() //CLASSROOMPLACES FECHA COM LESSONPLACES EM AMBAS, RESOURCE FECHA EM AMBAS, UMA COM MAIS RESOURCES E OUTRA COM MENOS RESOURCES
	{						   //ESCOLHER CLASSROOM COM MENOS RESOURCES EXTRAS
		//System.out.println("TESTE 7");
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources1.put(ScholarResource.MOTORIZED_SCREEN, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 80);
		availResources2.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources2.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		availResources2.put(ScholarResource.SOUND_AND_MICROPHONE, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);

		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom1.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom1.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAlocate8() //CLASSROOMPLACES FECHA COM LESSONPLACES EM AMBAS, RESOURCE FECHA EM AMBAS, UMA COM MAIS RESOURCES E OUTRA COM MENOS RESOURCES
	{						   //ESCOLHER CLASSROOM COM MENOS RESOURCES EXTRAS
		//System.out.println("TESTE 8");
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources1.put(ScholarResource.MOTORIZED_SCREEN, 1);
		availResources1.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 80);
		availResources2.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources2.put(ScholarResource.SOUND_AND_MICROPHONE, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);

		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom2.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom2.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testAlocate9() //CLASSROOMPLACES FECHA COM LESSONPLACES EM MAIS DE UMA, RESOURCE FECHA EM MAIS DE UMA, UMA COM MAIS RESOURCES E OUTRA COM MENOS RESOURCES E OUTRA SEM LUGARES CERTOS
	{						   //ESCOLHER CLASSROOM COM MENOS RESOURCES EXTRAS E COM LUGARES DEFINIDOS
		//System.out.println("TESTE 9");
		Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
		Group group = new Group(discipline, "A", "ÉRIKA COTA", 80);
		
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.THURSDAY);
		Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>(); // Resources Lesson
		reqResources.put(ScholarResource.PLACES, group.getNumStudents());
		reqResources.put(ScholarResource.VIDEO_CONFERENCE, 1);
		reqResources.put(ScholarResource.MOTORIZED_SCREEN, 1);
		
		Lesson lesson = new Lesson(group, LocalTime.of(10,10), LocalTime.of(1,40), daysOfWeek, reqResources);
		
		Map<Resource, Integer> availResources1 = new HashMap<Resource, Integer>(); // Resources Classroom 1
		availResources1.put(ScholarResource.PLACES, 80);
		availResources1.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources1.put(ScholarResource.MOTORIZED_SCREEN, 1);
		availResources1.put(ScholarResource.HARDWARE_TEACHING_LABORATORY, 1);
		
		Classroom classroom1 = new Classroom("45425", "108", availResources1);
		
		Map<Resource, Integer> availResources2 = new HashMap<Resource, Integer>(); // Resources Classroom 2
		availResources2.put(ScholarResource.PLACES, 70);
		availResources2.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources2.put(ScholarResource.MOTORIZED_SCREEN, 1);
		
		Classroom classroom2 = new Classroom("45324", "118", availResources2);
		
		Map<Resource, Integer> availResources3 = new HashMap<Resource, Integer>(); // Resources Classroom 3
		availResources3.put(ScholarResource.PLACES, 80);
		availResources3.put(ScholarResource.VIDEO_CONFERENCE, 1);
		availResources3.put(ScholarResource.MOTORIZED_SCREEN, 1);
		availResources3.put(ScholarResource.MEETINGS, 1);
		availResources3.put(ScholarResource.INTRODUCTION_TEACHING_LAB, 1);
		
		Classroom classroom3 = new Classroom("45324", "118", availResources3);


		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		availClassrooms.add(classroom1);
		availClassrooms.add(classroom2);
		availClassrooms.add(classroom3);
		
		Allocable local = this.algorithm.run(lesson, availClassrooms);
		try {
			assertEquals(Classroom.getBuildingFromAllocable(local), classroom1.getBuilding());
			assertEquals(Classroom.getRoomFromAllocable(local), classroom1.getRoom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
