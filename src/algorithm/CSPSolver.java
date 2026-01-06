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
		// If all classes assigned -> check final constraints
		if (allAssigned(s))
			return validateLabOriented(s) && validateLab(s);

		// Select next variable to assign by MRV + Degree Heuristic
		Variable v = Heuristics.selectMRVDegree(s);

		for (Value val : new ArrayList<>(v.domain)) {
			if (consistent(s, v, val)) {

				assign(s, v, val);

				Map<String, List<Value>> removed = ForwardChecker.prune(s, v, val);

				// recursion happens here
				if (removed != null && solve(s))
					return true;

				// rollback changes
				ForwardChecker.restore(s, removed);
				unassign(s, v, val);
			}
		}
		return false; // backtrack
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

		// Teacher must be qualified
		if (!c.teacherIds.contains(val.teacherId))
			return false;
		if (!t.qualifiedCourseIds.contains(c.id))
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
		// Free previously reserved time
		s.teacherOccupied.get(val.teacherId)[val.day] ^= val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] ^= val.slotMask;
		s.sectionOccupied.get(v.section.id)[val.day] ^= val.slotMask;

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
