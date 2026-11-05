package studio.cactunus.cacttus;

import java.lang.reflect.Field;
import android.app.Activity;

/** Only server status/count metadata. Never stores query, URLs, cookies or results. */
public final class SearchDiagnostics {
    private static volatile String last="Ответ поиска ещё не зафиксирован. Выполни поиск, затем открой диагностику снова.";
    private static volatile String routing="search_priority_region: not observed";
    private static String region(String value){return value!=null&&value.matches("[A-Za-z]{2}")?value:"unknown";}
    public static void routing(String original,String selected){routing="search_priority_region: "+region(original)+" -> "+region(selected);}
    private static Object field(Object object,String name){
        if(object==null)return null;
        try { Field f=object.getClass().getField(name);return f.get(object); }
        catch(ReflectiveOperationException e){return null;}
    }
    private static String number(Object value){return value instanceof Number?value.toString():"unknown";}
    public static void event(Object response,Throwable error){
        if(response!=null)record(response);else last="search_response=null";
        if(error!=null)last+="\nexception_type="+error.getClass().getName();
    }
    public static void record(Object response){
        Object nil=field(response,"searchNilInfo");
        Object reason=field(nil,"nilItem");
        String code="unknown";
        // These are server-defined enums, never arbitrary server/user text.
        String[] known={"hit_limit","hate_speech","hit_core_table","pack_nil","federation_empty"};
        for(String k:known)if(k.equals(reason))code=k;
        last="Search response metadata\nstatus_code="+number(field(response,"status_code"))
            +"\nreturned_items="+(field(response,"mItems") instanceof java.util.List?((java.util.List)field(response,"mItems")).size():-1)
            +"\nresult_status="+number(field(response,"resultStatus"))
            +"\nerror_code="+number(field(response,"error_code"))
            +"\nitem_count="+number(field(response,"itemCount"))
            +"\nnil_reason="+code+"\ntext_type="+number(field(nil,"textType"));
    }
    public static void show(Activity activity,String settings){
        CrashReport.showDiagnostic(activity,last+"\n"+routing+"\n"+settings);
    }
}
