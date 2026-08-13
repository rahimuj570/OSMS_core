package ai_prompt;

import algorithm.AvailabilityHelper;
import algorithm.CSPState;
import algorithm.RoutineGenerationResult;
import algorithm.TimeSlotInfo;
import algorithm.Variable;
import entity.Teacher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AIPromptGenerator {

    private AIPromptGenerator() {
    }

    public static String generate(RoutineGenerationResult result) {
        CSPState state = result.getState();

        StringBuilder sb = new StringBuilder();

        sb.append(PromptConstants.HEADER);

        List<Variable> unassigned = new ArrayList<>();

        for (Variable v : state.variables.values()) {

            if (!v.assigned) {
                unassigned.add(v);
            }

        }

        Collections.sort(unassigned, Comparator.comparing(v -> v.id));

        for (Variable variable : unassigned) {

            appendCourse(sb, state, variable);

        }

        appendStatistics(sb, state, unassigned.size(), result);

        sb.append(PromptConstants.FOOTER);

        return sb.toString();
    }

    private static void appendCourse(
            StringBuilder sb,
            CSPState state,
            Variable variable) {

        sb.append("\n");
        sb.append("=====================================================\n\n");

        sb.append("Course ID:\n");
        sb.append(variable.course.id).append("\n\n");

        sb.append("Section:\n");
        sb.append(variable.section.id).append("\n\n");

        sb.append("Course Type:\n");
        sb.append(variable.course.type).append("\n\n");

        sb.append("Preferred Teachers:\n\n");

        if (variable.course.preferredTeachers == null ||
                variable.course.preferredTeachers.isEmpty()) {

            sb.append("None\n");

        } else {

            for (Integer teacherId : variable.course.preferredTeachers) {

                Teacher teacher = state.teachers.get(teacherId);

                if (teacher != null) {

                    sb.append("- ")
                            .append(teacher.name)
                            .append("\n");

                }

            }

        }

        sb.append("\n-----------------------------------------------------\n\n");

        sb.append("AVAILABLE CONFLICT-FREE TEACHER SLOTS\n\n");

        List<TimeSlotInfo> teacherSlots =
                AvailabilityHelper.getTeacherFreeSlots(state, variable);

        if (teacherSlots.isEmpty()) {

            sb.append("No available teacher slot.\n");

        } else {

            for (TimeSlotInfo slot : teacherSlots) {

                sb.append("Teacher : ")
                        .append(slot.name)
                        .append("\n");

                sb.append("Day : ")
                        .append(slot.getDayName())
                        .append("\n");

                sb.append("Time : ")
                        .append(slot.getTimeRange())
                        .append("\n\n");

            }

        }

        sb.append("-----------------------------------------------------\n\n");

        sb.append("AVAILABLE CONFLICT-FREE ROOMS\n\n");

        List<TimeSlotInfo> roomSlots =
                AvailabilityHelper.getRoomFreeSlots(state, variable);

        if (roomSlots.isEmpty()) {

            sb.append("No available room.\n");

        } else {

            for (TimeSlotInfo slot : roomSlots) {

                sb.append("Room : ")
                        .append(slot.name)
                        .append("\n");

                sb.append("Day : ")
                        .append(slot.getDayName())
                        .append("\n");

                sb.append("Time : ")
                        .append(slot.getTimeRange())
                        .append("\n\n");

            }

        }

        sb.append("=====================================================\n\n");

    }

    private static void appendStatistics(
            StringBuilder sb,
            CSPState state,
            int unassigned,
            RoutineGenerationResult result) {

        int total = state.variables.size();

        int assigned = total - unassigned;

        double completion =
                total == 0
                        ? 0
                        : assigned * 100.0 / total;

        sb.append("\n");
        sb.append("=====================================================\n");
        sb.append("SOLVER STATISTICS\n");
        sb.append("=====================================================\n\n");

        sb.append("Total Courses : ")
                .append(total)
                .append("\n");

        sb.append("Assigned : ")
                .append(assigned)
                .append("\n");

        sb.append("Unassigned : ")
                .append(unassigned)
                .append("\n");

        sb.append(String.format(
                "Completion : %.2f%%\n",
                completion));

        sb.append("States Explored : ")
                .append(result.getVisitedNodes())
                .append("\n");

        sb.append("Search Speed : ")
                .append(String.format(
                        "%.2f",
                        result.getStatesPerSecond()))
                .append(" states/second\n");

        sb.append("\n=====================================================\n\n");

    }

}