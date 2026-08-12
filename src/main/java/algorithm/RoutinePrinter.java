package algorithm;

import entity.Course;
import entity.Section;

import java.util.*;

public class RoutinePrinter {

	public static final String[] DAYS = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday",
			"Friday" };

	public static void print(CSPState state) {

		System.out.println("\n===== FINAL ROUTINE =====\n");

		List<Variable> vars = new ArrayList<>(state.variables.values());

		// Sort by day → startSlot
		vars.sort(Comparator.comparingInt((Variable v) -> v.assignedValue.day)
				.thenComparingInt(v -> v.assignedValue.startSlot));

//		for (String cl : List.of("A", "B", "C")) {
		state.sections.forEach((cl, u) -> {
			
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
		});
	}

	public static String timeRange(int startSlot, int slotCount) {
		int startTotalMin = 9 * 60 + startSlot * 30;
		int endTotalMin = startTotalMin + slotCount * 30;
		return formatTime(startTotalMin) + " - " + formatTime(endTotalMin);
	}

	private static String formatTime(int totalMinutes) {
		int hour = totalMinutes / 60;
		int min = totalMinutes % 60;
		String ampm = hour >= 12 ? "PM" : "AM";
		if (hour > 12) hour -= 12;
		if (hour == 0) hour = 12;
		return hour + ":" + (min < 10 ? "0" : "") + min + " " + ampm;
	}
}
