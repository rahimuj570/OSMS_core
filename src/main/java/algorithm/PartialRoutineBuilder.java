package algorithm;

import java.util.ArrayList;
import java.util.List;

import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Teacher;

public class PartialRoutineBuilder {

    public static final int TBA_TEACHER = -1;
    public static final String TBA_ROOM = "TBA";

    public static void build(CSPState state) {

        for (Variable v : state.variables.values()) {

            // already assigned by CSP
            if (v.assignedValue != null)
                continue;

            Value partial = createBestEffortAssignment(state, v);

            if (partial != null) {
                v.assigned = true;
                v.assignedValue = partial;
            }
        }
    }

    private static Value createBestEffortAssignment(
            CSPState state,
            Variable v) {

        int slotCount = switch (v.course.type) {
            case THEORY -> 3;
            case LAB -> 6;
            case LAB_ORIENTED_THEORY -> 3;
        };

        for (int day = 0; day < Main.DAYS; day++) {

            for (int start = 0;
                    start + slotCount <= Main.SLOTS_PER_DAY;
                    start++) {

                long slotMask =
                        ((1L << slotCount) - 1) << start;

                // section must be free
                if ((state.sectionOccupied
                        .get(v.section.id)[day]
                        & slotMask) != 0) {
                    continue;
                }

                String roomId =
                        findAvailableRoom(
                                state,
                                v,
                                day,
                                slotMask);

                Integer teacherId =
                        findAvailableTeacher(
                                state,
                                day,
                                slotMask);

                boolean roomFound =
                        roomId != null;

                boolean teacherFound =
                        teacherId != null;

                // At least one resource found
                if (roomFound || teacherFound) {

                    return new Value(
                            day,
                            start,
                            slotCount,
                            roomFound
                                    ? roomId
                                    : TBA_ROOM,
                            teacherFound
                                    ? teacherId
                                    : TBA_TEACHER);
                }
            }
        }

        // absolutely nothing possible
        return new Value(
                0,
                0,
                slotCount,
                TBA_ROOM,
                TBA_TEACHER);
    }

    private static String findAvailableRoom(
            CSPState state,
            Variable v,
            int day,
            long slotMask) {

        for (Room r : state.rooms.values()) {

            if (r.capacity < v.section.students)
                continue;

            if (v.course.type == CourseType.THEORY
                    && r.type == RoomType.LAB)
                continue;

            if (v.course.type == CourseType.LAB) {

                if (r.type != RoomType.LAB)
                    continue;

                if (r.labType == null
                        || r.labType != v.course.requiredLab)
                    continue;
            }

            if ((state.roomOccupied
                    .get(r.id)[day]
                    & slotMask) != 0)
                continue;

            return r.id;
        }

        return null;
    }

    private static Integer findAvailableTeacher(
            CSPState state,
            int day,
            long slotMask) {

        List<Teacher> teachers =
                new ArrayList<>(state.teachers.values());

        for (Teacher t : teachers) {

            if (!t.availability.get(day))
                continue;

            if ((state.teacherOccupied
                    .get(t.id)[day]
                    & slotMask) != 0)
                continue;

            return t.id;
        }

        return null;
    }
}