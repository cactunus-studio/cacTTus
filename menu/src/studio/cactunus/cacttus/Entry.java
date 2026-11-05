package studio.cactunus.cacttus;

import android.app.Activity;
import android.util.Log;

/** Small boundary: a menu/resource/verification failure must not close TikTok. */
public final class Entry {
    private Entry() {}
    public static void install(Activity activity) {
        try { CrashReport.install(activity); Menu.install(activity); }
        catch (RuntimeException error) { report(error); }
        catch (LinkageError error) { report(error); }
    }
    public static void settings(Activity activity) {
        try { CrashReport.install(activity); Menu.settings(activity); }
        catch (RuntimeException error) { menuReport(activity,error); }
        catch (LinkageError error) { menuReport(activity,error); }
    }
    public static String simCountry(String original) {
        try { return Menu.simCountry(original); }
        catch (RuntimeException error) { report(error); return original; }
        catch (LinkageError error) { report(error); return original; }
    }
    public static String networkCountry(String original) {
        try { return Menu.networkCountry(original); }
        catch (RuntimeException error) { report(error); return original; }
        catch (LinkageError error) { report(error); return original; }
    }
    private static void menuReport(Activity activity,Throwable error) {
        report(error);
        try { CrashReport.menuError(activity,"Settings entry",error); }
        catch (RuntimeException unavailable) { report(unavailable); }
        catch (LinkageError unavailable) { report(unavailable); }
    }
    private static void report(Throwable error) {
        Log.e("cacTTus", "Optional mod hook failed; using original behavior", error);
    }
}
