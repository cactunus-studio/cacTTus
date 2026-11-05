package studio.cactunus.cacttus;
import android.app.Activity;
public class Menu {
    public static String deviceCountry(){return "ru";}
    public static String country(String original){return "kz";}
    public static void install(Activity a) { throw new IllegalStateException("broken UI context"); }
    public static void settings(Activity a) { throw new NoClassDefFoundError("unavailable UI class"); }
    public static String simCountry(String original) { throw new VerifyError("verification failure"); }
    public static String networkCountry(String original) { throw new IllegalStateException("broken preferences"); }
}
