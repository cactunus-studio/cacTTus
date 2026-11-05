package studio.cactunus.cacttus;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
/** Limit regional consistency changes to first-party search API paths. */
public final class SearchRouting {
    private SearchRouting() {}
    public static boolean isSearch(String url){
        try{
            URI uri=new URI(url);
            if(!"https".equalsIgnoreCase(uri.getScheme()))return false;
            String host=uri.getHost(),path=uri.getPath();
            if(host==null||path==null)return false;
            host=host.toLowerCase(Locale.US);
            if(!(host.equals("tiktokv.com")||host.endsWith(".tiktokv.com")||host.equals("tiktok.com")||host.endsWith(".tiktok.com")))return false;
            return path.startsWith("/aweme/v1/general/search/")||path.startsWith("/aweme/v1/search/")
                ||path.startsWith("/aweme/v1/discover/search/")||path.startsWith("/aweme/v1/user/search/")
                ||path.startsWith("/aweme/v1/challenge/search/")||path.startsWith("/aweme/v1/music/search/")
                ||path.startsWith("/aweme/v1/live/search/");
        }catch(URISyntaxException e){return false;}
        catch(NullPointerException e){return false;}
    }
    public static String choose(String url,String original,String selected){
        if(!isSearch(url)||selected==null||!selected.matches("[A-Za-z]{2}"))return original;
        return original!=null&&original.matches("[A-Z]{2}")?selected.toUpperCase(Locale.US):selected.toLowerCase(Locale.US);
    }
}
