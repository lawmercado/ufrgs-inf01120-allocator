package data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import data.domain.*;

public class Database {

	private List<Discipline> disciplines;
	private List<Group> groups;
	private List<Classroom> classrooms;
	private List<ScholarReservation> reservations;
	private List<Resource> resources;

	public Database() {
		this.disciplines = new ArrayList<Discipline>();
		this.groups = new ArrayList<Group>();
		this.classrooms = new ArrayList<Classroom>();
		this.reservations = new ArrayList<ScholarReservation>();
		this.resources = new ArrayList<Resource>();
	}

	public void addDiscipline(Discipline discipline) {
		this.disciplines.add(discipline);
	}

	public Discipline getDiscipline(String disciplineId) {
		for (Discipline discipline : this.disciplines) {
			if (discipline.getId().equals(disciplineId)) {
				return discipline;
			}
		}

		return null;
	}

	public void addGroup(Group group) {
		this.groups.add(group);
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

	public void save(Group group) throws Exception {
		for (int i = 0; i < this.groups.size(); i++) {
			Group currentGroup = this.groups.get(i);
			
			if (currentGroup.getId().equals(group.getId()) && currentGroup.getDiscipline().getId().equals(group.getDiscipline().getId())) {
				this.groups.set(i, group);
				
				return;
			}
		}
		
		throw new Exception("There is no group with the spedified key to save!");
	}

	public void addClassroom(Classroom classroom) {
		this.classrooms.add(classroom);
	}

	public void addScholarReservation(ScholarReservation reservation) {
		this.reservations.add(reservation);
	}

	public void addResource(Resource resource) {
		this.resources.add(resource);
	}
	
	public List<Discipline> getDisciplines() {
		return this.disciplines;
	}

	public List<Group> getGroups() {
		return this.groups;
	}

	public List<Classroom> getClassrooms() {
		return this.classrooms;
	}

	public Resource getResource(int id) {
		Iterator<Resource> itrResources = this.resources.iterator();
		
		while(itrResources.hasNext()) {
			Resource resource = itrResources.next();
			
			if(resource.getId() == id) {
				return resource;
			}
		}
		
		return null;
	}
	
	public Resource getResource(String description) {
		Iterator<Resource> itrResources = this.resources.iterator();
		
		while(itrResources.hasNext()) {
			Resource resource = itrResources.next();
			
			if(resource.getDescription().equals(description)) {
				return resource;
			}
		}
		
		return null;
	}
	
	public List<ScholarReservation> getReservations(LocalDate from, LocalDate to) {
		return this.reservations;
	}

}
