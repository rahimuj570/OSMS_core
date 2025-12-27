package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import algorithm.CSPState;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Room;

public class RoutinePrinter {

	private static final String[] DAYS = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday" };

	public static void print(CSPState state) {
		System.out.println("\n========== GENERATED WEEKLY ROUTINE ==========\n");

		// Group by Section
		Map<String, List<Variable>> bySection = new HashMap();

		for (Variable v : state.variables.values()) {
			bySection.computeIfAbsent(v.course.sectionId, k -> new ArrayList<>()).add(v);
		}

		for (String sectionId : bySection.keySet()) {
			System.out.println("SECTION: " + sectionId);
			System.out.println("----------------------------------");

			List<Variable> list = bySection.get(sectionId);

			// Sort by day then time
			list.sort(Comparator.comparingInt((Variable v) -> v.assignedValue.day)
					.thenComparingInt(v -> v.assignedValue.startSlot));

			for (Variable v : list) {
				printEntry(v, state);
			}

			System.out.println();
		}
	}

	private static void printEntry(Variable v, CSPState state) {
		Value val = v.assignedValue;
		Course c = v.course;
		Room r = state.rooms.get(val.roomId);

		String time = slotToTime(val.startSlot, val.slotCount);

		System.out.printf("%-10s | %-9s | %-20s | %-5s | %-4s%n", DAYS[val.day], time, c.id + " (" + c.type + ")", r.id,
				r.type);
	}

	private static String slotToTime(int start, int len) {
		int startMin = start * 30;
		int endMin = startMin + len * 30;

		return format(startMin) + "-" + format(endMin);
	}

	private static String format(int mins) {
		int h = 8 + mins / 60; // assume day starts at 8:00
		int m = mins % 60;
		return String.format("%02d:%02d", h, m);
	}
}
