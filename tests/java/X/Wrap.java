package X;
import com.bytedance.retrofit2.mime.TypedOutput;
public class Wrap implements TypedOutput {
    public final Base body;
    public Wrap(Base body){this.body=body;}
}
