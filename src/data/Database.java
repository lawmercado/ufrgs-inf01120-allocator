package data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.domain.*;

public class Database {

	private List<Discipline> disciplines;
	private List<Group> groups;
	private List<Lesson> lessons;
	private List<Classroom> classrooms;
	private List<ScholarReservation> reservations;

	public Database() {
		this.disciplines = new ArrayList<Discipline>();
		this.groups = new ArrayList<Group>();
		this.classrooms = new ArrayList<Classroom>();
		this.reservations = new ArrayList<ScholarReservation>();
		this.lessons = new ArrayList<Lesson>();
	}

	public void insert(Discipline discipline) {
		this.disciplines.add(discipline);
	}

	public void insert(Group group) {
		this.groups.add(group);
	}

	public void insert(Lesson lesson) {
		this.lessons.add(lesson);
	}

	public void insert(Classroom classroom) {
		this.classrooms.add(classroom);
	}

	public void insert(ScholarReservation reservation) {
		this.reservations.add(reservation);
	}
	
	public Discipline getDiscipline(String disciplineId) {
		for (Discipline discipline : this.disciplines) {
			if (discipline.getId().equals(disciplineId)) {
				return discipline;
			}
		}

		return null;
	}
	
	public List<Discipline> listDisciplines() {
		return this.disciplines;
	}

	public Group getGroup(String disciplineId, String groupId) {
		Iterator<Group> itrGroup = this.groups.iterator();
		
		while(itrGroup.hasNext()) {
			Group group = itrGroup.next();
			
			if (group.getDiscipline().getId().equals(disciplineId) && group.getId().equals(groupId)) {
				return group;
			}
		}

		return null;
	}
	
	public List<Group> listGroups() {
		return this.groups;
	}
	
	public List<Group> listGroups(String disciplineId) {
		List<Group> groups = new ArrayList<Group>();
		Iterator<Group> itrGroups = this.groups.iterator();
		
		while(itrGroups.hasNext()) {
			Group group = itrGroups.next();
			
			if (group.getDiscipline().getId().equals(disciplineId)) {
				groups.add(group);
			}
		}

		return groups;
	}
	
	public List<Lesson> listLessons(String disciplineId, String groupId) {
		List<Lesson> lessons = new ArrayList<Lesson>();
		Iterator<Lesson> itrLessons = this.lessons.iterator();
		
		while(itrLessons.hasNext()) {
			Lesson lesson = itrLessons.next();
			Group group = lesson.getGroup();
			
			if (group.getDiscipline().getId().equals(disciplineId) && group.getId().equals(groupId)) {
				lessons.add(lesson);
			}
		}

		return lessons;
	}

	public List<Classroom> listClassrooms() {
		return this.classrooms;
	}

	public List<ScholarReservation> listReservations(LocalDate from, LocalDate to) {
		return this.reservations;
	}

}
