package algorithm;

import java.util.*;

public class ForwardChecker {

	public static Map<String, List<Value>> prune(CSPState s, Variable v, Value val) {
		Map<String, List<Value>> removed = new HashMap<>();

		for (String nid : v.neighbors) {
			Variable n = s.variables.get(nid);
			if (n.assigned)
				continue;

			Iterator<Value> it = n.domain.iterator();
			while (it.hasNext()) {
				Value nv = it.next();
				if (conflict(val, nv)) {
					removed.computeIfAbsent(nid, k -> new ArrayList<>()).add(nv);
					it.remove();
				}
			}
			if (n.domain.isEmpty())
				return null;
		}
		return removed;
	}

	public static void restore(CSPState s, Map<String, List<Value>> removed) {
		if (removed == null)
			return;
		for (String k : removed.keySet()) {
			s.variables.get(k).domain.addAll(removed.get(k));
		}
	}

	private static boolean conflict(Value a, Value b) {
		return a.day == b.day && (a.slotMask & b.slotMask) != 0;
	}
}
