package net.flashlight.battery;
public final class Recovery17 {
 static final Recovery17 UPDATE=new Recovery17();long after,report;int failures;
 public boolean ready(long now){return now>=after;}
 public boolean failure(long now){failures=Math.min(4,failures+1);after=now+Math.min(1_000_000_000L,100_000_000L*(1L<<failures));boolean log=now>=report;if(log)report=now+10_000_000_000L;return log;}
 public void success(){after=0;failures=0;}
}
