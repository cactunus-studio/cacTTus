package studio.cactunus.cacttus;
import android.app.Activity;
import android.util.Log;
public class EntryTest {
    public static void main(String[] args) {
        Entry.install(new Activity());
        Entry.settings(new Activity());
        if (!"ru".equals(Entry.simCountry("ru"))) throw new AssertionError("SIM fallback");
        if (!"de".equals(Entry.networkCountry("de"))) throw new AssertionError("Network fallback");
        if (Log.failures != (args.length == 0 ? 4 : 5)) throw new AssertionError("Failures must be reported");
        System.out.println("PASS: runtime/linkage failures are contained and original countries preserved");
    }
}
