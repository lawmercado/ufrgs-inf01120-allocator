package data.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.DayOfWeek;

public class ScholarReservation {
	private String disciplineId;
	private String groupId;
	private String building;
	private String room;
	private LocalTime lessonBegin;
	private LocalTime lessonDuration;
	private List<DayOfWeek> lessonDaysOfWeek;
	private LocalDate from;
	private LocalDate to;

	public ScholarReservation(String building, String room, String disciplineId, String groupId, LocalTime lessonBegin, LocalTime lessonDurationn, List<DayOfWeek> lessonDaysOfWeek, LocalDate from, LocalDate to) {
		this.disciplineId = disciplineId;
		this.groupId = groupId;
		this.building = building;
		this.room = room;
		this.lessonBegin = lessonBegin;
		this.lessonDuration = lessonDurationn;
		this.from = from;
		this.to = to;
		this.lessonDaysOfWeek = lessonDaysOfWeek;
	}

	public String getDisciplineId() {
		return this.disciplineId;
	}
	
	public String getGroupId() {
		return this.groupId;
	}

	public String getBuilding() {
		return this.building;
	}

	public String getRoom() {
		return this.room;
	}

	public LocalTime getLessonBegin() {
		return this.lessonBegin;
	}
	
	public LocalTime getLessonDuration() {
		return this.lessonDuration;
	}
	
	public List<DayOfWeek> getLessonDaysOfWeek() {
		return this.lessonDaysOfWeek;
	}

	public LocalDate getFrom() {
		return this.from;
	}

	public LocalDate getTo() {
		return this.to;
	}
}
