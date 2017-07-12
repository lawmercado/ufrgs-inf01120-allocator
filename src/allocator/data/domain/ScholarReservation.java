package allocator.data.domain;

import java.time.LocalDate;

public class ScholarReservation {
	private Classroom classroom;
	private Lesson lesson;
	private LocalDate from;
	private LocalDate to;

	public ScholarReservation(Classroom classroom, Lesson lesson, LocalDate from, LocalDate to) {
		this.classroom = classroom;
		this.lesson = lesson;
		this.from = from;
		this.to = to;
	}

	public Classroom getClassroom() {
		return this.classroom;
	}

	public Lesson getLesson() {
		return this.lesson;
	}
	
	public LocalDate getFrom() {
		return this.from;
	}

	public LocalDate getTo() {
		return this.to;
	}
}
