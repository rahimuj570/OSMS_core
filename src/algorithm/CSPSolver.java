package algorithm;

import java.util.*;

import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;

public class CSPSolver {

	public static boolean solve(CSPState s) {
		if (allAssigned(s))
			return validateLabOriented(s) && validateLab(s);

		Variable v = Heuristics.selectMRVDegree(s);

		for (Value val : new ArrayList<>(v.domain)) {
			if (consistent(s, v, val)) {
				assign(s, v, val);
				Map<String, List<Value>> removed = ForwardChecker.prune(s, v, val);

				if (removed != null && solve(s))
					return true;

				ForwardChecker.restore(s, removed);
				unassign(s, v, val);
			}
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
		Teacher t = s.teachers.get(c.teacherId);
		Room r = s.rooms.get(val.roomId);
		Section sec = s.sections.get(c.sectionId);

		if (!t.qualifiedCourseIds.contains(c.id))
			return false;
		if (sec.students > r.capacity)
			return false;

		if ((s.teacherOccupied.get(t.id)[val.day] & val.slotMask) != 0)
			return false;
		if ((s.roomOccupied.get(r.id)[val.day] & val.slotMask) != 0)
			return false;
		if ((s.sectionOccupied.get(sec.id)[val.day] & val.slotMask) != 0)
			return false;

		return true;
	}

	private static void assign(CSPState s, Variable v, Value val) {
		v.assigned = true;
		v.assignedValue = val;

		Course c = v.course;
		s.teacherOccupied.get(c.teacherId)[val.day] |= val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
		s.sectionOccupied.get(c.sectionId)[val.day] |= val.slotMask;
	}

	private static void unassign(CSPState s, Variable v, Value val) {
		Course c = v.course;
		s.teacherOccupied.get(c.teacherId)[val.day] ^= val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] ^= val.slotMask;
		s.sectionOccupied.get(c.sectionId)[val.day] ^= val.slotMask;

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
