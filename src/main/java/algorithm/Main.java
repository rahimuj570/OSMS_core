package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;
import helper.CSVRoutineExporter;
import helper.CSVTeacherScheduleExporter;
import local_db.CourseData;
import local_db.RoomData;
import local_db.SectionData;
import local_db.TeacherData;

public class Main {

	public static boolean timeout = false;
	public static final int DAYS = 7;
	public static final int SLOTS_PER_DAY = 14;
	public static CSPState state;

	static List<Variable> generateVariables(List<Course> courses, Map<String, Section> sections) {
		List<Variable> vars = new ArrayList<>();

		for (Course c : courses) {
			for (String secId : c.sectionIds) {
				Section sec = sections.get(secId);
				switch (c.type) {
				case THEORY -> {
					vars.add(new Variable(c.id + "_" + secId + "_1", c, sec));
					vars.add(new Variable(c.id + "_" + secId + "_2", c, sec));
				}
				case LAB -> {
					vars.add(new Variable(c.id + "_" + secId + "_LAB", c, sec));
				}
				case LAB_ORIENTED_THEORY -> {
					vars.add(new Variable(c.id + "_" + secId + "_1", c, sec));
					vars.add(new Variable(c.id + "_" + secId + "_2", c, sec));
				}
				}
			}
		}
		return vars;
	}

	static void generateDomains(List<Variable> vars, Map<Integer, Teacher> teachers, Map<String, Room> rooms) {

		for (Variable v : vars) {

			int slotCount = switch (v.course.type) {
			case THEORY -> 3;
			case LAB -> 6;
			case LAB_ORIENTED_THEORY -> 3;
			};

			for (int day = 0; day < DAYS; day++) {

				for (Teacher t : teachers.values()) {

// check blacklist
					if (v.course.forbiddenTeachers != null && v.course.forbiddenTeachers.contains(t.id))
						continue;

// check availability
					if (!t.availability.get(day))
						continue;
//////////////////////////////////////////////////////////////////////////////////////
					if (!v.course.preferredTeachers.contains(t.id)) {
						continue;
					}
					////////////////////////////////////////////////////////////////////////
					///
					///
					///
					///
					for (Room r : rooms.values()) {

						if (!roomAllowed(v.course.type, r))
							continue;

						if (v.course.type == CourseType.LAB) {
							if (r.type != RoomType.LAB)
								continue;
							if (r.labType != v.course.requiredLab)
								continue;
						}

						if (v.course.type == CourseType.THEORY && r.type == RoomType.LAB)
							continue;

						if (r.capacity < v.section.students)
							continue;

						long roomDayMask = r.availability[day];

//						for (int start = 0; start + slotCount <= SLOTS_PER_DAY; start++) {
//
//							long neededMask = ((1L << slotCount) - 1) << start;
//
//							if ((roomDayMask & neededMask) != neededMask)
//								continue;
//
//							v.domain.add(new Value(day, start, slotCount, r.id, t.id));
//						}
						long lunchMask = ((1L << 2) - 1) << 6; // slots 6 & 7 are lunch

						for (int start = 0; start + slotCount <= SLOTS_PER_DAY; start++) {

							long neededMask = ((1L << slotCount) - 1) << start;

							// ❌ BLOCK lunch overlap
							if ((neededMask & lunchMask) != 0)
								continue;

							if ((roomDayMask & neededMask) != neededMask)
								continue;

							v.domain.add(new Value(day, start, slotCount, r.id, t.id));
						}
					}
				}
			}

// sort domain (preferred teacher first)
			v.domain.sort((a, b) -> {
				boolean aPref = v.course.preferredTeachers != null && v.course.preferredTeachers.contains(a.teacherId);

				boolean bPref = v.course.preferredTeachers != null && v.course.preferredTeachers.contains(b.teacherId);

				return Boolean.compare(bPref, aPref); // preferred first
			});
		}
	}

	static boolean roomAllowed(CourseType type, Room r) {
		return switch (type) {
		case THEORY -> r.type == RoomType.THEORY;
		case LAB -> r.type == RoomType.LAB;
		case LAB_ORIENTED_THEORY -> true;
		};
	}

	static void buildNeighbors(List<Variable> vars) {
		for (int i = 0; i < vars.size(); i++) {
			for (int j = i + 1; j < vars.size(); j++) {

				Variable a = vars.get(i);
				Variable b = vars.get(j);

				// only section-based conflict
				if (a.section.id.equals(b.section.id)) {
					a.neighbors.add(b.id);
					b.neighbors.add(a.id);
				}
			}
		}
	}

	public static boolean isComplete = false;
	public static boolean isLabOrientedIncomplete = false;

	public static void main(String[] args) {
		// Delegate to run() with fresh data from DB (backward compatibility)
		List<Course> courses = CourseData.getCourses(null);
		Map<String, Section> sections = SectionData.getSections(null);
		Map<Integer, Teacher> teachers = TeacherData.getTeachers(null);
		Map<String, Room> rooms = RoomData.getRooms(null);
		run(courses, sections, teachers, rooms);
	}

	/**
	 * Entry point that accepts data snapshots directly, avoiding shared static state.
	 * Used by GenerateRoutineServlet to pass per-request data.
	 */
	public static void run(List<Course> courses, Map<String, Section> sections,
			Map<Integer, Teacher> teachers, Map<String, Room> rooms) {

		List<Variable> vars = generateVariables(courses, sections);
		generateDomains(vars, teachers, rooms);

		// Debug output
		for (Variable v : vars) {
			System.out.println(v.id + " → domain size: " + v.domain.size());
		}

		// Debug Output
		for (Variable v : vars) {
			for (Value val : v.domain) {
				if ((val.slotMask & ((1L << 6) | (1L << 7))) != 0) {
					System.out.println(" *** Lunch violation: " + v.id);
				}
			}
		}

		state = new CSPState(teachers, rooms, sections);

		for (Variable v : vars) {
			state.variables.put(v.id, v);
		}
		buildNeighbors(vars);

		CSPSolver.reset();
		boolean solved = CSPSolver.startSolve(state);

		Map<String, Value> bestAssignment = CSPSolver.getBestAssignment();
		Set<String> bestSkipped = CSPSolver.getBestSkipped();

		isComplete = bestAssignment.size() == state.variables.size();

		if (isComplete) {
		    System.out.println("Complete solution found.");
		} else {
		    System.out.println("Partial solution found.");
		    System.out.println("Assigned : " + bestAssignment.size());
		    System.out.println("TBA : " + bestSkipped.size());

		    for (String id : bestSkipped) {
		        System.out.println(id);
		    }
		}
		


//		 restore best found assignment
		if (!bestAssignment.isEmpty()) {

			state.clearOccupations();
			
			for (Teacher t : state.teachers.values()) {
			    state.teacherWeeklyLoad.put(t.id, 0f);
			}

			for (Variable v : state.variables.values()) {

			    Value val = bestAssignment.get(v.id);

			    if (val == null) {
			        v.assigned = false;
			        v.assignedValue = null;
			        v.skipped = bestSkipped.contains(v.id);
			        continue;
			    }

			    v.assigned = true;
			    v.skipped = false;
			    v.assignedValue = val;

			    state.teacherOccupied.get(val.teacherId)[val.day] |= val.slotMask;
			    state.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
			    state.sectionOccupied.get(v.section.id)[val.day] |= val.slotMask;
			    state.teacherWeeklyLoad.put(
			    	    val.teacherId,
			    	    state.teacherWeeklyLoad.get(val.teacherId)
			    	        + ((float) val.slotCount * 30f / 60f)
			    	);
			}

			solved = isComplete;
		}

		isLabOrientedIncomplete = !CSPSolver.isAllLabFitted(state);
		System.out.println("\nSolved: " + solved);
		System.out.println("Timeout: " + Main.timeout);
		System.out.println("BestAssignment size: " + CSPSolver.getBestAssignment().size());

		if (bestAssignment.isEmpty()) {

		    System.out.println("No solution found.");

		} else if (Main.timeout) {

		    System.out.println("⚠️ Timeout reached — best found solution used");

		} else if (isComplete) {

		    System.out.println("Complete solution restored.");

		} else {

		    System.out.println("Partial solution restored.");

		}
	}

}
