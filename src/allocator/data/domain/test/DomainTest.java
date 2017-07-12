package allocator.data.domain.test;

import static org.junit.Assert.*;

import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import allocator.data.domain.*;

public class DomainTest {

	@Test
	public void testGetInfoFromAllocableLesson() {
		try {
			Discipline discipline = new Discipline("INF01120", "Técnicas de Construção de Programas");
			Group group = new Group(discipline, "A", "ÉRIKA COTA", 40);
			
			LocalTime begin = LocalTime.parse("10:30");
			LocalTime duration = LocalTime.parse("01:40");
			List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
			daysOfWeek.add(DayOfWeek.MONDAY);
			daysOfWeek.add(DayOfWeek.WEDNESDAY);
			
			Allocable lesson = new Lesson(group, begin, duration, daysOfWeek, new HashMap<Resource, Integer>());
			
			
			assertTrue(begin.equals(Lesson.getBeginTimeFromAllocable(lesson)));
			assertTrue(duration.equals(Lesson.getDurationTimeFromAllocable(lesson)));
			assertTrue(daysOfWeek.equals(Lesson.getDaysOfWeekFromAllocable(lesson)));
		} catch (Exception e) {
			fail("Failed due to unexpected error!" + e.getMessage());
		}
	}
	
	@Test
	public void testGetInfoFromAllocableClassroom() {
		try {
			String building = "43425"; 
			String room = "108";
			
			Allocable classroom = new Classroom(building, room, new HashMap<Resource, Integer>()); 
			
			assertTrue(building.equals(Classroom.getBuildingFromAllocable(classroom)));
			assertTrue(room.equals(Classroom.getRoomFromAllocable(classroom)));
			
		} catch (Exception e) {
			fail("Failed due to unexpected error!" + e.getMessage());
		}
	}

}
