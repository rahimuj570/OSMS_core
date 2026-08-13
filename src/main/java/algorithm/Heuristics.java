package algorithm;

public class Heuristics {

	public static Variable selectMRVDegree(CSPState s) {
		Variable best = null;
		int minDomain = Integer.MAX_VALUE;
		int maxDegree = -1;

		for (Variable v : s.variables.values()) {
			if (v.assigned || v.skipped)
				continue;

			int d = v.domain.size();
			int deg = v.neighbors.size();

			if (d < minDomain || (d == minDomain && deg > maxDegree)) {
				minDomain = d;
				maxDegree = deg;
				best = v;
			}
		}
		return best;
	}
	
}
