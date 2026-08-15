package algorithm;

/**
 * Central manager for JVM-wide generation ownership and status.
 *
 * Lifecycle:
 *   start()  — atomically claims the generation slot (only if not already running)
 *   finish() — releases the slot, sets running=false
 *   getPercentage() — delegates to the existing CSPSolver static field
 *
 * The lock is ONLY used to check-and-claim ownership.
 * Long-running work happens in a background thread outside the lock.
 */
public final class GenerationManager {

    private static final Object GENERATION_LOCK = new Object();

    /**
     * Whether a generation is currently active.
     * volatile so that reads in other threads see the latest write.
     */
    private static volatile boolean running = false;

    private GenerationManager() {
        // utility class
    }

    /**
     * Attempt to claim generation ownership.
     * @return true if this call started a generation; false if one is already running.
     */
    public static boolean start() {
        synchronized (GENERATION_LOCK) {
            if (running) {
                return false;
            }
            running = true;
            return true;
        }
    }

    /**
     * Release generation ownership (must be called in finally).
     */
    public static void finish() {
        synchronized (GENERATION_LOCK) {
            running = false;
        }
    }

    public static boolean isRunning() {
        return running;
    }

    /**
     * Delegates to the existing CSPSolver percentage field.
     * No CSP algorithm changes.
     */
    public static int getPercentage() {
        return CSPSolver.routineGenerationPercentage;
    }

    /**
     * @return the shared lock object (for callers that need to synchronize externally).
     */
    public static Object getLock() {
        return GENERATION_LOCK;
    }
}
