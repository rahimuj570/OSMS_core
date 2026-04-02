package algorithm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class SoftConstraints {

	public static int score(CSPState s) {
	    int p = 0;
	    p += teacherLoadPenalty(s);
	    p += sectionDaySpreadPenalty(s);
	    p += preferredTeacherPenalty(s);
	    p += teacherConsistencyPenalty(s);
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
		Map<String, Integer> count = new HashMap<>();
		int penalty = 0;

		for (Variable v : s.variables.values()) {
			String t = v.assignedValue.teacherId;

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

	    for (Variable v : s.variables.values()) {

	        if (!v.assigned) continue;

	        String t = v.assignedValue.teacherId;

	        if (v.course.preferredTeachers != null &&
	            !v.course.preferredTeachers.contains(t)) {

	            penalty += 8; 
	        }
	    }

	    return penalty;
	}
	
	private static int teacherConsistencyPenalty(CSPState s) {

	    int penalty = 0;

	    // key = courseId + sectionId
	    Map<String, Set<String>> teacherMap = new HashMap<>();

	    for (Variable v : s.variables.values()) {

	        if (!v.assigned) continue;

	        String key = v.course.id + "_" + v.section.id;

	        teacherMap.putIfAbsent(key, new HashSet<>());
	        teacherMap.get(key).add(v.assignedValue.teacherId);
	    }

	    for (Set<String> teachers : teacherMap.values()) {

	        if (teachers.size() > 1) {
	            //penalty per multiple  teacher
	            penalty += (teachers.size() - 1) * 200;
	        }
	    }
	    return penalty;
	}
	
//	private static int sameDayClusterPenalty(CSPState s) {
//	    int penalty = 0;
//
//	    for (String secId : s.sections.keySet()) {
//
//	        long[] days = s.sectionOccupied.get(secId);
//
//	        for (int d = 0; d < days.length; d++) {
//
//	            int slotsUsed = Long.bitCount(days[d]);
//
//	            if (slotsUsed > 4) {
//	                penalty += (slotsUsed - 4) * 3;
//	            }
//	        }
//	    }
//
//	    return penalty;
//	}

}
