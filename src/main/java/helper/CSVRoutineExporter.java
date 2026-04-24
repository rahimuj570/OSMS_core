package helper;

import algorithm.CSPState;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Section;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVRoutineExporter {

    public static final String[] DAYS = {
            "Saturday", "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday"
    };

    public static void export(CSPState state, String fileName) {

        try (FileWriter writer = new FileWriter(fileName)) {

            List<Variable> vars = new ArrayList<>(state.variables.values());

            // Sort by day → startSlot
            vars.sort(Comparator
                    .comparingInt((Variable v) -> v.assignedValue.day)
                    .thenComparingInt(v -> v.assignedValue.startSlot));

            // Loop per section (same as your printer)
            state.sections.forEach((secId, section) -> {

                try {

                    // Section header
                    writer.append("Class ").append(secId).append("\n");

                    // Column header
                    writer.append("Course,Section,Teacher,Room,Day,Time\n");

                    for (Variable v : vars) {

                        if (!v.section.id.equals(secId))
                            continue;

                        if (!v.assigned || v.assignedValue == null)
                            continue;

                        Value val = v.assignedValue;
                        Course c = v.course;
                        Section s = v.section;

                        String day = DAYS[val.day];
                        String time = timeRange(val.startSlot, val.slotCount);

                        writer.append(c.id).append(",")
                                .append(s.id).append(",")
                                .append(String.valueOf(val.teacherId)).append(",")
                                .append(val.roomId).append(",")
                                .append(day).append(",")
                                .append(time)
                                .append("\n");
                    }

                    writer.append("\n=========================\n\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            writer.flush();
            System.out.println("✅ CSV Exported: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SAME logic as your printer
    public static String timeRange(int startSlot, int slotCount) {

        int startMinutes = startSlot * 30;
        int endMinutes = startMinutes + (slotCount * 30);

        int startHour = 9 + startMinutes / 60;
        int startMin = startMinutes % 60;

        int endHour = 9 + endMinutes / 60;
        int endMin = endMinutes % 60;

        return formatTime(startHour, startMin) + " - " + formatTime(endHour, endMin);
    }

    private static String formatTime(int hour, int min) {
        return String.format("%02d:%02d", hour, min);
    }
}