package studio.cactunus.cacttus;
public class SearchRoutingTest {
    private static void expect(String url,String old,String selected,String wanted){
        String actual=SearchRouting.choose(url,old,selected);
        if(!wanted.equals(actual))throw new AssertionError(url+": "+actual);
    }
    public static void main(String[] args){
        expect("https://search.tiktokv.com/aweme/v1/general/search/single/?keyword=anything","RU","kz","KZ");
        expect("https://search.tiktokv.com/aweme/v1/discover/search/","ru","us","us");
        expect("https://api.tiktokv.com/aweme/v1/feed/","RU","kz","RU");
        expect("https://api.tiktokv.com/passport/auth/login","RU","kz","RU");
        expect("https://search.tiktokv.com/other?path=/aweme/v1/search/","RU","kz","RU");
        expect("https://search.tiktokv.com.evil.test/aweme/v1/search/","RU","kz","RU");
        expect("http://search.tiktokv.com/aweme/v1/search/","RU","kz","RU");
        expect("https://search.tiktokv.com/aweme/v1/search/","RU","invalid","RU");
        expect("https://search.tiktokv.com/aweme/v1/search/","RU","RU","RU");
        System.out.println("PASS: search-only region consistency; feed/login/unrelated hosts unchanged");
    }
}
