package studio.cactunus.cacttus;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.ScrollView;
import java.io.PrintWriter;
import java.io.StringWriter;

/** Local-only diagnostics. Fatal errors keep the application's original crash handler. */
public final class CrashReport {
    private static Application application;
    private static boolean visible;
    private static final String PREFS = "cacttus_diagnostics";
    private CrashReport() {}

    public static synchronized void install(final Activity activity) {
        if (application != null) return;
        application = activity.getApplication();
        final Thread.UncaughtExceptionHandler previous = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread thread, Throwable error) {
                try {
                    application.getSharedPreferences(PREFS,0).edit()
                        .putString("pending",format("Uncaught on " + thread.getName(),error)).commit();
                } catch (Throwable ignored) { /* Never replace the underlying crash. */ }
                if (previous != null) previous.uncaughtException(thread,error);
                else { android.os.Process.killProcess(android.os.Process.myPid()); System.exit(10); }
            }
        });
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            public void onActivityResumed(final Activity a) {
                a.getWindow().getDecorView().postDelayed(new Runnable() {
                    public void run() { pending(a); }
                },500);
            }
            public void onActivityCreated(Activity a,Bundle b) {}
            public void onActivityStarted(Activity a) {}
            public void onActivityPaused(Activity a) {}
            public void onActivityStopped(Activity a) {}
            public void onActivitySaveInstanceState(Activity a,Bundle b) {}
            public void onActivityDestroyed(Activity a) {}
        });
    }
    private static String format(String stage, Throwable error) {
        StringWriter text = new StringWriter();
        text.append("cacTTus diagnostic\nAndroid ").append(android.os.Build.VERSION.RELEASE)
            .append(" / API ").append(String.valueOf(android.os.Build.VERSION.SDK_INT))
            .append("\n").append(stage).append("\n");
        error.printStackTrace(new PrintWriter(text));
        String value=text.toString();
        return value.length()>48000 ? value.substring(0,48000)+"\n[truncated]" : value;
    }
    public static void menuError(final Activity activity,String stage,Throwable error) {
        try {
            final String report = format(stage,error);
            activity.runOnUiThread(new Runnable(){public void run(){show(activity,report,false);}});
        } catch (RuntimeException unavailable) { Log.e("cacTTus","Cannot schedule diagnostic",unavailable); }
        catch (LinkageError unavailable) { Log.e("cacTTus","Cannot load diagnostic",unavailable); }
    }
    private static void pending(Activity activity) {
        try {
            if (application == null || activity.isFinishing() || visible) return;
            String report=application.getSharedPreferences(PREFS,0).getString("pending",null);
            if(report!=null) show(activity,report,true);
        } catch (RuntimeException error) { Log.e("cacTTus","Cannot show previous crash",error); }
        catch (LinkageError error) { Log.e("cacTTus","Cannot load crash viewer",error); }
    }
    private static void show(final Activity activity,final String report,final boolean saved) {
        if (visible || activity.isFinishing()) return;
        try {
            TextView text=new TextView(activity); text.setText(report); text.setTextSize(12);
            text.setTextIsSelectable(true); text.setPadding(24,16,24,16);
            ScrollView scroll=new ScrollView(activity); scroll.addView(text);
            AlertDialog dialog=new AlertDialog.Builder(activity)
                .setTitle(saved ? "Журнал предыдущего вылета" : "Ошибка меню cacTTus")
                .setView(scroll).setPositiveButton("Скопировать лог",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface d,int which){
                        try {
                            android.content.ClipboardManager clipboard=(android.content.ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
                            if(clipboard!=null) clipboard.setPrimaryClip(ClipData.newPlainText("cacTTus log",report));
                        } catch (RuntimeException error) { Log.e("cacTTus","Cannot copy diagnostic",error); }
                    }
                }).setNegativeButton("Закрыть",null).create();
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){public void onDismiss(DialogInterface d){
                visible=false;
                if(saved && application!=null) application.getSharedPreferences(PREFS,0).edit().remove("pending").apply();
            }});
            dialog.show(); visible=true;
        } catch (RuntimeException error) { Log.e("cacTTus","Cannot display diagnostic",error); }
        catch (LinkageError error) { Log.e("cacTTus","Cannot load diagnostic",error); }
    }
}
