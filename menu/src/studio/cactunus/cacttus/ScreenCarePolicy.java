package studio.cactunus.cacttus;

/** Pure timing/offset policy, in physical pixels and foreground milliseconds. */
public final class ScreenCarePolicy {
    public static final long INTERVAL_MS = 60000L;
    public static final long BREAK_MS = 20L * 60000L;
    private static final int[][] OFFSETS = {{-2,0},{0,-2},{2,0},{0,2}};
    private ScreenCarePolicy() {}
    public static int x(int step) { return OFFSETS[(step & Integer.MAX_VALUE)%4][0]; }
    public static int y(int step) { return OFFSETS[(step & Integer.MAX_VALUE)%4][1]; }
    public static boolean breakDue(long foregroundMs) { return foregroundMs >= BREAK_MS; }
}
