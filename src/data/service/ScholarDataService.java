package data.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

import data.domain.*;

public interface ScholarDataService {

	public void insertDiscipline(String id, String name);
	
	public void insertGroup(String disciplineId, String id, String teacher, int numStudents);
	
	public void insertLesson(String disciplineId, String groupId, LocalTime begin, LocalTime duration, List<DayOfWeek> daysOfWeek,  Map<Resource, Integer> reqResources);
	
	public void insertClassroom(String building, String room, Map<Resource, Integer> availResources);
	
	public void insertReservation(String building, String room, String disciplineId, String groupId, LocalTime lessonBegin, LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to);
	
	public List<Discipline> getDisciplines();
	
	public List<Group> getGroups(String disciplineId);
	
	public List<Group> getRelatedGroups(String disciplineId, String groupId, LocalTime lessonBegin, List<DayOfWeek> lessonDaysOfWeek);
	
	public List<Allocable> getLessons(String disciplineId, String groupId);
	
	public List<Allocable> getAvailableClassrooms(LocalTime lessonBegin, LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to);
	
	public List<ScholarReservation> getReservations();
	
	public boolean classroomIsReserved(String building, String room, LocalTime lessonBegin, LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to);
	
	public boolean lessonHasReservation(String disciplineId, String groupId, LocalTime lessonBegin, LocalTime lessonDuration, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to);
}
