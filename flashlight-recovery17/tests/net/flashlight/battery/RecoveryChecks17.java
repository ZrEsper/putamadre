package net.flashlight.battery;
public class RecoveryChecks17{
 public static void main(String[]args){Recovery17 r=new Recovery17();if(!r.ready(0))throw new AssertionError();if(!r.failure(1)||r.ready(2))throw new AssertionError("backoff");if(r.failure(2))throw new AssertionError("logs must be rate limited");if(!r.ready(1_000_000_002L))throw new AssertionError("retry must recover");r.success();if(!r.ready(3)||r.failures!=0)throw new AssertionError("success resets retry");for(int i=0;i<100;i++)r.failure(20_000_000_000L);if(r.after>21_000_000_000L)throw new AssertionError("backoff bounded");System.out.println("TRANSIENT_LIGHT_UPDATE_RECOVERY_CHECKS=6");}
}
