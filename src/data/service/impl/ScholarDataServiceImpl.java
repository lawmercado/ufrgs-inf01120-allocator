package data.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import data.Database;
import data.domain.*;
import data.service.*;

public class ScholarDataServiceImpl implements ScholarDataService {

	private Database database;

	public ScholarDataServiceImpl() {
		this.database = new Database();

	}

	@Override
	public void insertDiscipline(String id, String name) {
		this.database.addDiscipline(new Discipline(id, name));

	}

	@Override
	public void insertGroup(String disciplineId, String id, String teacher, int numStudents) {
		this.database.addGroup(new Group(this.database.getDiscipline(disciplineId), id, teacher, numStudents));
	}

	@Override
	public void insertLesson(String disciplineId, String groupId, LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek,  Map<Resource, Integer> reqResources) throws Exception {
		Group group = this.database.getGroup(disciplineId, groupId);
		group.addLesson(new Lesson(begin, duration, daysOfWeek, reqResources));

		this.database.save(group);
	}

	@Override
	public void insertClassroom(String building, String room, Map<Resource, Integer> availResources) {
		this.database.addClassroom(new Classroom(building, room, availResources));

	}

	@Override
	public void insertReservation(String building, String room, String disciplineId, String groupId, LocalTime lessonBegin, LocalTime lessonDuration, LocalDate from, LocalDate to) {
		this.database.addScholarReservation(
				new ScholarReservation(building, room, groupId, lessonBegin, lessonDuration, from, to));

	}

	@Override
	public List<Group> getRelatedGroups(String disciplineId, String groupId) {
		Group group = this.database.getGroup(disciplineId, groupId);
		List<Lesson> lessons = group.getLessons();
		String commonTeacher = group.getTeacher();
		
		List<Group> groups = this.database.getGroups();
		
		List<Group> relatedGroups = new ArrayList<Group>();
		
		for(int i = 0; i < groups.size(); i++) {
			Group currentGroup = groups.get(i); 
			
			if(currentGroup.getTeacher().equals(commonTeacher) && currentGroup.getLessons().equals(lessons)) {
				relatedGroups.add(currentGroup);
			}
		}
		
		return relatedGroups;
	}

	@Override
	public List<Allocable> getLessons(String disciplineId, String groupId) {
		Group group = this.database.getGroup(disciplineId, groupId);
		List<Lesson> lessons = group.getLessons();
		List<Allocable> allocables = new ArrayList<Allocable>();
		
		for( int i = 0; i < lessons.size(); i++ ) {
			allocables.add(lessons.get(i));
		}
		
		return allocables;
	}

	@Override
	public List<Allocable> getAvailableClassrooms(LocalTime begin, LocalDate from, LocalDate to) {
		List<Classroom> classrooms = this.database.getClassrooms();
		List<Allocable> availClassrooms = new ArrayList<Allocable>();
		
		for( int i = 0; i < classrooms.size(); i++ ) {
			if(!this.isReserved(classrooms.get(i).getBuilding(), classrooms.get(i).getRoom(), begin, from, to)) {
				availClassrooms.add(classrooms.get(i));
			}
		}
		
		return availClassrooms;
	}

	@Override
	public boolean isReserved(String building, String room, LocalTime begin, LocalDate from, LocalDate to) {
		List<ScholarReservation> reservations = this.database.getReservations(from, to);
		
		for(int i = 0; i < reservations.size(); i++) {
			ScholarReservation currentReserv = reservations.get(i);
			
			List<Boolean> isReservedTests = new ArrayList<Boolean>();
			isReservedTests.add(currentReserv.getBuilding().equals(building));
			isReservedTests.add(currentReserv.getRoom().equals(room));
			isReservedTests.add(currentReserv.getLessonBegin().equals(begin));
			isReservedTests.add(currentReserv.getFrom().isEqual(from));
			isReservedTests.add(currentReserv.getTo().isEqual(to));
			
			if(!isReservedTests.contains(false)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public List<Discipline> getDisciplines() {
		return this.database.getDisciplines();
	}

	@Override
	public List<Group> getGroups(String disciplineId) {
		List<Group> disciplineGroups = new ArrayList<Group>();
		List<Group> groups = this.database.getGroups();
		
		Iterator<Group> itrGroups = groups.iterator();
		
		while(itrGroups.hasNext()) {
			Group currGroup = itrGroups.next();
			
			if(currGroup.getDiscipline().getId().equals(disciplineId)) {
				disciplineGroups.add(currGroup);
			}

		}
		
		return disciplineGroups;
	}

}
