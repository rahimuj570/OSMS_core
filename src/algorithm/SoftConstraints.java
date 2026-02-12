package algorithm;

import java.util.HashMap;
import java.util.Map;


public class SoftConstraints {

	public static int score(CSPState s) {
		int p = 0;
		p += teacherLoadPenalty(s);
		p += sectionDaySpreadPenalty(s);
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

			// quadratic growth: 1, 4, 9, 16, ...
			penalty += c * c;
		}

		return penalty;
	}

}
