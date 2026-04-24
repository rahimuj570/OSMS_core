package algorithm;

import java.util.*;

import entity.Room;
import entity.Section;
import entity.Teacher;

public class CSPState {
	public Map<String, Variable> variables = new HashMap<>();

	public Map<Integer, Teacher> teachers;
	public Map<String, Room> rooms;
	public Map<String, Section> sections;

	public Map<Integer, long[]> teacherOccupied = new HashMap<>();
	public Map<String, long[]> roomOccupied = new HashMap<>();
	public Map<String, long[]> sectionOccupied = new HashMap<>();
	
	public Map<String, Integer> courseSectionTeacher = new HashMap<>();

	public CSPState(Map<Integer, Teacher> teachers, Map<String, Room> rooms, Map<String, Section> sections) {

		this.teachers = teachers;
		this.rooms = rooms;
		this.sections = sections;

		for (int t : teachers.keySet())
			teacherOccupied.put(t, new long[7]);

		for (String r : rooms.keySet())
			roomOccupied.put(r, new long[7]);

		for (String s : sections.keySet())
			sectionOccupied.put(s, new long[7]);
	}

	public void clearOccupations() {
		for (long[] arr : teacherOccupied.values())
			Arrays.fill(arr, 0L);
		for (long[] arr : roomOccupied.values())
			Arrays.fill(arr, 0L);
		for (long[] arr : sectionOccupied.values())
			Arrays.fill(arr, 0L);
	}

}
