package data.domain;

import java.time.LocalDate;
import java.time.LocalTime;

public class ScholarReservation {
	private String groupId;
	private String building;
	private String room;
	private LocalTime lessonBegin;
	private LocalTime lessonDuration;
	private LocalDate from;
	private LocalDate to;

	public ScholarReservation(String building, String room, String groupId, LocalTime lessonBegin, LocalTime lessonDurationn, LocalDate from, LocalDate to) {
		this.groupId = groupId;
		this.building = building;
		this.room = room;
		this.lessonBegin = lessonBegin;
		this.lessonDuration = lessonDurationn;
		this.from = from;
		this.to = to;
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

	public LocalDate getFrom() {
		return this.from;
	}

	public LocalDate getTo() {
		return this.to;
	}
}
