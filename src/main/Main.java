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

	static final int DAYS = 7;
	static final int SLOTS_PER_DAY = 14;

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

		long FULL_DAY = (1L << SLOTS_PER_DAY) - 1;
		long LUNCH_MASK = ~((1L << 6) | (1L << 7));
		long DAY_WITHOUT_LUNCH = FULL_DAY & LUNCH_MASK;

		Room r1 = new Room("R801", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH });
		Room r2 = new Room("R803", RoomType.THEORY, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH });
		Room l1 = new Room("L802", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH });
		Room l2 = new Room("L902", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH });
		Room l3 = new Room("L903", RoomType.LAB, 60, new long[] { DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH,
				DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH, DAY_WITHOUT_LUNCH });

		enum CN {
			MATH, PHYSICS, PHYSICS_LAB, COMPUTER_SCIENCE, ENGLISH, HISTORY, CHEMISTRY, PROGRAMING_LAB, CHEMISTRY_LAB,
			STATISTIC, SWE, PRESENTATION
		}
		enum TN {
			RAHMAN, DAS, SULTANA, CHOWDHURY, KAFI, NOOR, ALAM, 
		}

		Teacher t1 = new Teacher(TN.RAHMAN.name(),
				Set.of(CN.MATH.name(),CN.COMPUTER_SCIENCE.name(),CN.HISTORY.name(),CN.PROGRAMING_LAB.name(),CN.STATISTIC.name()),
				new boolean[] { true, true, true, true, true, true, true });

		Teacher t2 = new Teacher(TN.DAS.name(),
				Set.of(CN.MATH.name(),CN.HISTORY.name(),CN.PRESENTATION.name()),
				new boolean[] { true, true, true, true, true, true, true });
		
		Teacher t3 = new Teacher(TN.SULTANA.name(),
				Set.of(CN.PHYSICS.name(),CN.PHYSICS_LAB.name(),CN.STATISTIC.name()),
				new boolean[] { true, true, true, true, true, true, true });
		
		Teacher t4 = new Teacher(TN.CHOWDHURY.name(),
				Set.of(CN.PHYSICS.name(),CN.ENGLISH.name(),CN.SWE.name()),
				new boolean[] { true, true, true, true, true, true, true });

		Teacher t5 = new Teacher(TN.KAFI.name(),
				Set.of(CN.COMPUTER_SCIENCE.name(),CN.CHEMISTRY.name(),CN.PHYSICS_LAB.name(),CN.PROGRAMING_LAB.name(),CN.SWE.name()),
				new boolean[] { true, true, true, true, true, true, true });

		Teacher t6 = new Teacher(TN.NOOR.name(),
				Set.of(CN.ENGLISH.name(),CN.CHEMISTRY_LAB.name(),CN.PRESENTATION.name()),
				new boolean[] { true, true, true, true, true, true, true });

		Teacher t7 = new Teacher(TN.ALAM.name(), Set.of(CN.CHEMISTRY.name(),CN.CHEMISTRY_LAB.name()),
				new boolean[] { true, true, true, true, true, true, true });

		

		Section A = new Section("A", 40);
		Section B = new Section("B", 35);
		Section C = new Section("C", 35);
		
		Course c1 = new Course(CN.MATH.name(), CourseType.THEORY,
				Set.of(TN.RAHMAN.name(), TN.DAS.name()), Set.of(A.id, B.id));

		Course c2 = new Course(CN.ENGLISH.name(), CourseType.THEORY,
				Set.of(TN.CHOWDHURY.name(), TN.NOOR.name()),
				Set.of(A.id, B.id, C.id));

		Course c3 = new Course(CN.COMPUTER_SCIENCE.name(), CourseType.THEORY,
				Set.of(TN.KAFI.name(), TN.RAHMAN.name()), Set.of(A.id, C.id));

		Course c4 = new Course(CN.HISTORY.name(), CourseType.THEORY,
				Set.of(TN.DAS.name(), TN.RAHMAN.name()), Set.of(A.id, B.id));

		Course c5 = new Course(CN.PROGRAMING_LAB.name(), CourseType.LAB,
				Set.of(TN.KAFI.name(), TN.RAHMAN.name()),
				Set.of(A.id, B.id));
		
		Course c6 = new Course(CN.PRESENTATION.name(), CourseType.LAB_ORIENTED_THEORY,
				Set.of(TN.NOOR.name(), TN.DAS.name()), Set.of(A.id, C.id));

		Course c7 = new Course(CN.PHYSICS.name(), CourseType.THEORY,
				Set.of(TN.SULTANA.name(), TN.CHOWDHURY.name()), Set.of(B.id, C.id));

		Course c8 = new Course(CN.CHEMISTRY.name(), CourseType.THEORY,
				Set.of(TN.ALAM.name(), TN.KAFI.name()), Set.of(B.id, C.id));

		Course c9 = new Course(CN.PHYSICS_LAB.name(), CourseType.LAB,
				Set.of(TN.SULTANA.name(), TN.KAFI.name()), Set.of(B.id, C.id));

		Course c10 = new Course(CN.CHEMISTRY_LAB.name(), CourseType.LAB,
				Set.of(TN.ALAM.name(), TN.NOOR.name()), Set.of(B.id, C.id));

		Course c11 = new Course(CN.STATISTIC.name(), CourseType.LAB_ORIENTED_THEORY,
				Set.of(TN.RAHMAN.name(), TN.SULTANA.name()), Set.of(B.id, C.id));

		Course c12 = new Course(CN.SWE.name(), CourseType.LAB_ORIENTED_THEORY,
				Set.of(TN.KAFI.name(), TN.CHOWDHURY.name()),
				Set.of(B.id, C.id));

	

		List<Course> courses = List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, c11, c12);

		Map<String, Section> sections = Map.ofEntries(Map.entry(A.id, A), Map.entry(B.id, B),
				Map.entry(C.id, C));

		Map<String, Teacher> teachers = Map.ofEntries(Map.entry(t1.id, t1), Map.entry(t2.id, t2), Map.entry(t3.id, t3),
				Map.entry(t4.id, t4), Map.entry(t5.id, t5), Map.entry(t6.id, t6), Map.entry(t7.id, t7));

		Map<String, Room> rooms = Map.of(r1.id, r1, r2.id, r2, l1.id, l1, l2.id, l2,
				l3.id, l3);

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
					System.out.println("❌ Lunch violation: " + v.id);
				}
			}
		}

		CSPState state = new CSPState(teachers, rooms, sections);

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

		System.out.println("Solved: " + solved);

		if (solved) {
			RoutinePrinter.print(state);
		}

	}

}
