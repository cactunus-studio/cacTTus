package studio.cactunus.cacttus;
import java.util.*;
import X.*;
import com.bytedance.retrofit2.mime.TypedOutput;
public class SearchBodyTest {
    public static class Request {
        public String url,method;public Base requestBody;public TypedOutput body;public final String header;
        public Request(String path,String verb,Base b){url=path;method=verb;requestBody=b;header="unchanged-header";}
        public String getUrl(){return url;}
        public String getMethod(){return method;}
        public Base getRequestBody(){return requestBody;}
        public Builder newBuilder(){return new Builder(this);}
    }
    public static class Builder {
        Request original;String url,method;TypedOutput output;public final Base LJ;
        public Builder(Request r){original=r;url=r.url;method=r.method;output=r.body;LJ=r.requestBody;}
        public void LIZJ(String value){url=value;}
        public void LIZIZ(TypedOutput value,String verb){output=value;method=verb;}
        public Request LIZ(){Request r=new Request(url,method,LJ);r.body=output;return r;}
    }
    public static void main(String[] args){
        List<String> names=Arrays.asList("keyword","carrier_region","sim_region","nonce","carrier_region","priority_region");
        List<String> values=Arrays.asList("cats%2Bdogs","KZ","kz","a%2Fb%3D","KZ","KZ");
        Frm0 body=new Frm0(names,values);
        Request source=new Request("https://search.tiktokv.com/aweme/v2/search/general/single/?count=20","POST",body);
        List<String> mergedNames=new ArrayList<String>(names);mergedNames.add("send_request_timestamp");
        List<String> mergedValues=new ArrayList<String>(values);mergedValues.add("123456");
        source.body=new Wrap(new Frm0(mergedNames,mergedValues));
        Request result=(Request)Entry.searchRequest(source);
        if(result==source||!(result.body instanceof Wrap))throw new AssertionError("POST body was not rebuilt");
        Frm0 changed=(Frm0)((Wrap)result.body).LIZ;
        if(!changed.LIZ.equals(mergedNames))throw new AssertionError("Names/order changed");
        if(!changed.LIZIZ.equals(Arrays.asList("cats%2Bdogs","RU","ru","a%2Fb%3D","RU","RU","123456")))throw new AssertionError("Encoded form changed incorrectly");
        if(result.getRequestBody()!=changed)throw new AssertionError("SDK would prefer stale body");
        if(!body.LIZIZ.equals(values))throw new AssertionError("Original body mutated");
        if(!result.url.equals(source.url)||!result.method.equals(source.method)||!result.header.equals(source.header))throw new AssertionError("Request metadata changed");
        Request feed=new Request("https://api.tiktokv.com/aweme/v1/feed/","POST",body);
        if(Entry.searchRequest(feed)!=feed)throw new AssertionError("Feed request changed");
        System.out.println("PASS: actual reflection bridge rebuilds POST SIM fields, preserves encoded data, duplicates and source body");
    }
}
