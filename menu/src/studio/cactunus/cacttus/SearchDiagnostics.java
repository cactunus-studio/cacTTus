package studio.cactunus.cacttus;

import java.lang.reflect.Field;
import android.app.Activity;

/** Only server status/count metadata. Never stores query, URLs, cookies or results. */
public final class SearchDiagnostics {
    private static volatile String last="Ответ поиска ещё не зафиксирован. Выполни поиск, затем открой диагностику снова.";
    private static Object field(Object object,String name){
        if(object==null)return null;
        try { Field f=object.getClass().getField(name);return f.get(object); }
        catch(ReflectiveOperationException e){return null;}
    }
    private static String number(Object value){return value instanceof Number?value.toString():"unknown";}
    public static void record(Object response){
        Object nil=field(response,"searchNilInfo");
        Object reason=field(nil,"nilItem");
        String code="unknown";
        // These are server-defined enums, never arbitrary server/user text.
        String[] known={"hit_limit","hate_speech","hit_core_table","pack_nil","federation_empty"};
        for(String k:known)if(k.equals(reason))code=k;
        last="Search response metadata\nstatus_code="+number(field(response,"status_code"))
            +"\nerror_code="+number(field(response,"error_code"))
            +"\nitem_count="+number(field(response,"itemCount"))
            +"\nnil_reason="+code+"\ntext_type="+number(field(nil,"textType"));
    }
    public static void show(Activity activity,String settings){
        CrashReport.showDiagnostic(activity,last+"\n"+settings);
    }
}
