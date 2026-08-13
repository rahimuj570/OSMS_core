package algorithm;

import java.util.*;

import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;

public class CSPSolver {

	public static int routineGenerationPercentage = 0;
	public static boolean isSolverRunning = false;
	public static String efficiency = "";
	public static String outsidePreferred = "";

	private static long divisor = 100000;
	private static boolean shouldTakeFirstSolution = false;
	private static int bestScore = Integer.MAX_VALUE;
	private static Map<String, Value> bestAssignment = new HashMap<>();
	private static long solveTime = 0;
	public static int MAX_NODES = 1_000_000;
	private static boolean wasTimedOut = false;

	private static int nodes = 0;

	public static Map<String, Value> getBestAssignment() {
		return bestAssignment;
	}

	private static Set<String> bestSkipped = new HashSet<>();

	public static Set<String> getBestSkipped() {
		return bestSkipped;
	}

	public static int getVisitedNodes() {
		return nodes;
	}

	public static long getSolveTime() {
		return solveTime;
	}
	public static double getStatesPerSecond() {

	    if (solveTime == 0)
	        return 0;

	    return nodes / (solveTime / 1000.0);
	}

	public static boolean wasTimedOut() {
		return wasTimedOut;
	}

	/////////////// FOR PARTIAL TBA RESULT
	///
	///
	///

	private static int bestPartialAssignedCount = 0;

	private static int currentAssignedCount(CSPState s) {

		int count = 0;

		for (Variable v : s.variables.values()) {
			if (v.assigned)
				count++;
		}

		return count;
	}
	//////////////////////////

	public static void reset() {
		bestAssignment.clear();
		bestSkipped.clear();

		bestPartialAssignedCount = -1;
		shouldTakeFirstSolution = false;
		isSolverRunning = false;
		routineGenerationPercentage = 0;
		wasTimedOut = false;
		nodes = 0;
		solveTime = 0;
		bestScore = Integer.MAX_VALUE;
		bestAssignment.clear();

		if (efficiency.equals("low")) {
			shouldTakeFirstSolution = true;
			MAX_NODES = 1_000_000;
			divisor = 100_000;
		} else if (efficiency.equals("medium")) {
			MAX_NODES = 1_000_000;
			divisor = 100_000;
		} else if (efficiency.equals("high")) {
			MAX_NODES = 3_000_000;
			divisor = 300_000;
		} else {
			MAX_NODES = 5_000_000;
			divisor = 500_000;
		}
	}

	public static boolean startSolve(CSPState s) {
		System.out.println("ssssssssssssssssssssssssshoilf 1st" + shouldTakeFirstSolution);
		long start = System.currentTimeMillis();
		isSolverRunning = true;
		routineGenerationPercentage = 0;

		boolean result = solve(s);

		solveTime = System.currentTimeMillis() - start;
		System.out.println("Best Assignment = " + bestAssignment.size());
		System.out.println("Best Skipped = " + bestSkipped.size());

		isSolverRunning = false;
		routineGenerationPercentage = 100;

		return result;
	}

	public static boolean solve(CSPState s) {

		if (++nodes > MAX_NODES) {
			wasTimedOut = true;
			return true;
		}

		if (nodes % divisor == 0) {
			routineGenerationPercentage += 10;
			System.out.println("Visited nodes: " + nodes);
		}

		if (allDone(s)) {
			// System.out.println("passssssssssssssssssspasssssss");
			if (currentAssignedCount(s) == s.variables.size()) {

				if (!validateLabOriented(s) || !validateLab(s))
					return false;
			}

			int assignedCount = currentAssignedCount(s);

			if (assignedCount > bestPartialAssignedCount) {

				bestPartialAssignedCount = assignedCount;

				bestScore = SoftConstraints.score(s);

				bestAssignment.clear();
				bestSkipped.clear();

				for (Variable var : s.variables.values()) {

					if (var.assigned) {
						bestAssignment.put(var.id, var.assignedValue);
					}

					if (var.skipped) {
						bestSkipped.add(var.id);
					}
				}

			} else if (assignedCount == bestPartialAssignedCount) {

				int score = SoftConstraints.score(s);

				if (score < bestScore) {

					bestScore = score;

					bestAssignment.clear();
					bestSkipped.clear();

					for (Variable v : s.variables.values()) {

						if (v.assigned)
							bestAssignment.put(v.id, v.assignedValue);

						if (v.skipped)
							bestSkipped.add(v.id);
					}
				}

			}

			if (shouldTakeFirstSolution) {
				routineGenerationPercentage = 100;
				return true;
			}
			return false;
		}

		Variable v = Heuristics.selectMRVDegree(s);

//		if (v == null || v.domain.isEmpty())
//			return false;
		if (v == null) {
			return false;
		}
		List<Value> values = new ArrayList<>(v.domain);

		for (Value val : values) {

			if (!consistent(s, v, val))
				continue;

			assign(s, v, val);

			Map<String, List<Value>> removed = ForwardChecker.prune(s, v, val);

//			if (removed != null) {

			boolean result = solve(s);

			ForwardChecker.restore(s, removed);

			if (result)
				return true;
//			}

			unassign(s, v, val);
		}

		// No value could be assigned.
		// Mark this variable as TBA and continue.

		v.skipped = true;

		boolean result = solve(s);

		v.skipped = false;

		return result;
	}

	private static boolean allDone(CSPState s) {

		for (Variable v : s.variables.values()) {

			if (!v.assigned && !v.skipped)
				return false;
		}

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

		// // HARD: preferred teacher
//		if (outsidePreferred.equalsIgnoreCase("no")) {
//			if (c.preferredTeachers != null && !c.preferredTeachers.contains(val.teacherId))
//				return false;
//		}

		if (!t.availability.get(val.day)) {
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

		if (v.course.type == CourseType.LAB) {
			if (r.type != RoomType.LAB)
				return false;

			if (r.labType == null || v.course.requiredLab != r.labType)
				return false;
		}

		// Same course for the same section cannot be scheduled twice on the same day.
		for (Variable other : s.variables.values()) {

			if (!other.assigned)
				continue;

			if (other == v)
				continue;

			if (other.course.id.equals(v.course.id) && other.section.id.equals(v.section.id)
					&& other.assignedValue.day == val.day) {

				return false;
			}
		}

//		for (Variable other : s.variables.values()) {
//			if (!other.assigned)
//				continue;
//
////			if (other.course.id.equals(v.course.id) && other.section.id.equals(v.section.id)
////					&& other.assignedValue.day == val.day) {
////				return false;
////			}
//			// allow same day, but not overlapping
//			if (other.course.id.equals(v.course.id) && other.section.id.equals(v.section.id)
//					&& other.assignedValue.day == val.day && (other.assignedValue.slotMask & val.slotMask) != 0) {
//				return false;
//			}
//		}

		// Must be same teacher forr a specific section fpr same course
//		String k = key(v);
//		int assignedTeacher = s.courseSectionTeacher.getOrDefault(k, 0);
//
//		if (assignedTeacher != 0 && assignedTeacher != val.teacherId) {
//			return false;
//		}

		// Teacher weekly limit
		float currentLoad = s.teacherWeeklyLoad.get(val.teacherId);

		if (currentLoad + (float) (val.slotCount * 30.0) / (float) 60.0 > t.maxSlotHours) {
//		    return false;
		}

		return true;
	}

	/// HEleper FOR courseSectionTeacher
//	private static String key(Variable v) {
//		return v.section.id + "_" + v.course.id;
//	}

	private static void assign(CSPState s, Variable v, Value val) {
		v.assigned = true;
		v.assignedValue = val;

		// Reserve teacher, room, section timeslot
		s.teacherOccupied.get(val.teacherId)[val.day] |= val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
		s.sectionOccupied.get(v.section.id)[val.day] |= val.slotMask;

//		String k = key(v);

//		// assign teacher if first time
//		if (!s.courseSectionTeacher.containsKey(k)) {
//			s.courseSectionTeacher.put(k, val.teacherId);
//		}

		// NEW
		s.teacherWeeklyLoad.put(val.teacherId,
				s.teacherWeeklyLoad.get(val.teacherId) + (float) ((val.slotCount * 30.0) / 60.0));
	}

	private static void unassign(CSPState s, Variable v, Value val) {
		s.teacherOccupied.get(val.teacherId)[val.day] &= ~val.slotMask;
		s.roomOccupied.get(val.roomId)[val.day] &= ~val.slotMask;
		s.sectionOccupied.get(v.section.id)[val.day] &= ~val.slotMask;

		v.assigned = false;
		v.assignedValue = null;

//		String k = key(v);

		// check if this was the last variable using this teacher
//		boolean stillUsed = false;
//
//		for (Variable other : s.variables.values()) {
//			if (other == v || !other.assigned)
//				continue;
//
//			if (key(other).equals(k)) {
//				stillUsed = true;
//				break;
//			}
//		}
//
//		if (!stillUsed) {
//			s.courseSectionTeacher.remove(k);
//		}

		s.teacherWeeklyLoad.put(val.teacherId,
				s.teacherWeeklyLoad.get(val.teacherId) - (float) ((val.slotCount * 30.0) / 60.0));
	}

	private static boolean validateLabOriented(CSPState s) {
		Map<String, Boolean> usedLab = new HashMap<>();

		for (Variable v : s.variables.values()) {
			if (!v.assigned) {
				continue;
			}

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

			if (!v.assigned) {
				continue;
			}

			if (v.course.type == CourseType.LAB) {
				usedLab.putIfAbsent(v.course.id, true);
				Room r = s.rooms.get(v.assignedValue.roomId);
				if (r.type != RoomType.LAB)
					usedLab.put(v.course.id, false);
			}
		}
		return !usedLab.containsValue(false);
	}

	public static boolean isAllLabFitted(CSPState s) {
		return validateLab(s) && validateLabOriented(s);
	}
}
