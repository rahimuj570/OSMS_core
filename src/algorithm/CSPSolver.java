package algorithm;

import java.util.*;

import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;
import main.Main;

public class CSPSolver {

	private static int bestScore = Integer.MAX_VALUE;
	private static Map<String, Value> bestAssignment = new HashMap<>();

	private static int nodes = 0;
	private static final int MAX_NODES = 1_000_000;

	public static Map<String, Value> getBestAssignment() {
		return bestAssignment;
	}

	public static void reset() {
		Main.timeout = false;
		nodes = 0;
		bestScore = Integer.MAX_VALUE;
		bestAssignment.clear();
	}

	public static boolean solve(CSPState s) {

		if (++nodes > MAX_NODES) {
			Main.timeout = true;
			return true;
		}

		if (nodes % 200_000 == 0) {
			System.out.println("Visited nodes: " + nodes);
		}

		if (allAssigned(s)) {
			if (!validateLabOriented(s) || !validateLab(s))
				return false;
			

			int score = SoftConstraints.score(s);
			if (score < bestScore) {
				bestScore = score;
				bestAssignment.clear();
				for (Variable v : s.variables.values())
					bestAssignment.put(v.id, v.assignedValue);
			}
			return false;
		}
		
		

		Variable v = Heuristics.selectMRVDegree(s);

		if (v == null || v.domain.isEmpty())
			return false;
		for (Value val : new ArrayList<>(v.domain)) {

			if (!consistent(s, v, val))
				continue;

			assign(s, v, val);

			Map<String, List<Value>> removed = ForwardChecker.prune(s, v, val);

			if (removed != null) {

				boolean result = solve(s);

				ForwardChecker.restore(s, removed);

				if (result)
					return true;
			}

			unassign(s, v, val);
		}
		return false;
	}

	private static boolean allAssigned(CSPState s) {
		for (Variable v : s.variables.values())
			if (!v.assigned)
				return false;
		return true;
	}

	private static boolean consistent(CSPState s, Variable v, Value val) {
		Course c = v.course;
		Teacher t = s.teachers.get(val.teacherId);
		Room r = s.rooms.get(val.roomId);
		Section sec = s.sections.get(v.section.id);

		// HARD: forbidden teacher
		if (c.forbiddenTeachers != null && c.forbiddenTeachers.contains(val.teacherId))
			return false;
		
	
		if (!t.availability[val.day]) {
			return false;
		}

		// Room must handle student count
		if (sec.students > r.capacity)
			return false;

		if ((r.availability[val.day] & val.slotMask) != val.slotMask)
			return false;

		// Time/Room/Teacher clash check using bitmask
		if ((s.teacherOccupied.get(val.teacherId)[val.day] & val.slotMask) != 0)
			return false;
		if ((s.roomOccupied.get(val.roomId)[val.day] & val.slotMask) != 0)
			return false;
		if ((s.sectionOccupied.get(sec.id)[val.day] & val.slotMask) != 0)
			return false;

		if (v.course.type == CourseType.THEORY && r.type == RoomType.LAB)
			return false;
		if (v.course.type == CourseType.LAB && r.type != RoomType.LAB)
			return false;

		for (Variable other : s.variables.values()) {
			if (!other.assigned)
				continue;

//			if (other.course.id.equals(v.course.id) && other.section.id.equals(v.section.id)
//					&& other.assignedValue.day == val.day) {
//				return false;
//			}
			// allow same day, but not overlapping
			if (other.course.id.equals(v.course.id) && other.section.id.equals(v.section.id)
					&& other.assignedValue.day == val.day && (other.assignedValue.slotMask & val.slotMask) != 0) {
				return false;
			}
		}

		return true;
	}

	private static void assign(CSPState s, Variable v, Value val) {
		v.assigned = true;
		v.assignedValue = val;

		// Reserve teacher, room, section timeslot
		s.teacherOccupied.get(val.teacherId)[val.day] |= val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
		s.sectionOccupied.get(v.section.id)[val.day] |= val.slotMask;
	}

	private static void unassign(CSPState s, Variable v, Value val) {
		s.teacherOccupied.get(val.teacherId)[val.day] &= ~val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] &= ~val.slotMask;
		s.sectionOccupied.get(v.section.id)[val.day] &= ~val.slotMask;

		v.assigned = false;
		v.assignedValue = null;
	}

	private static boolean validateLabOriented(CSPState s) {
		Map<String, Boolean> usedLab = new HashMap<>();

		for (Variable v : s.variables.values()) {
			if (v.course.type == CourseType.LAB_ORIENTED_THEORY) {
				usedLab.putIfAbsent(v.course.id, false);
				Room r = s.rooms.get(v.assignedValue.roomId);
				if (r.type == RoomType.LAB)
					usedLab.put(v.course.id, true);
			}
		}
		return !usedLab.containsValue(false);
	}

	private static boolean validateLab(CSPState s) {
		Map<String, Boolean> usedLab = new HashMap<>();

		for (Variable v : s.variables.values()) {
			if (v.course.type == CourseType.LAB) {
				usedLab.putIfAbsent(v.course.id, true);
				Room r = s.rooms.get(v.assignedValue.roomId);
				if (r.type != RoomType.LAB)
					usedLab.put(v.course.id, false);
			}
		}
		return !usedLab.containsValue(false);
	}
}
