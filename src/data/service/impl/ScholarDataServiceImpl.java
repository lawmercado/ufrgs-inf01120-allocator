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
		this.database.insert(new Discipline(id, name));

	}

	@Override
	public void insertGroup(String disciplineId, String id, String teacher, int numStudents) {
		this.database.insert(new Group(this.database.getDiscipline(disciplineId), id, teacher, numStudents));
	}

	@Override
	public void insertLesson(String disciplineId, String groupId, LocalTime begin, LocalTime duration,
			List<DayOfWeek> daysOfWeek, Map<Resource, Integer> reqResources) {
		
		
		Group group = this.database.getGroup(disciplineId, groupId);
		
		this.database.insert(new Lesson(group, begin, duration, daysOfWeek, reqResources));
		
	}

	@Override
	public void insertClassroom(String building, String room, Map<Resource, Integer> availResources) {
		this.database.insert(new Classroom(building, room, availResources));

	}

	@Override
	public void insertReservation(String building, String room, String disciplineId, String groupId,
			LocalTime lessonBegin, LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from,
			LocalDate to) {
		
		Classroom classroom = this.database.getClassroom(building, room);
		Lesson lesson = this.database.getLesson(disciplineId, groupId, lessonBegin);
		this.database.insert(new ScholarReservation(classroom, lesson, from, to));

	}

	@Override
	public List<Discipline> getDisciplines() {
		return this.database.listDisciplines();
	}

	@Override
	public List<Group> getGroups(String disciplineId) {
		return this.database.listGroups(disciplineId);
	}

	@Override
	public List<Group> getRelatedGroups(String disciplineId, String groupId, LocalTime lessonBegin,
			List<DayOfWeek> lessonDaysOfWeek) {
		Group referenceGroup = this.database.getGroup(disciplineId, groupId);
		String commonTeacher = referenceGroup.getTeacher();

		List<Group> groups = this.database.listGroups();

		List<Group> relatedGroups = new ArrayList<Group>();

		Iterator<Group> itrGroups = groups.iterator();

		while (itrGroups.hasNext()) {
			Group currGroup = itrGroups.next();

			if (!currGroup.equals(referenceGroup) && currGroup.getTeacher().equals(commonTeacher)) {
				List<Lesson> currGroupLessons = this.database.listLessons(currGroup.getDiscipline().getId(), currGroup.getId());
				Iterator<Lesson> itrLessons = currGroupLessons.iterator();

				while (itrLessons.hasNext()) {
					Lesson currLesson = itrLessons.next();
					if (currLesson.getBegin().equals(lessonBegin)
							&& currLesson.getDaysOfWeek().equals(lessonDaysOfWeek)) {
						relatedGroups.add(currGroup);
					}
				}
			}
		}

		return relatedGroups;
	}

	@Override
	public List<Allocable> getLessons(String disciplineId, String groupId) {
		List<Lesson> lessons = this.database.listLessons(disciplineId, groupId);
		List<Allocable> allocables = new ArrayList<Allocable>();

		Iterator<Lesson> itrLessons = lessons.iterator();

		while (itrLessons.hasNext()) {
			allocables.add(itrLessons.next());
		}

		return allocables;
	}

	@Override
	public List<Allocable> getAvailableClassrooms(LocalTime lessonBegin, LocalTime lessonDuration,
			List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to) {
		List<Classroom> classrooms = this.database.listClassrooms();
		List<Allocable> availClassrooms = new ArrayList<Allocable>();

		Iterator<Classroom> itrClassrooms = classrooms.iterator();

		while (itrClassrooms.hasNext()) {
			Classroom currPlace = itrClassrooms.next();

			if (!this.classroomIsReserved(currPlace.getBuilding(), currPlace.getRoom(), lessonBegin, lessonDuration,
					lessonDaysOfWeek, from, to)) {
				availClassrooms.add(currPlace);
			}
		}

		return availClassrooms;
	}

	@Override
	public boolean classroomIsReserved(String building, String room, LocalTime lessonBegin, LocalTime lessonDuration,
			List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to) {
		List<ScholarReservation> reservations = this.database.listReservations();

		Iterator<ScholarReservation> itrReservations = reservations.iterator();

		while (itrReservations.hasNext()) {
			ScholarReservation currentReserv = itrReservations.next();

			List<Boolean> isReservedTests = new ArrayList<Boolean>();
			isReservedTests.add(currentReserv.getClassroom().getBuilding().equals(building));
			isReservedTests.add(currentReserv.getClassroom().getRoom().equals(room));
			isReservedTests.add(currentReserv.getLesson().getBegin().equals(lessonBegin));
			isReservedTests.add(currentReserv.getLesson().getDuration().equals(lessonDuration));
			isReservedTests.add(currentReserv.getLesson().getDaysOfWeek().equals(lessonDaysOfWeek));
			isReservedTests.add(currentReserv.getFrom().isEqual(from));
			isReservedTests.add(currentReserv.getTo().isEqual(to));

			if (!isReservedTests.contains(false)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean lessonHasReservation(String disciplineId, String groupId, LocalTime lessonBegin,
			LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to) {

		List<ScholarReservation> reservations = this.database.listReservations();

		Iterator<ScholarReservation> itrReservations = reservations.iterator();

		while (itrReservations.hasNext()) {
			ScholarReservation currentReserv = itrReservations.next();

			List<Boolean> isReservedTests = new ArrayList<Boolean>();
			isReservedTests.add(currentReserv.getLesson().getGroup().getDiscipline().getId().equals(disciplineId));
			isReservedTests.add(currentReserv.getLesson().getGroup().getId().equals(groupId));
			isReservedTests.add(currentReserv.getLesson().getBegin().equals(lessonBegin));
			isReservedTests.add(currentReserv.getLesson().getDuration().equals(lessonDuration));
			isReservedTests.add(currentReserv.getLesson().getDaysOfWeek().equals(lessonDaysOfWeek));
			isReservedTests.add(currentReserv.getFrom().isEqual(from));
			isReservedTests.add(currentReserv.getTo().isEqual(to));

			if (!isReservedTests.contains(false)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<ScholarReservation> getReservations() {
		return this.database.listReservations();
	}

}
