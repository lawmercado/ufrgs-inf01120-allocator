package allocator.service.allocation;

import allocator.service.AllocationService;
import allocator.data.domain.*;
import java.util.*;

import allocator.algorithm.AllocationAlgorithm;

import java.time.LocalDate;
import allocator.service.ScholarDataService;

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
		//System.out.println("EXECUTE");
		
		while(disciplineIter.hasNext())
		{
			//System.out.println("WHILE");
			Discipline discipline = disciplineIter.next();
			List<Group> groups = this.sds.getGroups(discipline.getId());
			Iterator<Group> groupIter = groups.iterator();
			
			//System.out.println("-----WHILE DISCIPLINAS-----");
			//System.out.println("DISCIPLINA: " + discipline.getName());
			
			// Navegar pelas turmas (groups) 
			while(groupIter.hasNext())
			{
				Group actualGroup = groupIter.next();
				// Obter as aulas de cada turma (groups)
				List<Allocable> lessons = this.sds.getLessons(discipline.getId(), actualGroup.getId());
				Iterator<Allocable> lessonIter = lessons.iterator();
				
				//System.out.println("-----WHILE GROUPS-----");
				//System.out.println("GROUP: " + actualGroup.getId());
				
				while(lessonIter.hasNext())
				{
				
					Allocable actualLesson = lessonIter.next();
					Allocable temporaryLesson = actualLesson;		// Caso seja necessário aumentar o número de lugares por haver grupos relacionados a uma mesma aula, aumenta-se da lesson temporaria que será usada apenas para alocação
					
					//System.out.println("-----WHILE LESSONS-----");
					//System.out.println("LESSON: " + actualLesson);
					
					try {
						boolean hasReservation = this.sds.lessonHasReservation(discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
						//System.out.println("SE A LESSON NÃO TA RESERVADA -> ENTRA NO IF -> RESERVADO = " + hasReservation);
						if (!(hasReservation))
						{
							//System.out.println("ENTROU NO IF -> LESSON AINDA N FOI RESERVADA");
							//System.out.println("totalStudents ANTIGO : " + actualGroup.getNumStudents());
							//System.out.println("PROCURA POR GRUPOS RELACIONADOS");
							
							List<Group> relatedGroups = this.sds.getRelatedGroups(discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson));
							Iterator<Group> relatedGroupsIter = relatedGroups.iterator();
							
							// Tem turmas (groups) diferentes relacionadas a uma mesma aula?
							//System.out.println("GRUPOS RELACIONADOS = " + relatedGroupsIter.hasNext());
							
							if(relatedGroupsIter.hasNext() == true)
							{
								//System.out.println("TEM " + relatedGroups.size() + "GRUPO(S) RELACIONADO(S)!");
								int totalStudents = actualGroup.getNumStudents();
							
								while(relatedGroupsIter.hasNext())
								{
									Group relatedGroupX = relatedGroupsIter.next(); // relatedGroupX = Grupo X relacionado, sendo X=1, para o primeiro grupo
																					// e segue até X=n para o n-ésimo grupo relacionado
									int numStudentsFromRelatedGroup = relatedGroupX.getNumStudents(); // Obtém o número de estudantes do n-ésima grupo relacionado
										
									totalStudents = totalStudents + numStudentsFromRelatedGroup; 	
								}
								
								//System.out.println("totalStudents NOVO : " + totalStudents);
								
								Map<Resource, Integer> temporaryLessonResources = temporaryLesson.getResources();
								temporaryLessonResources.put(ScholarResource.PLACES, totalStudents);
								
							}	
							//System.out.println("NÃO TEM GRUPO RELACIONADO, PEGA AS SALAS LIVRES E FAZ ALOCAÇÃO");
							
							// Alocação
							List<Allocable> availableClassrooms =  this.sds.getAvailableClassrooms(Lesson.getBeginTimeFromAllocable(temporaryLesson), Lesson.getDurationTimeFromAllocable(temporaryLesson) , Lesson.getDaysOfWeekFromAllocable(temporaryLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
								
							Allocable local = this.algorithm.run(temporaryLesson, availableClassrooms);
							
							//System.out.println("O LOCAL ALOCADO É : " + local.toString());
							//System.out.println("O LOCAL Q ERA PRA SER : " + availableClassrooms.toString());
							//System.out.println("A LESSON É : " + temporaryLesson.toString());
							//System.out.println("SEMESTER BEGIN : " + this.semesterBegin);
							//System.out.println("SEMESTER END : " + this.semesterBegin.plusMonths(6));	
							//System.out.println("CLASSROOMS DISPONIVEIS ANTES DA RESERVA : " + availableClassrooms.size());
							
							if(local != null)
							{
								this.sds.insertReservation(Classroom.getBuildingFromAllocable(local), Classroom.getRoomFromAllocable(local), discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
								
								relatedGroups = this.sds.getRelatedGroups(discipline.getId(), actualGroup.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson));
								relatedGroupsIter = relatedGroups.iterator();
								
								while(relatedGroupsIter.hasNext())
								{
									Group relatedGroupX = relatedGroupsIter.next();
									
									this.sds.insertReservation(Classroom.getBuildingFromAllocable(local), Classroom.getRoomFromAllocable(local), relatedGroupX.getDiscipline().getId(), relatedGroupX.getId(), Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson), Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
								}
							}
							//System.out.println("INSERIU A RESERVA GERAL, TEORICAMENTE");
							
							availableClassrooms = this.sds.getAvailableClassrooms(Lesson.getBeginTimeFromAllocable(actualLesson), Lesson.getDurationTimeFromAllocable(actualLesson) , Lesson.getDaysOfWeekFromAllocable(actualLesson), this.semesterBegin, this.semesterBegin.plusMonths(6));
							//System.out.println("CLASSROOMS DISPONIVEIS DEPOIS DA RESERVA : " + availableClassrooms.size());
								
							//List<ScholarReservation> reservations = this.sds.getReservations(this.semesterBegin, this.semesterBegin.plusMonths(6));
							//System.out.println("NRO DE RESERVAS NA LISTA DE RESERVAS : " + reservations.size());
								
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				
				}
				
			}
		}
		//System.out.println("ACABOU OS WHILE");
	}
	
	
	

}
