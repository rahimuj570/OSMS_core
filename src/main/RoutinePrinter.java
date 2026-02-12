package main;

import algorithm.CSPState;
import algorithm.Variable;
import algorithm.Value;
import entity.Course;
import entity.Section;

import java.util.*;

public class RoutinePrinter {

	private static final String[] DAYS = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday" };

	public static void print(CSPState state) {

		System.out.println("\n===== FINAL ROUTINE =====\n");

		List<Variable> vars = new ArrayList<>(state.variables.values());

		// Sort by day → startSlot
		vars.sort(Comparator.comparingInt((Variable v) -> v.assignedValue.day)
				.thenComparingInt(v -> v.assignedValue.startSlot));

		for (String cl : List.of("A", "B", "C")) {
			
			System.out.println("________Class "+cl+" _____\n");

			for (Variable v : vars) {
				
				if(v.section.id!=cl) {
					continue;
				}
				Value val = v.assignedValue;
				Course c = v.course;
				Section s = v.section;

				String day = DAYS[val.day];
				String time = timeRange(val.startSlot, val.slotCount);

				System.out.println("Course: " + c.id + " | Section: " + s.id + " | Teacher: " + val.teacherId
						+ " | Room: " + val.roomId + " | Day: " + day + " | Time: " + time);
			}

			System.out.println("\n=========================\n");
		}
	}

	private static String timeRange(int startSlot, int slotCount) {
		int startHour = 9 + (startSlot*30)/60;
		int endHour = startHour + (slotCount * 30) / 60;
		return startHour + ":"+(startSlot*30)%60+" - " + (endHour+":"+((startSlot*30)%60+ (slotCount*30)%60));
	}
}
