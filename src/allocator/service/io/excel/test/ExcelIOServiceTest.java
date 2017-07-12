package allocator.service.io.excel.test;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import allocator.data.domain.*;
import allocator.data.service.ScholarDataService;
import allocator.data.service.impl.ScholarDataServiceImpl;
import allocator.service.FileIOService;
import allocator.service.io.excel.ExcelIOService;

public class ExcelIOServiceTest {

	private final String INPUT_VALID_TEST_FILE_PATH = "src/allocator/service/io/excel/test/testSheet.xlsx";
	private final String OUTPUT_TEST_FILE_PATH = "src/allocator/service/io/excel/test/testSheet_out.xlsx";
	
	private ScholarDataService sds;
	private FileIOService fios;
	
	@Before
	public void setUp() throws Exception {
		this.sds = new ScholarDataServiceImpl();
		this.fios = new ExcelIOService();
		this.fios.populateFromFile(this.sds, INPUT_VALID_TEST_FILE_PATH);
	}

	@Test
	public void testDisciplinesFromInput() {
		List<Discipline> disciplines = this.sds.getDisciplines();
		
		assertEquals(disciplines.size(), 70);
		
		assertEquals("INF01108", disciplines.get(5).getId());
		assertEquals("ARQUITETURA E ORGANIZAÇÃO DE COMPUTADORES I", disciplines.get(5).getName());
	}
	
	@Test
	public void testGroupsFromInput() {
		List<Group> groups = this.sds.getGroups("INF01202");
		
		assertEquals(groups.size(), 6);
		
		assertEquals(groups.get(0).getId(), "A");
		assertEquals(groups.get(0).getNumStudents(), 20);
		assertEquals(groups.get(0).getTeacher(), "ANDERSON MACIEL");
		
		assertEquals(groups.get(1).getId(), "B");
		assertEquals(groups.get(1).getNumStudents(), 20);
		assertEquals(groups.get(1).getTeacher(), "ANDERSON MACIEL");
		
		assertEquals(groups.get(2).getId(), "C");
		assertEquals(groups.get(2).getNumStudents(), 23);
		assertEquals(groups.get(2).getTeacher(), "ULISSES BRISOLARA CORREA");
		
		assertEquals(groups.get(3).getId(), "D");
		assertEquals(groups.get(3).getNumStudents(), 17);
		assertEquals(groups.get(3).getTeacher(), "ULISSES BRISOLARA CORREA");
		
		assertEquals(groups.get(4).getId(), "E");
		assertEquals(groups.get(4).getNumStudents(), 22);
		assertEquals(groups.get(4).getTeacher(), "MARA ABEL");
		
		assertEquals(groups.get(5).getId(), "F");
		assertEquals(groups.get(5).getNumStudents(), 17);
		assertEquals(groups.get(5).getTeacher(), "MARA ABEL");
	}
	
	public void testLessonsFromInput() {
		List<Group> groups = this.sds.getGroups("INF01202");
		
		List<DayOfWeek> daysOfWeek1 = new ArrayList<DayOfWeek>();
		daysOfWeek1.add(DayOfWeek.TUESDAY);
		daysOfWeek1.add(DayOfWeek.THURSDAY);
		
		List<Allocable> lessons = this.sds.getLessons(groups.get(0).getDiscipline().getId(), groups.get(0).getId());
		assertEquals(2, lessons.size());
		
		assertEquals(daysOfWeek1, Lesson.getDaysOfWeekFromAllocable(lessons.get(0)));
		assertEquals(1, lessons.get(0).getResources().size());
		
		List<DayOfWeek> daysOfWeek2 = new ArrayList<DayOfWeek>();
		daysOfWeek2.add(DayOfWeek.FRIDAY);
		
		assertEquals(daysOfWeek2, Lesson.getDaysOfWeekFromAllocable(lessons.get(1)));
		assertEquals(2, lessons.get(1).getResources().size());
		assertEquals((int) groups.get(0).getNumStudents(), (int) lessons.get(1).getResources().get(ScholarResource.PLACES));
	}
	
	@Test
	public void testClassroomsFromInput() {
		List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.TUESDAY);
		daysOfWeek.add(DayOfWeek.THURSDAY);
		
		List<Allocable> classrooms = this.sds.getAvailableClassrooms(LocalTime.of(10, 30), LocalTime.of(1, 40), daysOfWeek, LocalDate.of(2016, 1, 1), LocalDate.of(2016, 1, 1).plusMonths(6));
		
		assertEquals(28, classrooms.size());
		
		assertEquals("43412(65)", Classroom.getBuildingFromAllocable(classrooms.get(4)));
		assertEquals("220", Classroom.getRoomFromAllocable(classrooms.get(4)));
		assertEquals(4, classrooms.get(4).getResources().size());
		assertEquals(20, (int) classrooms.get(4).getResources().get(ScholarResource.PLACES));
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testInputOfInvalidFile() throws FileNotFoundException {
		this.fios.populateFromFile(this.sds, "TESTE");
	}
	
	@Test
	public void testWrite() {
		for(int i = 0; i < this.sds.getGroups("INF01202").size(); i++) {
			Group sampleGroup = this.sds.getGroups("INF01202").get(i);
			
			Allocable sampleLesson = this.sds.getLessons(sampleGroup.getDiscipline().getId(), sampleGroup.getId()).get(0);
			
			Allocable sampleClassroom = this.sds.getAvailableClassrooms(Lesson.getBeginTimeFromAllocable(sampleLesson), Lesson.getDurationTimeFromAllocable(sampleLesson), Lesson.getDaysOfWeekFromAllocable(sampleLesson), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31)).get(0);
			
			this.sds.insertReservation(Classroom.getBuildingFromAllocable(sampleClassroom), Classroom.getRoomFromAllocable(sampleClassroom), sampleGroup.getDiscipline().getId(), sampleGroup.getId(), Lesson.getBeginTimeFromAllocable(sampleLesson), Lesson.getDurationTimeFromAllocable(sampleLesson), Lesson.getDaysOfWeekFromAllocable(sampleLesson), LocalDate.of(2016, 1, 1), LocalDate.of(2016, 7, 31));
		
		}
	
		this.fios.write(OUTPUT_TEST_FILE_PATH);
	}

}
