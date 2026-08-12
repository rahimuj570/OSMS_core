package algorithm;

import java.util.ArrayList;
import java.util.List;

import entity.CourseType;
import entity.Room;
import entity.RoomType;
import entity.Teacher;

public class AvailabilityHelper {

    /**
     * Returns all free slots of preferred teachers
     * for this course.
     */
    public static List<TimeSlotInfo> getTeacherFreeSlots(
            CSPState state,
            Variable variable) {
    	
		long lunchMask = ((1L << 2) - 1) << 6; // slots 6 & 7 are lunch

        List<TimeSlotInfo> result = new ArrayList<>();

        int slotCount = getRequiredSlotCount(variable);

        if (variable.course.preferredTeachers == null)
            return result;

        for (Integer teacherId : variable.course.preferredTeachers) {

            Teacher teacher = state.teachers.get(teacherId);

            if (teacher == null)
                continue;

            long[] occupied = state.teacherOccupied.get(teacher.id);

            for (int day = 0; day < Main.DAYS; day++) {

                // Teacher unavailable this day
                if (!teacher.availability.get(day))
                    continue;

                for (int start = 0;
                        start + slotCount <= Main.SLOTS_PER_DAY;
                        start++) {

                    long mask = ((1L << slotCount) - 1) << start;
                    if ((mask & lunchMask) != 0)
						continue;

                    // Busy
                    if ((occupied[day] & mask) != 0)
                        continue;

                    result.add(new TimeSlotInfo(
                            teacher.name,
                            day,
                            start,
                            slotCount));

                }
            }
        }

        return result;
    }

    /**
     * Returns all suitable room free slots.
     */
    public static List<TimeSlotInfo> getRoomFreeSlots(
            CSPState state,
            Variable variable) {

        List<TimeSlotInfo> result = new ArrayList<>();

        int slotCount = getRequiredSlotCount(variable);

        for (Room room : state.rooms.values()) {

            //-------------------------
            // Room Type
            //-------------------------

            if (variable.course.type == CourseType.THEORY &&
                    room.type == RoomType.LAB)
                continue;

            if (variable.course.type == CourseType.LAB) {

                if (room.type != RoomType.LAB)
                    continue;

                if (room.labType != variable.course.requiredLab)
                    continue;
            }

            //-------------------------
            // Capacity
            //-------------------------

            if (room.capacity < variable.section.students)
                continue;

            long[] occupied = state.roomOccupied.get(room.id);

            for (int day = 0; day < Main.DAYS; day++) {

                for (int start = 0;
                        start + slotCount <= Main.SLOTS_PER_DAY;
                        start++) {

                    long mask = ((1L << slotCount) - 1) << start;

                    // Room unavailable
                    if ((room.availability[day] & mask) != mask)
                        continue;

                    // Room occupied
                    if ((occupied[day] & mask) != 0)
                        continue;

                    result.add(new TimeSlotInfo(
                            room.id,
                            day,
                            start,
                            slotCount));

                }
            }
        }

        return result;
    }

    /**
     * Slot count required by course.
     */
    private static int getRequiredSlotCount(Variable variable) {

        switch (variable.course.type) {

        case THEORY:
            return 3;

        case LAB:
            return 6;

        case LAB_ORIENTED_THEORY:
            return 3;

        default:
            return 3;
        }
    }

}