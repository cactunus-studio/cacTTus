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
    public static Object searchRequest(Object request){
        if(request==null)return null;
        try {
            String url=(String)request.getClass().getMethod("getUrl").invoke(request);
            if(!SearchRouting.isSearch(url))return request;
            String device=Menu.deviceCountry();
            String changed=SearchRouting.restoreSim(url,device);
            Object newBody=null;
            String method=(String)request.getClass().getMethod("getMethod").invoke(request);
            if("POST".equals(method)){
                Object body=request.getClass().getMethod("getRequestBody").invoke(request);
                Object transport=request.getClass().getField("body").get(request);
                if(transport!=null){
                    if(!transport.getClass().getName().equals("X.0OxD"))return request;
                    body=transport.getClass().getField("LIZ").get(transport);
                }
                if(body!=null&&body.getClass().getName().equals("X.07Hx")){
                    java.util.List<String> names=(java.util.List<String>)body.getClass().getField("LIZ").get(body);
                    java.util.List<String> values=(java.util.List<String>)body.getClass().getField("LIZIZ").get(body);
                    java.util.ArrayList<String> restored=new java.util.ArrayList<String>(values);
                    boolean modified=false;
                    for(int i=0;i<names.size();i++){
                        String value=SearchRouting.restoreField(names.get(i),values.get(i),device);
                        if(!value.equals(values.get(i))){restored.set(i,value);modified=true;}
                    }
                    if(modified)newBody=body.getClass().getConstructor(java.util.List.class,java.util.List.class)
                        .newInstance(new java.util.ArrayList<String>(names),restored);
                }
            }
            if(url.equals(changed)&&newBody==null)return request;
            Object builder=request.getClass().getMethod("newBuilder").invoke(request);
            if(!url.equals(changed))builder.getClass().getMethod("LIZJ",String.class).invoke(builder,changed);
            if(newBody!=null){
                ClassLoader loader=builder.getClass().getClassLoader();
                Class<?> bodyContract=Class.forName("X.05mg",false,loader);
                Object output=Class.forName("X.0OxD",true,loader).getConstructor(bodyContract).newInstance(newBody);
                Class<?> outputContract=Class.forName("com.bytedance.retrofit2.mime.TypedOutput",false,loader);
                builder.getClass().getMethod("LIZIZ",outputContract,String.class).invoke(builder,output,method);
                java.lang.reflect.Field metadata=builder.getClass().getField("LJ");
                metadata.setAccessible(true);metadata.set(builder,newBody);
            }
            return builder.getClass().getMethod("LIZ").invoke(builder);
        }catch(ReflectiveOperationException error){report(error);return request;}
        catch(RuntimeException error){report(error);return request;}
        catch(LinkageError error){report(error);return request;}
    }
    public static String searchRegion(Object request,String original) {
        try {
            String url=(String)request.getClass().getMethod("getUrl").invoke(request);
            String selected=SearchRouting.choose(url,original,Menu.deviceCountry());
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
