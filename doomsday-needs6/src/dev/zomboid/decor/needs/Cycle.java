package dev.zomboid.decor.needs;
/** Server-only active-play countdown, persisted by Needs. No wall clock/offline expiry. */
public final class Cycle {
 public int remaining=12000,kind=0,grace=2400,active=0,nextKind=1;public boolean held=false,comfort=false;
 /** 1=new need; 2=must start here; 3=completed; 0=no transition. */
 public int tick(){if(active>0){if(--active==0){kind=0;remaining=12000;grace=2400;held=false;return 3;}return 0;}if(kind==0){if(--remaining<=0){kind=nextKind;nextKind=3-nextKind;grace=2400;held=false;return 1;}}else if(--grace<=0)return 2;return 0;}
 public boolean hold(){if(kind==0||active>0||held)return false;held=true;grace=2400;return true;}
 public boolean start(boolean toilet){if(kind==0||active>0)return false;active=200;comfort=toilet;return true;}
 public void cancel(){active=0;comfort=false;}
}
