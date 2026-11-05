package studio.cactunus.cacttus;

import android.app.*;
import android.content.SharedPreferences;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import java.lang.ref.WeakReference;

/** Opt-in protection. No background service, overlay permission or network access. */
public final class ScreenCare {
    private static Application app;
    private static final Handler handler=new Handler(Looper.getMainLooper());
    private static WeakReference<Activity> active=new WeakReference<Activity>(null);
    private static View shifted;
    private static float baseX,baseY;
    private static int step;
    private static long foregroundMs,lastTick;
    private static SharedPreferences prefs(){return app.getSharedPreferences("cacttus",0);}
    public static synchronized void install(Activity activity){
        if(app!=null)return;
        app=activity.getApplication();
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks(){
            public void onActivityResumed(Activity a){
                stop(); active=new WeakReference<Activity>(a); lastTick=SystemClock.elapsedRealtime();
                handler.postDelayed(tick,ScreenCarePolicy.INTERVAL_MS);
            }
            public void onActivityPaused(Activity a){if(active.get()==a)stop();}
            public void onActivityDestroyed(Activity a){if(active.get()==a)stop();}
            public void onActivityCreated(Activity a,Bundle b){}
            public void onActivityStarted(Activity a){}
            public void onActivityStopped(Activity a){}
            public void onActivitySaveInstanceState(Activity a,Bundle b){}
        });
    }
    private static void accumulate(){
        if(lastTick!=0){long now=SystemClock.elapsedRealtime(); foregroundMs+=Math.max(0,now-lastTick); lastTick=now;}
    }
    private static void restore(){
        if(shifted!=null){shifted.setTranslationX(baseX); shifted.setTranslationY(baseY); shifted=null;}
    }
    private static void stop(){
        handler.removeCallbacks(tick); accumulate(); lastTick=0;
        try{restore();}catch(RuntimeException e){Log.e("cacTTus","Screen care reset",e);}
        active.clear();
    }
    public static void refresh(){
        if(app==null)return;
        if(!prefs().getBoolean("pixel_shift",false))restore();
        if(!prefs().getBoolean("break_reminder",false))foregroundMs=0;
    }
    private static final Runnable tick=new Runnable(){public void run(){
        Activity activity=active.get();
        if(activity==null||activity.isFinishing()){stop();return;}
        try{
            accumulate();
            if(prefs().getBoolean("pixel_shift",false)){
                View root=activity.findViewById(android.R.id.content);
                if(root!=null){
                    if(shifted!=root){restore();shifted=root;baseX=root.getTranslationX();baseY=root.getTranslationY();}
                    root.setTranslationX(baseX+ScreenCarePolicy.x(step));
                    root.setTranslationY(baseY+ScreenCarePolicy.y(step));step++;
                }
            }else restore();
            if(!prefs().getBoolean("break_reminder",false))foregroundMs=0;
            else if(ScreenCarePolicy.breakDue(foregroundMs)){
                Toast.makeText(activity,"20 минут просмотра. Пора сделать перерыв",Toast.LENGTH_LONG).show();foregroundMs=0;
            }
        }catch(RuntimeException error){Log.e("cacTTus","Screen care failed",error);}
        catch(LinkageError error){Log.e("cacTTus","Screen care unavailable",error);}
        handler.postDelayed(this,ScreenCarePolicy.INTERVAL_MS);
    }};
}
