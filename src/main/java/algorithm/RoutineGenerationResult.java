package algorithm;

import java.util.Map;
import java.util.Set;

/**
 * Per-invocation result of a routine generation run.
 *
 * Main's generation result/state is now scoped to an individual run, but CSPSolver
 * still contains static mutable state and therefore the complete solver is NOT thread-safe.
 */
public class RoutineGenerationResult {

    private final CSPState state;
    private final boolean timeout;
    private final boolean complete;
    private final boolean labOrientedIncomplete;
    private final Map<String, Value> bestAssignment;
    private final Set<String> bestSkipped;
    private final int visitedNodes;
    private final long solveTimeMs;
    private final double statesPerSecond;

    public RoutineGenerationResult(CSPState state, boolean timeout,
                                   boolean complete, boolean labOrientedIncomplete,
                                   Map<String, Value> bestAssignment,
                                   Set<String> bestSkipped,
                                   int visitedNodes, long solveTimeMs,
                                   double statesPerSecond) {
        this.state = state;
        this.timeout = timeout;
        this.complete = complete;
        this.labOrientedIncomplete = labOrientedIncomplete;
        this.bestAssignment = bestAssignment;
        this.bestSkipped = bestSkipped;
        this.visitedNodes = visitedNodes;
        this.solveTimeMs = solveTimeMs;
        this.statesPerSecond = statesPerSecond;
    }

    public CSPState getState() {
        return state;
    }

    public boolean isTimeout() {
        return timeout;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isLabOrientedIncomplete() {
        return labOrientedIncomplete;
    }

    public Map<String, Value> getBestAssignment() {
        return bestAssignment;
    }

    public Set<String> getBestSkipped() {
        return bestSkipped;
    }

    public int getVisitedNodes() {
        return visitedNodes;
    }

    public long getSolveTimeMs() {
        return solveTimeMs;
    }

    public double getStatesPerSecond() {
        return statesPerSecond;
    }
}
