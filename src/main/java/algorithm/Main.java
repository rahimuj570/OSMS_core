package algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;
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

						for (int start = 0; start + slotCount <= SLOTS_PER_DAY; start++) {

							long neededMask = ((1L << slotCount) - 1) << start;

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

	public static void main(String[] args) {

		List<Variable> vars = generateVariables(CourseData.courses, SectionData.sections);
		generateDomains(vars, TeacherData.teachers, RoomData.rooms);

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

		state = new CSPState(TeacherData.teachers, RoomData.rooms, SectionData.sections);

		for (Variable v : vars) {
			state.variables.put(v.id, v);
		}
		buildNeighbors(vars);

		CSPSolver.reset();
		boolean solved = CSPSolver.solve(state);

		if (!CSPSolver.getBestAssignment().isEmpty()) {
			System.out.println("Solution FOUND");
		} else {
			System.out.println("NO solution exists");
		}

		int countAssigned = 0;
		int countUnassigned = 0;
		int countUnassignedTheory = 0;
		int countUnassignedLab = 0;
		int countUnasignedLabOrientedTheory = 0;
		ArrayList<String> unassigedVarId = new ArrayList<String>();
		for (var a : state.variables.values()) {
			if (a.assigned) {
				countAssigned++;
			} else {
				countUnassigned++;
				if (a.course.type == CourseType.LAB) {
					countUnassignedLab++;
				} else if (a.course.type == CourseType.THEORY) {
					countUnassignedTheory++;
				} else {
					countUnasignedLabOrientedTheory++;
				}
				unassigedVarId.add(a.id);
			}
		}

//		 restore best found assignment
		if (!CSPSolver.getBestAssignment().isEmpty()) {

			// clear occupation maps first
			state.clearOccupations();

			for (Variable v : state.variables.values()) {
				v.assigned = true;
				v.assignedValue = CSPSolver.getBestAssignment().get(v.id);

				Value val = v.assignedValue;

				state.teacherOccupied.get(val.teacherId)[val.day] |= val.slotMask;
				state.roomOccupied.get(val.roomId)[val.day] |= val.slotMask;
				state.sectionOccupied.get(v.section.id)[val.day] |= val.slotMask;
			}

			solved = true;
		}

		System.out.println("\nSolved: " + solved);
		System.out.println("Timeout: " + Main.timeout);
		System.out.println("BestAssignment size: " + CSPSolver.getBestAssignment().size());

		if (CSPSolver.getBestAssignment().isEmpty()) {
			System.out.println("Total Variable= " + state.variables.size());
			System.out.println("Total Assigned Variable= " + countAssigned);
			System.out.println("Total Unassigned Variable= " + countUnassigned);
			System.out.println("Total Unassigned Lab Variable= " + countUnassignedLab);
			System.out.println("Total Unassigned Theory Variable= " + countUnassignedTheory);
			System.out.println("Total Unassigned Lab_oriented_theory Variable= " + countUnasignedLabOrientedTheory);
			for (var id : unassigedVarId) {
				System.out.println(id);
			}

		} else {
			System.out.println("⚠️ Timeout reached — best found solution used");
			RoutinePrinter.print(state);
		}

	}

}
