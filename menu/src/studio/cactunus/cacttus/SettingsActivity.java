package studio.cactunus.cacttus;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
public final class SettingsActivity extends Activity {
    public void onCreate(Bundle state){
        boolean dark=(getResources().getConfiguration().uiMode&48)==32;
        int theme=getResources().getIdentifier(dark?"Theme.Material.NoActionBar":"Theme.Material.Light.NoActionBar","style","android");
        setTheme(theme!=0?theme:android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        super.onCreate(state);requestWindowFeature(Window.FEATURE_NO_TITLE);
        Menu.install(this);
        try { Menu.page(this); }
        catch(RuntimeException error){CrashReport.menuError(this,"Settings page",error);}
        catch(LinkageError error){CrashReport.menuError(this,"Settings page",error);}
    }
}
