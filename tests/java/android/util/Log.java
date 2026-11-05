package android.util;
public class Log {
    public static int failures;
    public static int e(String tag, String message, Throwable cause) { failures++; return 0; }
}
