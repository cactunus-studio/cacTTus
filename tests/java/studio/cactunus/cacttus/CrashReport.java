package studio.cactunus.cacttus;
import android.app.Activity;
public class CrashReport {
    public static String last;
    public static void showDiagnostic(Activity activity,String text){last=text;}
    public static void install(Activity activity) {}
    public static void menuError(Activity activity,String stage,Throwable error) {}
}
