package studio.cactunus.cacttus;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
/** Limit regional consistency changes to first-party search API paths. */
public final class SearchRouting {
    private SearchRouting() {}
    public static void configure(Object builder) throws ReflectiveOperationException {
        final String name="com.ss.android.ugc.aweme.foundation.network.interceptor.CommonParamInterceptor";
        Object current=builder.getClass().getField("LIZLLL").get(builder);
        if(current instanceof java.util.List) for(Object interceptor:(java.util.List)current)
            if(interceptor!=null&&interceptor.getClass().getName().equals(name))return;
        ClassLoader loader=builder.getClass().getClassLoader();
        Class<?> type=Class.forName(name,true,loader);
        Class<?> contract=Class.forName("X.02xb",false,loader);
        builder.getClass().getMethod("LIZ",contract).invoke(builder,type.getConstructor().newInstance());
    }
    public static boolean isSearch(String url){
        try{
            URI uri=new URI(url);
            if(!"https".equalsIgnoreCase(uri.getScheme()))return false;
            String host=uri.getHost(),path=uri.getPath();
            if(host==null||path==null)return false;
            host=host.toLowerCase(Locale.US);
            if(!(host.equals("tiktokv.com")||host.endsWith(".tiktokv.com")||host.equals("tiktok.com")||host.endsWith(".tiktok.com")))return false;
            return path.startsWith("/aweme/v2/search/general/")||path.startsWith("/aweme/v1/general/search/")||path.startsWith("/aweme/v1/search/")
                ||path.startsWith("/aweme/v1/discover/search/")||path.startsWith("/aweme/v1/user/search/")
                ||path.startsWith("/aweme/v1/challenge/search/")||path.startsWith("/aweme/v1/music/search/")
                ||path.startsWith("/aweme/v1/live/search/");
        }catch(URISyntaxException e){return false;}
        catch(NullPointerException e){return false;}
    }
    public static String restoreField(String encodedKey,String encodedValue,String device){
        if(device==null||!device.matches("(?:[A-Za-z]{2})?"))return encodedValue;
        String key;
        try{key=java.net.URLDecoder.decode(encodedKey,"UTF-8");}
        catch(java.io.UnsupportedEncodingException e){return encodedValue;}
        catch(IllegalArgumentException e){return encodedValue;}
        if(!key.equals("carrier_region")&&!key.equals("sim_region")&&!key.equals("carrier_region1")&&!key.equals("priority_region"))return encodedValue;
        return encodedValue.matches("[A-Z]{2}")?device.toUpperCase(Locale.US):device.toLowerCase(Locale.US);
    }
    /** Keep all original raw query bytes/order except already-present SIM country values. */
    public static String restoreSim(String url,String device){
        if(!isSearch(url)||device==null||!device.matches("(?:[A-Za-z]{2})?"))return url;
        int query=url.indexOf('?');if(query<0)return url;
        int fragment=url.indexOf('#',query);if(fragment<0)fragment=url.length();
        String[] parts=url.substring(query+1,fragment).split("&",-1);
        boolean changed=false;
        for(int i=0;i<parts.length;i++){
            int equals=parts[i].indexOf('=');if(equals<0)continue;
            String old=parts[i].substring(equals+1);
            String replacement=restoreField(parts[i].substring(0,equals),old,device);
            parts[i]=parts[i].substring(0,equals+1)+replacement;changed|=!old.equals(replacement);
        }
        if(!changed)return url;
        StringBuilder result=new StringBuilder(url.substring(0,query+1));
        for(int i=0;i<parts.length;i++){if(i>0)result.append('&');result.append(parts[i]);}
        return result.append(url.substring(fragment)).toString();
    }
    public static String choose(String url,String original,String selected){
        if(!isSearch(url)||selected==null||!selected.matches("[A-Za-z]{2}"))return original;
        return original!=null&&original.matches("[A-Z]{2}")?selected.toUpperCase(Locale.US):selected.toLowerCase(Locale.US);
    }
}
