package helper;

import algorithm.CSPState;
import algorithm.Value;
import algorithm.Variable;
import entity.Course;
import entity.Teacher;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CSVTeacherScheduleExporter {

    public static final String[] DAYS = {
            "Saturday", "Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday"
    };

    public static void export(CSPState state, String fileName) {

        try (FileWriter writer = new FileWriter(fileName)) {

            List<Variable> vars = new ArrayList<>(state.variables.values());

            // sort like your JSP
            vars.sort(Comparator
                    .comparingInt((Variable v) -> v.assignedValue.day)
                    .thenComparingInt(v -> v.assignedValue.startSlot));

            for (Teacher t : state.teachers.values()) {

                // ===== TEACHER HEADER =====
                writer.append("Teacher: ").append(t.name).append("\n");

                // ===== COLUMN HEADER =====
                writer.append("Day,Time,Course,Section,Room\n");

                for (Variable v : vars) {

                    if (!v.assigned || v.assignedValue == null)
                        continue;

                    if (v.assignedValue.teacherId != t.id)
                        continue;

                    Value val = v.assignedValue;
                    Course c = v.course;

                    String day = DAYS[val.day];
                    String time = timeRange(val.startSlot, val.slotCount);

                    writer.append(day).append(",")
                            .append(time).append(",")
                            .append(c.id).append(",")
                            .append(v.section.id).append(",")
                            .append(val.roomId)
                            .append("\n");
                }

                writer.append("\n=========================\n\n");
            }

            writer.flush();
            System.out.println("✅ Teacher CSV Exported: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // same logic as your RoutinePrinter
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