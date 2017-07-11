package algorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import data.domain.Allocable;
import data.domain.Resource;
import data.domain.ScholarResource;

public class OurAllocateAlgorithm implements AllocationAlgorithm {
	
	public Allocable run(Allocable what, List<Allocable> where) 
	{
		Allocable lesson = what; // Apenas para ficar mais claro o código
		Allocable definedPlace = null;
		int numOfEqualResources = 0;
		int difference = -1;
		int actualDifference = 0;
		Iterator<Allocable> availClassroomsIter = where.iterator();
		
		Map<Resource, Integer> lessonResources = lesson.getResources();
		int numStudents = lessonResources.get(ScholarResource.PLACES);
        //System.out.println("Numero lugares que a lesson quer = " + numStudents);


		
		
		while(availClassroomsIter.hasNext())
		{	
			Allocable actualClassroom = availClassroomsIter.next();
			Map<Resource, Integer> roomResources = actualClassroom.getResources();
			int numPlacesClassroom = roomResources.get(ScholarResource.PLACES);
	        //System.out.println("Numero lugares da sala " + actualClassroom + "=" + numPlacesClassroom);
	        numOfEqualResources = 0;
			
			Iterator lessonResourcesIter = lessonResources.entrySet().iterator();
			
			// Caso mais que perfeito, onde nro estudantes = nro de lugares e os requisitos fecham perfeitamente
			if(numPlacesClassroom == numStudents && lessonResources.equals(roomResources)){
				 definedPlace = actualClassroom;
				 difference = 0;
				 actualDifference = 0;
				 break;
			}
			else if(numPlacesClassroom >= numStudents) 
			{	

				  while(lessonResourcesIter.hasNext())
				  {
					  Map.Entry pair = (Map.Entry)lessonResourcesIter.next();
					  Object lessonResourceId = pair.getKey();
					  //System.out.println("LESSON RESOURCE = " + lessonResourceId);
					  Iterator roomResourcesIter = roomResources.entrySet().iterator();
					  while(roomResourcesIter.hasNext())
					  {
						  Map.Entry pair2 = (Map.Entry)roomResourcesIter.next();
						  Object roomResourceId = pair2.getKey();
						  //System.out.println("CLASS RESOURCE = " + roomResourceId);
						  if(lessonResourceId == roomResourceId)
						  {
							  numOfEqualResources = numOfEqualResources + 1;
							  //System.out.println("MATCH EM " + numOfEqualResources + "RESOURCES");
						  }
					  }
				  }
				  //System.out.println("NRO DE MATCH É =" + numOfEqualResources + " NRO RESOURCES LESSON = " + lessonResources.size());
				  if(numOfEqualResources == lessonResources.size())
				  {
					  actualDifference = roomResources.size() - numOfEqualResources + numPlacesClassroom - numStudents;
					  //System.out.println("ACTUALDIF = " + actualDifference + " DIFFERENCE = " + difference);
					  if(definedPlace == null)
					  {
						  //System.out.println("ERA NULL ALOCA PRIMEIRA SALA");
						  definedPlace = actualClassroom;
						  difference = actualDifference;
						  //System.out.println("DIFFERENCE AGORA É = " + difference);
					  }
					  else if(actualDifference < difference)
					  {
						  //System.out.println("NAO ERA MAIS NULL");
						  definedPlace = actualClassroom;
						  difference = actualDifference;
					  }
				  }
			}
		} // while dos availClassrooms acaba aqui
        //System.out.println(definedPlace.toString());
		return definedPlace;
	}

}
