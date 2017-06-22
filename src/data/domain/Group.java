package data.domain;

import java.util.List;

public class Group {
	
	private Discipline discipline;
	private String id;
	private String teacher;
	private int numStudents;
	private List<Lesson> lessons;
	
	public Group(Discipline discipline, String id, String teacher, int numStudents)
	{
		this.discipline = discipline;
		this.id = id;
		this.teacher = teacher;
		this.numStudents = numStudents;
	}
	
	public void addLesson(Lesson lesson) {
		this.lessons.add(lesson);
	}
	
	public Discipline getDiscipline() {
		return this.discipline;
	}

	public String getId() {
		return this.id;
	}

	public String getTeacher() {
		return this.teacher;
	}

	public int getNumStudents() {
		return this.numStudents;
	}

	public List<Lesson> getLessons() {
		return this.lessons;
	}

}
