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
	
	
	
	
	public CSPState deepCopy() {

	    CSPState copy = new CSPState(
	            this.teachers,
	            new HashMap<>(this.rooms),
	            this.sections
	    );

	    // copy variables
	    for (Variable v : this.variables.values()) {
	        Variable nv = new Variable(v.id, v.course, v.section);

	        // deep copy domain
	        nv.domain = new ArrayList<>();
	        for (Value val : v.domain) {
	            nv.domain.add(new Value(
	                    val.day,
	                    val.startSlot,
	                    val.slotCount,
	                    val.roomId,
	                    val.teacherId
	            ));
	        }

	        copy.variables.put(nv.id, nv);
	    }

	    // copy occupied arrays
	    copy.teacherOccupied = cloneOccupied(this.teacherOccupied);
	    copy.roomOccupied = cloneOccupied(this.roomOccupied);
	    copy.sectionOccupied = cloneOccupied(this.sectionOccupied);

	    return copy;
	}

	private Map<String, long[]> cloneOccupied(Map<String, long[]> original) {
	    Map<String, long[]> copy = new HashMap<>();
	    for (Map.Entry<String, long[]> e : original.entrySet()) {
	        copy.put(e.getKey(), Arrays.copyOf(e.getValue(), 7));
	    }
	    return copy;
	}

}
