package allocator.service.io;

import java.io.FileNotFoundException;

import allocator.service.FileIOService;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import allocator.data.domain.Resource;
import allocator.data.domain.ScholarReservation;
import allocator.data.domain.ScholarResource;
import allocator.service.ScholarDataService;

public class XMLIOService implements FileIOService {

	private ScholarDataService sds;
	
	public XMLIOService(ScholarDataService sds) {
		this.sds = sds;
	}
	
	@Override
	public void populateFromFile(String filePath) throws FileNotFoundException {
		try {
			File file = new File(filePath);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(file);

			NodeList courseList = doc.getElementsByTagName("course");

			for (int i = 0; i < courseList.getLength(); i++) {

				Node courseNode = courseList.item(i);

				if (courseNode.getNodeType() == Node.ELEMENT_NODE) {

					Element courseElement = (Element) courseNode;

					String courseID = courseElement.getAttribute("id");
					String courseName = courseElement.getAttribute("name");

					this.sds.insertDiscipline(courseID, courseName);

					NodeList groupList = courseElement.getElementsByTagName("group");

					for (int j = 0; j < groupList.getLength(); j++) {
						Node groupNode = groupList.item(j);
						if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
							Element groupElement = (Element) groupNode;
							NodeList sessionList = groupElement.getElementsByTagName("session");

							String stringNumStudents = groupElement.getAttribute("number_of_students");
							String teacher = groupElement.getAttribute("teacher");
							String groupID = groupElement.getAttribute("id");

							int numberStudents = Integer.parseInt(stringNumStudents);

							sds.insertGroup(courseID, groupID, teacher, numberStudents);

							for (int k = 0; k < sessionList.getLength(); k++) {
								Node sessionNode = sessionList.item(k);
								if (sessionNode.getNodeType() == Node.ELEMENT_NODE) {
									Element sessionElement = (Element) sessionNode;

									String stringWeekDay = sessionElement.getAttribute("weekday");
									String stringStartTime = sessionElement.getAttribute("start_time");
									String classDuration = sessionElement.getAttribute("duration");

									String stringFeatureIDs = sessionElement.getAttribute("feature_ids");

									StringTokenizer stTime = new StringTokenizer(stringStartTime, ":");
									
									int startHours = Integer.parseInt(stTime.nextToken());
									int startMinutes = Integer.parseInt(stTime.nextToken());
									int durationHours, durationMinutes;
									if (classDuration.isEmpty()) {
										durationHours = 0;
										durationMinutes = 0;
									} else {
										int durationInt = Integer.parseInt(classDuration);
										durationHours = durationInt / 60;
										durationMinutes = durationInt % 60;
									}
									LocalTime start_time = LocalTime.of(startHours, startMinutes);
									LocalTime duration = LocalTime.of(durationHours, durationMinutes);

									int day = Integer.parseInt(stringWeekDay.trim());

									// DayOfWeek.of(day);
									ArrayList<DayOfWeek> WeekDay = new ArrayList<DayOfWeek>();
									WeekDay.add(DayOfWeek.of(day));
									Map<Resource, Integer> reqResources = new HashMap<Resource, Integer>();
									if (stringFeatureIDs.isEmpty() || stringFeatureIDs.equals("")) {
										reqResources.put(ScholarResource.PLACES, numberStudents);
									} else {
										List<Integer> id_feat = new ArrayList<Integer>(10);
										List<Resource> featID = new ArrayList<Resource>(10);

										StringTokenizer stToken = new StringTokenizer(stringFeatureIDs, ",");
										while (stToken.hasMoreElements()) {
											int x = 0;
											id_feat.add(x, Integer.parseInt(stToken.nextElement().toString().trim()));

											featID.add(x, ScholarResource.fromValue(id_feat.get(x)));
											reqResources.put(featID.get(x), 1);
											
											x++;
										}
										reqResources.put(ScholarResource.PLACES, numberStudents);
									}

									sds.insertLesson(courseID, groupID, start_time, duration, WeekDay, reqResources);

								}
							}
						}

					}
				}
			}
			NodeList buildingsList = doc.getElementsByTagName("building");
			for (int i = 0; i < buildingsList.getLength(); i++) {

				Node nodeBuilding = buildingsList.item(i);

				if (nodeBuilding.getNodeType() == Node.ELEMENT_NODE) {

					Element buildingElement = (Element) nodeBuilding;

					String buildingID = buildingElement.getAttribute("id");

					NodeList roomList = buildingElement.getChildNodes();

					for (int j = 0; j < roomList.getLength(); j++) {
						Node roomNode = roomList.item(j);

						if (roomNode.getNodeType() == Node.ELEMENT_NODE) {
							Element roomElement = (Element) roomNode;

							String classroomID = roomElement.getAttribute("id");
							String stringFeatureIDs = roomElement.getAttribute("feature_ids");
							String numPlaces = roomElement.getAttribute("number_of_places");
							
							int numberPlaces = Integer.parseInt(numPlaces);
							Map<Resource, Integer> avaibleResources = new HashMap<Resource, Integer>();
							if (stringFeatureIDs.isEmpty()) {
								avaibleResources.put(ScholarResource.PLACES, numberPlaces);
							} else {
								int x = 0;
								List<Integer> id_feat = new ArrayList<Integer>();
								List<Resource> featID = new ArrayList<Resource>();

								StringTokenizer stToken = new StringTokenizer(stringFeatureIDs, ",");
								while (stToken.hasMoreElements()) {

									id_feat.add(x, Integer.parseInt(stToken.nextElement().toString().trim()));
									featID.add(x, ScholarResource.fromValue(id_feat.get(x)));
									avaibleResources.put(featID.get(x), 1);
									
									x++;
								}
								avaibleResources.put(ScholarResource.PLACES, numberPlaces);
							}

							sds.insertClassroom(buildingID, classroomID, avaibleResources);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void saveToFile(String filePath) throws FileNotFoundException {
		try {

			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("reservations");
			doc.appendChild(rootElement);

			List<ScholarReservation> reservations = this.sds.getReservations();
					        
			Iterator<ScholarReservation> itrReservations = reservations.iterator();
			
			while(itrReservations.hasNext()) {
				ScholarReservation reservation = itrReservations.next();
			
				Element elemReservation = doc.createElement("reservation");
				rootElement.appendChild(elemReservation);

				elemReservation.setAttribute("discipline", reservation.getLesson().getGroup().getDiscipline().getId());
				elemReservation.setAttribute("group", reservation.getLesson().getGroup().getId());
				elemReservation.setAttribute("start_time", reservation.getLesson().getBegin().toString());
				elemReservation.setAttribute("duration", reservation.getLesson().getDuration().toString());
				elemReservation.setAttribute("days_of_week", reservation.getLesson().getDaysOfWeek().toString());
				
				String place = reservation.getClassroom().getBuilding() + " - " + reservation.getClassroom().getRoom(); 
				elemReservation.setAttribute("place", place);
			}
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));

			transformer.transform(source, result);

		} catch (Exception pce) {
			pce.printStackTrace();
			
		}

	}
}
