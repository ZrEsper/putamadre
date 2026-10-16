package dev.zomboid.remains.hygiene;
/** Server tick policy, no client/render dependence. */
public final class Exposure{
 public long last=-1,exposure,clean=-1,nextVomit;public boolean engaged;
 public record Result(boolean nausea,boolean vomit,boolean clean){}
 public Result step(long now,int bodies){long elapsed=last<0?0:Math.max(0,Math.min(20,now-last));last=now;
  if(bodies==0){if(clean<0)clean=now;if(now-clean>=200){exposure=0;engaged=false;nextVomit=0;return new Result(false,false,true);}return new Result(engaged,false,false);}
  clean=-1;if(bodies<2&&!engaged){exposure=0;nextVomit=0;return new Result(false,false,false);}exposure+=elapsed;if(exposure>=1200)engaged=true;
  boolean vomit=exposure>=1800&&(nextVomit==0||now>=nextVomit);if(vomit)nextVomit=now+1200;return new Result(engaged,vomit,false);
 }
}
