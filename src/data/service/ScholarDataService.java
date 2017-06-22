package data.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;

import data.domain.*;

public interface ScholarDataService {

	public void insertDiscipline(String id, String name);
	
	public void insertGroup(String disciplineId, String id, String teacher, int numStudents);
	
	public void insertLesson(String disciplineId, String groupId, LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek, List<Resource> reqResources) throws Exception;
	
	public void insertClassroom(String building, String room, List<Resource> availResources);
	
	public void insertReservation(String building, String room, String groupId, LocalTime lessonBegin, LocalTime lessonDuration, LocalDate from, LocalDate to);
	
	public List<Discipline> getDisciplines();
	
	public List<Group> getGroups(String disciplineId);
	
	public List<Group> getRelatedGroups(String disciplineId, String groupId);
	
	public List<Allocable> getLessons(String disciplineId, String groupId);
	
	public List<Allocable> getAvailableClassrooms(LocalTime begin, LocalDate from, LocalDate to);
	
	public boolean isReserved(String building, String room, LocalTime begin, LocalDate from, LocalDate to);
}
