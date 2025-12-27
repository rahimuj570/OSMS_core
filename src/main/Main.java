package main;

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

	public static void main(String[] args) {
		// Dummy minimal data
		Map<String, Section> sections = Map.of("S1", new Section("S1", 40));

		Map<String, Teacher> teachers = Map.of("T1", new Teacher("T1", Set.of("C1"), new long[5]));

		Map<String, Room> rooms = Map.of("R1", new Room("R1", RoomType.THEORY, 50, new long[5]), "L1",
				new Room("L1", RoomType.LAB, 50, new long[5]));

		CSPState state = new CSPState(teachers, rooms, sections);

		Course c1 = new Course("C1", CourseType.LAB_ORIENTED_THEORY, "T1", "S1");

		Variable v1 = new Variable("C1_1", c1);
		Variable v2 = new Variable("C1_2", c1);

		state.variables.put(v1.id, v1);
		state.variables.put(v2.id, v2);

		v1.neighbors.add(v2.id);
		v2.neighbors.add(v1.id);

		// Sample domain
		v1.domain.add(new Value(0, 2, 3, "L1"));
		v2.domain.add(new Value(2, 2, 3, "L1"));

		v2.domain.add(new Value(2, 2, 3, "L1"));

		boolean solved = CSPSolver.solve(state);
		System.out.println("Solved: " + solved);
		if(solved) {
		    RoutinePrinter.print(state);
		}
	}
}
