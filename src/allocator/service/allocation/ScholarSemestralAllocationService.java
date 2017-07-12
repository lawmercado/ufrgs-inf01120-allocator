package allocator.service.allocation;

import java.util.*;

import allocator.algorithm.AllocationAlgorithm;
import allocator.data.domain.*;
import allocator.data.service.ScholarDataService;
import allocator.service.AllocationService;

import java.time.LocalDate;

public class ScholarSemestralAllocationService implements AllocationService 
{	
	private AllocationAlgorithm algorithm;
	private ScholarDataService sds;
	private LocalDate semesterBegin;
	
	public ScholarSemestralAllocationService(AllocationAlgorithm algorithm, ScholarDataService sds, LocalDate semesterBegin)
	{
		this.algorithm = algorithm;
		this.sds = sds;
		this.semesterBegin = semesterBegin;
	}

	public void execute()
	{	
		List<Discipline> disciplines = this.sds.getDisciplines();
		Iterator<Discipline> disciplineIter = disciplines.iterator();
		System.out.println("chamou");
		
		while(disciplineIter.hasNext())
		{
			System.out.println("1 while");
			Discipline discipline = disciplineIter.next();
			List<Group> groups = this.sds.getGroups(discipline.getId());
			Iterator<Group> groupIter = groups.iterator();
			System.out.println("-----WHILE DISCIPLINAS-----");
			System.out.println("DISCIPLINA: " + discipline);
			
			// Navegar pelas turmas (groups) 
			while(groupIter.hasNext())
			{
				Group actualGroup = groupIter.next();
				// Obter as aulas de cada turma (groups)
				List<Allocable> lessons = this.sds.getLessons(discipline.getId(), actualGroup.getId());
				Iterator<Allocable> lessonIter = lessons.iterator();
				System.out.println("-----WHILE GROUPS-----");
				System.out.println("GROUP: " + actualGroup);
				
				while(lessonIter.hasNext())
				{
				
					Allocable actualLesson = lessonIter.next();
					System.out.println("-----WHILE LESSONS-----");
					System.out.println("LESSON: " + actualLesson);
					try {
						System.out.println("SE A LESSON N�O TA RESERVADA -> ENTRA NO IF");
						if (!(this.sds.lessonHasReservation(discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6))));
						{
							System.out.println("ENTROU NO IF -> LESSON AINDA N FOI RESERVADA");
							List<Group> relatedGroups;
							try {
								int totalStudents = 0;
								System.out.println("PROCURA POR GRUPOS RELACIONADOS");
								relatedGroups = this.sds.getRelatedGroups(discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson));
								Iterator<Group> relatedGroupsIter = relatedGroups.iterator();
								// Tem turmas (groups) diferentes relacionadas a uma mesma aula?
								System.out.println("GRUPOS RELACIONADOS = " + relatedGroupsIter.hasNext());
								while(relatedGroupsIter.hasNext()) //relatedGroups != null 
								{
									System.out.println("TEM GRUPO RELACIONADO!");
									Group relatedGroupX = relatedGroupsIter.next(); // relatedGroupX = Grupo X relacionado, sendo X=1, para o primeiro grupo
																					// e segue at� X=n para o n-�simo grupo relacionado
									int numStudentsFromRelatedGroup = relatedGroupX.getNumStudents(); // Obt�m o n�mero de estudantes da n-�sima classe relacionada
									
									Map<Resource, Integer> lessonResources = actualLesson.getResources(); // Pega novos resources toda vez que entra no while
									int numStudentsFromGroup = lessonResources.get(ScholarResource.PLACES); // Pega o nro de estudantes do grupo original
									
									totalStudents = numStudentsFromGroup + numStudentsFromRelatedGroup; 
									lessonResources.put(ScholarResource.PLACES, totalStudents); // Insere na resource da lesson o n�mero total de estudantes dessa lesson
									
								}
								System.out.println("N�O TEM GRUPO RELACIONADO, PEGA AS SALAS LIVRES E FAZ ALOCA��O");
								// Aloca��o
								List<Allocable> availableClassrooms = null;
								
								availableClassrooms = this.sds.getAvailableClassrooms(Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson) , Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
								Allocable local = this.algorithm.run(actualLesson, availableClassrooms);
								System.out.println("O LOCAL ALOCADO � : " + local.toString());
								System.out.println("O LOCAL Q ERA PRA SER : " + availableClassrooms.toString());
								System.out.println("A LESSON � : " + actualLesson.toString());
																
								this.sds.insertReservation(Classroom.getBuildingFromAllocable(local), Classroom.getRoomFromAllocable(local), discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
								System.out.println("INSERIU A RESERVA");
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				
				}
				
			}
		}
		System.out.println("ACABOU OS WHILE");
	}
	
	
	

}
