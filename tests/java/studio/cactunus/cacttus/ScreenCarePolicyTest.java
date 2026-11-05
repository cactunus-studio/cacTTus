package studio.cactunus.cacttus;
public class ScreenCarePolicyTest {
    public static void main(String[] args){
        for(int i=-8;i<16;i++){
            int x=ScreenCarePolicy.x(i),y=ScreenCarePolicy.y(i);
            if(Math.abs(x)>2||Math.abs(y)>2)throw new AssertionError("Shift exceeds safe bound");
            if(x==0&&y==0)throw new AssertionError("No movement");
            if(x!=ScreenCarePolicy.x(i+4)||y!=ScreenCarePolicy.y(i+4))throw new AssertionError("Invalid cycle");
        }
        if(ScreenCarePolicy.breakDue(1199999L))throw new AssertionError("Early reminder");
        if(!ScreenCarePolicy.breakDue(1200000L))throw new AssertionError("Missing reminder");
        System.out.println("PASS: bounded pixel shifts and 20-minute reminder boundary");
    }
}
