package studio.cactunus.cacttus;
import java.util.*;
import X.*;
import com.bytedance.retrofit2.mime.TypedOutput;
public class SearchBodyTest {
    public static class Request {
        public String url,method;public Base body;public TypedOutput output;public final String header;
        public Request(String path,String verb,Base b){url=path;method=verb;body=b;header="unchanged-header";}
        public String getUrl(){return url;}
        public String getMethod(){return method;}
        public Base getRequestBody(){return body;}
        public Builder newBuilder(){return new Builder(this);}
    }
    public static class Builder {
        Request original;String url,method;TypedOutput output;
        public Builder(Request r){original=r;url=r.url;method=r.method;output=r.output;}
        public void LIZJ(String value){url=value;}
        public void LIZIZ(TypedOutput value,String verb){output=value;method=verb;}
        public Request LIZ(){Request r=new Request(url,method,original.body);r.output=output;return r;}
    }
    public static void main(String[] args){
        List<String> names=Arrays.asList("keyword","carrier_region","sim_region","nonce","carrier_region","priority_region");
        List<String> values=Arrays.asList("cats%2Bdogs","KZ","kz","a%2Fb%3D","KZ","KZ");
        Frm0 body=new Frm0(names,values);
        Request source=new Request("https://search.tiktokv.com/aweme/v2/search/general/single/?count=20","POST",body);
        Request result=(Request)Entry.searchRequest(source);
        if(result==source||!(result.output instanceof Wrap))throw new AssertionError("POST body was not rebuilt");
        Frm0 changed=(Frm0)((Wrap)result.output).body;
        if(!changed.LIZ.equals(names))throw new AssertionError("Names/order changed");
        if(!changed.LIZIZ.equals(Arrays.asList("cats%2Bdogs","RU","ru","a%2Fb%3D","RU","RU")))throw new AssertionError("Encoded form changed incorrectly");
        if(!body.LIZIZ.equals(values))throw new AssertionError("Original body mutated");
        if(!result.url.equals(source.url)||!result.method.equals(source.method)||!result.header.equals(source.header))throw new AssertionError("Request metadata changed");
        Request feed=new Request("https://api.tiktokv.com/aweme/v1/feed/","POST",body);
        if(Entry.searchRequest(feed)!=feed)throw new AssertionError("Feed request changed");
        System.out.println("PASS: actual reflection bridge rebuilds POST SIM fields, preserves encoded data, duplicates and source body");
    }
}
