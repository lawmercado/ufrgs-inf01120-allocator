package allocator.service.allocation;

import allocator.service.AllocationService;
import allocator.data.domain.*;
import java.util.*;

import allocator.algorithm.AllocationAlgorithm;

import java.time.LocalDate;
import allocator.service.ScholarDataService;

public class ScholarSemestralAllocationService implements AllocationService {
	private AllocationAlgorithm algorithm;
	private ScholarDataService sds;
	private LocalDate semesterBegin;

	public ScholarSemestralAllocationService(AllocationAlgorithm algorithm, ScholarDataService sds,
			LocalDate semesterBegin) {
		this.algorithm = algorithm;
		this.sds = sds;
		this.semesterBegin = semesterBegin;
	}

	public void execute() {
		List<Discipline> disciplines = this.sds.getDisciplines();
		Iterator<Discipline> disciplineIter = disciplines.iterator();

		while (disciplineIter.hasNext()) {
			Discipline discipline = disciplineIter.next();
			List<Group> groups = this.sds.getGroups(discipline.getId());
			Iterator<Group> groupIter = groups.iterator();

			// Navegar pelas turmas (groups)
			while (groupIter.hasNext()) {
				Group actualGroup = groupIter.next();
				// Obter as aulas de cada turma (groups)
				List<Allocable> lessons = this.sds.getLessons(discipline.getId(), actualGroup.getId());
				Iterator<Allocable> lessonIter = lessons.iterator();

				while (lessonIter.hasNext()) {

					Allocable actualLesson = lessonIter.next();
					Allocable temporaryLesson = actualLesson; // Caso seja
																// necessário
																// aumentar o
																// número de
																// lugares por
																// haver grupos
																// relacionados
																// a uma mesma
																// aula,
																// aumenta-se da
																// lesson
																// temporaria
																// que será
																// usada apenas
																// para alocação

					try {
						boolean hasReservation = this.sds.lessonHasReservation(discipline.getId(), actualGroup.getId(),
								Lesson.getBeginTimeFromAllocable(actualLesson),
								Lesson.getDurationTimeFromAllocable(actualLesson),
								Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin,
								this.semesterBegin.plusMonths(6));
						if (!(hasReservation)) {
							List<Group> relatedGroups = this.sds.getRelatedGroups(discipline.getId(),
									actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson),
									Lesson.getDaysOfWeekFromAllocable(actualLesson));
							Iterator<Group> relatedGroupsIter = relatedGroups.iterator();

							// Tem turmas (groups) diferentes relacionadas a uma
							// mesma aula?
							if (relatedGroupsIter.hasNext() == true) {
								updateTotalStudentsFromLesson(relatedGroups, actualGroup, temporaryLesson);
							}
							// Alocação

							List<Allocable> availableClassrooms = this.sds.getAvailableClassrooms(
									Lesson.getBeginTimeFromAllocable(temporaryLesson),
									Lesson.getDurationTimeFromAllocable(temporaryLesson),
									Lesson.getDaysOfWeekFromAllocable(temporaryLesson), this.semesterBegin,
									this.semesterBegin.plusMonths(6));
							Allocable local = this.algorithm.run(temporaryLesson, availableClassrooms);

							actualLesson.getResources().put(ScholarResource.PLACES, actualGroup.getNumStudents());
							
							if (local != null) {
								insertAllReservations(local, discipline, actualGroup, actualLesson, semesterBegin);
							}
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}

				}

			}
		}
	}

	private void updateTotalStudentsFromLesson(List<Group> groups, Group actualGroup, Allocable temporaryLesson) {
		int numTotalStudents = actualGroup.getNumStudents();
		List<Group> relatedGroups = groups;
		Iterator<Group> relatedGroupsIter = relatedGroups.iterator();
		while (relatedGroupsIter.hasNext()) {
			Group relatedGroupX = relatedGroupsIter.next(); // relatedGroupX =
															// Grupo X
															// relacionado,
															// sendo X=1, para o
															// primeiro grupo
															// e segue até X=n
															// para o n-ésimo
															// grupo relacionado
			int numStudentsFromRelatedGroup = relatedGroupX.getNumStudents(); // Obtém
																				// o
																				// número
																				// de
																				// estudantes
																				// do
																				// n-ésima
																				// grupo
																				// relacionado

			numTotalStudents = numTotalStudents + numStudentsFromRelatedGroup;
		}
		Map<Resource, Integer> temporaryLessonResources = temporaryLesson.getResources();
		temporaryLessonResources.put(ScholarResource.PLACES, numTotalStudents);
	}

	private void insertAllReservations(Allocable local, Discipline discipline, Group actualGroup,
			Allocable actualLesson, LocalDate semesterBegin) {
		this.sds.insertReservation(Classroom.getBuildingFromAllocable(local), Classroom.getRoomFromAllocable(local),
				discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson),
				Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson),
				this.semesterBegin, this.semesterBegin.plusMonths(6));

		List<Group> relatedGroups = this.sds.getRelatedGroups(discipline.getId(), actualGroup.getId(),
				Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson));
		Iterator<Group> relatedGroupsIter = relatedGroups.iterator();

		while (relatedGroupsIter.hasNext()) // Realiza a reserva dos grupos
											// relacionados também
		{
			Group relatedGroupX = relatedGroupsIter.next();
			this.sds.insertReservation(Classroom.getBuildingFromAllocable(local), Classroom.getRoomFromAllocable(local),
					relatedGroupX.getDiscipline().getId(), relatedGroupX.getId(),
					Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson),
					Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin,
					this.semesterBegin.plusMonths(6));
		}
	}
}
