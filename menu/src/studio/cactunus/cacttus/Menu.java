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
    public static String simCountry(String original) { return country(original); }
    public static String networkCountry(String original) {
        if (app == null || !prefs().getBoolean("network_override", false)) return original;
        return country(original);
    }
    public static void install(Activity activity) { app = activity.getApplication(); }
    public static void settings(final Activity activity) {
        install(activity);
        activity.getWindow().getDecorView().post(new Runnable() {
            public void run() { attach(activity); }
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
        TextView button = new TextView(a); button.setTag("cacttus-menu");
        button.setText("cacTTus  ·  Регион и настройки  ›");
        button.setTextColor(0xff20331b); button.setGravity(Gravity.CENTER_VERTICAL);
        button.setPadding(dp(a,20),0,dp(a,20),0); button.setTextSize(16);
        button.setContentDescription("Открыть настройки cacTTus");
        button.setBackgroundDrawable(shape(systemColor(a,"system_accent1_100",0xffd9e8c5),a));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(-1,dp(a,64),Gravity.BOTTOM);
        params.setMargins(dp(a,12),0,dp(a,12),dp(a,12));
        // Reserve space below the settings content for the entry row.
        ViewGroup container=(ViewGroup)root;
        for(int i=0;i<container.getChildCount();i++) {
            View child=container.getChildAt(i);
            ViewGroup.LayoutParams lp=child.getLayoutParams();
            if(lp instanceof FrameLayout.LayoutParams) {
                FrameLayout.LayoutParams fl=(FrameLayout.LayoutParams)lp;
                fl.bottomMargin += dp(a,88); child.setLayoutParams(fl);
            }
        }
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
        final Switch network = new Switch(ui); network.setText("Подменять страну сети");
        network.setTextColor(ink); network.setChecked(prefs().getBoolean("network_override",false));
        panel.addView(network);
        panel.addView(label(ui,"Регион",18,ink));
        final Spinner regions = new Spinner(ui);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ui,android.R.layout.simple_spinner_item,NAMES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); regions.setAdapter(adapter);
        String selected=prefs().getString("region","kz");
        for(int i=0;i<CODES.length;i++) if(CODES[i].equals(selected)) regions.setSelection(i);
        panel.addView(regions,new LinearLayout.LayoutParams(-1,dp(a,56)));
        panel.addView(label(ui,"Страна сети по умолчанию не меняется. Если поиск не работает, отключи подмену страны и перезапусти TikTok. Настройки не меняют IP-адрес.",14,secondary));
        Button save=new Button(ui); save.setText("Сохранить"); save.setTextColor(ink);
        save.setBackgroundDrawable(shape(accent,a)); panel.addView(save,new LinearLayout.LayoutParams(-1,dp(a,52)));
        save.setOnClickListener(new View.OnClickListener(){public void onClick(View v){
            prefs().edit().putBoolean("network_override",network.isChecked()).putBoolean("override",enabled.isChecked()).putString("region",CODES[regions.getSelectedItemPosition()]).apply();
            Toast.makeText(a,"Сохранено. Перезапусти TikTok",Toast.LENGTH_LONG).show(); dialog.dismiss();
        }});
        ScrollView scroll = new ScrollView(ui); scroll.addView(panel); dialog.setContentView(scroll); dialog.show();
        if(dialog.getWindow()!=null) { dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); dialog.getWindow().setLayout(Math.min(dp(a,380),a.getResources().getDisplayMetrics().widthPixels-dp(a,32)),-2); }
    }
}
