package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import entity.Course;
import entity.Room;
import entity.Section;
import entity.Teacher;


public class SoftConstraints {

	public static int score(CSPState s) {
		int p = 0;
		p += teacherLoadPenalty(s);
		p += sectionDaySpreadPenalty(s);
		p += preferredTeacherPenalty(s);
		p+=maxHourPanalty(s);
		return p;
	}

	private static int sectionDaySpreadPenalty(CSPState s) {
		int penalty = 0;

		for (String secId : s.sections.keySet()) {
			long[] days = s.sectionOccupied.get(secId);
			int used = 0;
			for (int d = 0; d < days.length; d++) {
				if (days[d] != 0)
					used++;
			}

			if (used > 4)
				penalty += (used - 4) * 5;
		}

		return penalty;
	}

	public static int teacherLoadPenalty(CSPState s) {
		Map<Integer, Integer> count = new HashMap<>();
		int penalty = 0;

		for (Variable v : s.variables.values()) {
			int t = v.assignedValue.teacherId;

			int c = count.getOrDefault(t, 0) + 1;
			count.put(t, c);

			if (c > 6) {
				penalty += (c - 6) * (c - 6);
			}
		}

		return penalty;
	}

	private static int preferredTeacherPenalty(CSPState s) {
		int penalty = 0;
//
//	    for (Variable v : s.variables.values()) {
//
//	        if (!v.assigned) continue;
//
//	        int t = v.assignedValue.teacherId;
//
//	        if (v.course.preferredTeachers != null &&
//	            !v.course.preferredTeachers.contains(t)) {
//
//	            penalty += 100; 
//	        }
//	    }

//	    Map<String, Integer> assigned = s.courseSectionTeacher;
//
//	    for (String key : assigned.keySet()) {
//
//	        int teacher = assigned.get(key);
//
//	        // extract courseId
//	        String courseId = key.split("_")[1];
//
//	        Course c = CourseData.courses.stream().filter(ct -> ct.id==courseId).findFirst().orElse(null);
//
//	        if (c!=null && c.preferredTeachers != null &&
//	            !c.preferredTeachers.contains(teacher)) {
//
//	            penalty += 200;
//	        }
//	    }
//
		return penalty;
	}

	public static int maxHourPanalty(CSPState s) {
		int pan = 0;
		for (Variable v : s.variables.values()) {
			if (!v.assigned)
				continue;
			Value val = v.assignedValue;
			Course c = v.course;
			Teacher t = s.teachers.get(val.teacherId);
			Room r = s.rooms.get(val.roomId);
			Section sec = s.sections.get(v.section.id);

			float currentLoad = s.teacherWeeklyLoad.get(val.teacherId);
			if (currentLoad + (float) (val.slotCount * 30.0) / (float) 60.0 > t.maxSlotHours) {
				pan += 60;
			}
		}
		return pan;
	}

}
