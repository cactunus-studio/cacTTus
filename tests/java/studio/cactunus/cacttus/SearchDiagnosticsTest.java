package studio.cactunus.cacttus;
import android.app.Activity;
public class SearchDiagnosticsTest {
    public static class Nil {public String nilItem="private-query-text";public int textType=12;}
    public static class Response {
        public int status_code=99,error_code=42,itemCount=0;
        public Nil searchNilInfo=new Nil();
        public String query="private-query-text",cookie="private-cookie",status_msg="private-message";
    }
    public static void main(String[] args){
        SearchDiagnostics.record(new Response());SearchDiagnostics.show(new Activity(),"region=kz");
        String log=CrashReport.last;
        if(!log.contains("status_code=99")||!log.contains("error_code=42"))throw new AssertionError("Missing codes");
        if(log.contains("private-"))throw new AssertionError("Unexpected data disclosure");
        if(!log.contains("nil_reason=unknown"))throw new AssertionError("Unvalidated server string");
        Response r=new Response();r.searchNilInfo.nilItem="pack_nil";
        SearchDiagnostics.record(r);SearchDiagnostics.show(new Activity(),"");
        if(!CrashReport.last.contains("nil_reason=pack_nil"))throw new AssertionError("Missing known reason");
        System.out.println("PASS: search metadata is restricted to numeric fields and known reason enums");
    }
}
