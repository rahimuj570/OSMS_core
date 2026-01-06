package main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import algorithm.CSPSolver;
import algorithm.CSPState;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Section;
import entity.Teacher;

public class Main {

	static final int DAYS = 5;
	static final int SLOTS_PER_DAY = 16;

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

	static void generateDomains(List<Variable> vars, Map<String, Teacher> teachers, Map<String, Room> rooms) {
		for (Variable v : vars) {

			int slotCount = switch (v.course.type) {
			case THEORY -> 3;
			case LAB -> 6;
			case LAB_ORIENTED_THEORY -> 3;
			};

			for (int day = 0; day < DAYS; day++) {

				for (String teacherId : v.course.teacherIds) {
					Teacher t = teachers.get(teacherId);

					if (t.availability[day] == false)
						continue;

					if (!t.qualifiedCourseIds.contains(v.course.id))
						continue;

					for (Room r : rooms.values()) {

						if (!roomAllowed(v.course.type, r))
							continue;
						if (r.capacity < v.section.students)
							continue;

						long roomDayMask = r.availability[day];

						for (int start = 0; start + slotCount <= SLOTS_PER_DAY; start++) {
							long neededMask = ((1L << slotCount) - 1) << start;

							if ((roomDayMask & neededMask) != neededMask)
								continue;

							v.domain.add(new Value(day, start, slotCount, r.id, teacherId));
						}
					}
				}
			}
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

				boolean sameSection = a.section.id.equals(b.section.id);

				boolean commonTeacher = false;
				for (String t : a.course.teacherIds) {
					if (b.course.teacherIds.contains(t)) {
						commonTeacher = true;
						break;
					}
				}

				if (sameSection || commonTeacher) {
					a.neighbors.add(b.id);
					b.neighbors.add(a.id);
				}
			}
		}
	}

	public static void main(String[] args) {

		Teacher t1 = new Teacher("T1", Set.of("C1", "C3"), new boolean[] { true, true, true, true, false // Fri (not
				// available)
		});

		Teacher t2 = new Teacher("T2", Set.of("C2", "C3"), new boolean[] { false, true, true, true, true });

		Room r1 = new Room("R1", RoomType.THEORY, 60, new long[] { 0b1111111111111111L, 0b1111111111111111L,
				0b1111111111111111L, 0b1111111111111111L, 0b1111111111111111L });

		Room l1 = new Room("L1", RoomType.LAB, 40, new long[] { 0b0000001111111100L, // slots 2–11
				0b0000001111111100L, 0b0000001111111100L, 0b0000001111111100L, 0b0000001111111100L });

		Section s1 = new Section("S1", 40);
		Section s2 = new Section("S2", 35);

		Course c1 = new Course("C1", CourseType.THEORY, Set.of("T1"), Set.of("S1", "S2"));

		Course c2 = new Course("C2", CourseType.LAB, Set.of("T2"), Set.of("S1"));

		Course c3 = new Course("C3", CourseType.LAB_ORIENTED_THEORY, Set.of("T1", "T2"), Set.of("S2"));

		List<Course> courses = List.of(c1, c2, c3);

		Map<String, Section> sections = Map.of("S1", s1, "S2", s2);

		Map<String, Teacher> teachers = Map.of("T1", t1, "T2", t2);

		Map<String, Room> rooms = Map.of("R1", r1, "L1", l1);

		List<Variable> vars = generateVariables(courses, sections);
		generateDomains(vars, teachers, rooms);

		// Debug output
		for (Variable v : vars) {
			System.out.println(v.id + " → domain size: " + v.domain.size());
		}

		CSPState state = new CSPState(teachers, rooms, sections);

		for (Variable v : vars) {
			state.variables.put(v.id, v);
		}
		buildNeighbors(vars);

		boolean solved = CSPSolver.solve(state);
		System.out.println("Solved: " + solved);

		if (solved) {
			RoutinePrinter.print(state);
		}

	}

}
