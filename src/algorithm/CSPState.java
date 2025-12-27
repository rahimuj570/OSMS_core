package algorithm;

import java.util.*;

import entity.Room;
import entity.Section;
import entity.Teacher;

public class CSPState {
	public Map<String, Variable> variables = new HashMap<>();

	public Map<String, Teacher> teachers;
	public Map<String, Room> rooms;
	public Map<String, Section> sections;

	public Map<String, long[]> teacherOccupied = new HashMap<>();
	public Map<String, long[]> roomOccupied = new HashMap<>();
	public Map<String, long[]> sectionOccupied = new HashMap<>();

	public CSPState(Map<String, Teacher> teachers, Map<String, Room> rooms, Map<String, Section> sections) {

		this.teachers = teachers;
		this.rooms = rooms;
		this.sections = sections;

		for (String t : teachers.keySet())
			teacherOccupied.put(t, new long[5]);

		for (String r : rooms.keySet())
			roomOccupied.put(r, new long[5]);

		for (String s : sections.keySet())
			sectionOccupied.put(s, new long[5]);
	}
}
