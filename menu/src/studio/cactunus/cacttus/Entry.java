package studio.cactunus.cacttus;

import android.app.Activity;
import android.util.Log;

/** Small boundary: a menu/resource/verification failure must not close TikTok. */
public final class Entry {
    private Entry() {}
    public static void install(Activity activity) {
        try { CrashReport.install(activity); ScreenCare.install(activity); Menu.install(activity); }
        catch (RuntimeException error) { report(error); }
        catch (LinkageError error) { report(error); }
    }
    public static void settings(Activity activity) {
        try { CrashReport.install(activity); ScreenCare.install(activity); Menu.settings(activity); }
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
    public static void videoSearchBuilder(Object builder) {
        try { SearchRouting.configure(builder); }
        catch (ReflectiveOperationException error) { report(error); }
        catch (RuntimeException error) { report(error); }
        catch (LinkageError error) { report(error); }
    }
    public static String searchRegion(Object request,String original) {
        try {
            String url=(String)request.getClass().getMethod("getUrl").invoke(request);
            String selected=SearchRouting.choose(url,original,Menu.country(original));
            if(SearchRouting.isSearch(url))SearchDiagnostics.routing(original,selected);
            return selected;
        } catch (ReflectiveOperationException error) { report(error);return original; }
        catch (RuntimeException error) { report(error);return original; }
        catch (LinkageError error) { report(error);return original; }
    }
    public static void searchEvent(Object response,Throwable error) {
        try { SearchDiagnostics.event(response,error); }
        catch (RuntimeException unavailable) { report(unavailable); }
        catch (LinkageError unavailable) { report(unavailable); }
    }
    public static void searchResult(Object response) {
        try { SearchDiagnostics.record(response); }
        catch (RuntimeException error) { report(error); }
        catch (LinkageError error) { report(error); }
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
