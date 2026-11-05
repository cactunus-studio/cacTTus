package studio.cactunus.cacttus;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public final class Menu {
    private static volatile Application app;
    private static boolean installed;
    private static final String[] CODES = {"kz", "us", "de", "tr", "gb", "jp"};
    private static final String[] NAMES = {"Казахстан", "США", "Германия", "Турция", "Великобритания", "Япония"};
    private static SharedPreferences prefs() { return app.getSharedPreferences("cacttus", 0); }
    public static String country(String original) {
        if (app == null) return "kz";
        if (!prefs().getBoolean("override", true)) return original;
        String code = prefs().getString("region", "kz");
        for (String c : CODES) if (c.equals(code)) return code;
        return "kz";
    }
    public static void install(Activity activity) {
        if (installed) return;
        installed = true;
        app = activity.getApplication();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityResumed(final Activity a) {
                if (a.getClass().getName().contains("SplashActivity")) return;
                a.getWindow().getDecorView().post(new Runnable() { public void run() { attach(a); } });
            }
            public void onActivityCreated(Activity a, Bundle b) {}
            public void onActivityStarted(Activity a) {}
            public void onActivityPaused(Activity a) {}
            public void onActivityStopped(Activity a) {}
            public void onActivitySaveInstanceState(Activity a, Bundle b) {}
            public void onActivityDestroyed(Activity a) {}
        });
    }
    private static int dp(Context c, int v) { return (int)(v*c.getResources().getDisplayMetrics().density+0.5f); }
    private static GradientDrawable shape(int color, Context c) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(dp(c,24)); return d;
    }
    private static void attach(final Activity a) {
        if (a.isFinishing()) return;
        View root = a.findViewById(android.R.id.content);
        if (!(root instanceof android.view.ViewGroup) || root.findViewWithTag("cacttus-menu") != null) return;
        TextView button = new TextView(a); button.setTag("cacttus-menu"); button.setText("🌵");
        button.setGravity(Gravity.CENTER); button.setTextSize(22);
        button.setContentDescription("Настройки cacTTus"); button.setBackgroundDrawable(shape(0xffd9e8c5,a));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(a,48), dp(a,48), Gravity.TOP|Gravity.RIGHT);
        params.setMargins(0,dp(a,48),dp(a,12),0);
        a.addContentView(button,params);
        button.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { show(a); } });
    }
    private static TextView label(Context c, String text, int size, int color) {
        TextView v = new TextView(c); v.setText(text); v.setTextSize(size); v.setTextColor(color);
        v.setPadding(0,dp(c,8),0,dp(c,8)); return v;
    }
    private static int systemColor(Context c, String name, int fallback) {
        int id = c.getResources().getIdentifier(name, "color", "android");
        return id == 0 ? fallback : c.getResources().getColor(id);
    }
    private static void show(final Activity a) {
        boolean dark = (a.getResources().getConfiguration().uiMode & 48) == 32;
        final Context ui = new ContextThemeWrapper(a, dark ? android.R.style.Theme_Holo : android.R.style.Theme_Holo_Light);
        int surface = systemColor(a, dark ? "system_neutral1_900" : "system_neutral1_10", dark ? 0xff1d211a : 0xfff4f7ed);
        int accent = systemColor(a, dark ? "system_accent1_700" : "system_accent1_100", dark ? 0xff384b2e : 0xffd9e8c5);
        int ink = dark ? 0xffe2ead9 : 0xff20331b;
        int secondary = dark ? 0xffbac5b0 : 0xff586450;
        final Dialog dialog = new Dialog(ui);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout panel = new LinearLayout(ui); panel.setOrientation(1);
        panel.setPadding(dp(a,24),dp(a,20),dp(a,24),dp(a,20));
        panel.setBackgroundDrawable(shape(surface,a));
        TextView title=label(ui,"cacTTus 🌵",28,ink); title.setTypeface(null,Typeface.BOLD); panel.addView(title);
        panel.addView(label(ui,"Сделай ленту своей",15,secondary));
        final Switch enabled = new Switch(ui); enabled.setText("Подмена страны"); enabled.setTextColor(ink);
        enabled.setChecked(prefs().getBoolean("override",true)); panel.addView(enabled);
        panel.addView(label(ui,"Регион",18,ink));
        final Spinner regions = new Spinner(ui);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ui,android.R.layout.simple_spinner_item,NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); regions.setAdapter(adapter);
        String selected=prefs().getString("region","kz");
        for(int i=0;i<CODES.length;i++) if(CODES[i].equals(selected)) regions.setSelection(i);
        panel.addView(regions,new LinearLayout.LayoutParams(-1,dp(a,56)));
        panel.addView(label(ui,"Казахстан выбран по умолчанию. После изменения перезапусти TikTok. Регион сети и аккаунта может влиять на результат.",14,secondary));
        Button save=new Button(ui); save.setText("Сохранить"); save.setTextColor(ink);
        save.setBackgroundDrawable(shape(accent,a)); panel.addView(save,new LinearLayout.LayoutParams(-1,dp(a,52)));
        save.setOnClickListener(new View.OnClickListener(){public void onClick(View v){
            prefs().edit().putBoolean("override",enabled.isChecked()).putString("region",CODES[regions.getSelectedItemPosition()]).apply();
            Toast.makeText(a,"Сохранено. Перезапусти TikTok",Toast.LENGTH_LONG).show(); dialog.dismiss();
        }});
        dialog.setContentView(panel); dialog.show();
        if(dialog.getWindow()!=null) { dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); dialog.getWindow().setLayout(Math.min(dp(a,380),a.getResources().getDisplayMetrics().widthPixels-dp(a,32)),-2); }
    }
}
