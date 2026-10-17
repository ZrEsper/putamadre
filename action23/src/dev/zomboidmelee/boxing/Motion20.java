package dev.zomboidmelee.boxing;
/** Original action tracks, expressed in viewport coordinates and radians for body poses. */
public final class Motion20{
 public static final int LIGHT=0,HEAVY=1,STOMP=2,SHOVE=3,SEARCH=4,INTERACT=5;
 public static double smooth(double p){p=Math.max(0,Math.min(1,p));return p*p*p*(p*(p*6-15)+10);}
 public static double track(double p,double[]t,double[]v){if(!Double.isFinite(p)||p<=0||p>=1)return 0;for(int i=1;i<t.length;i++)if(p<=t[i])return v[i-1]+(v[i]-v[i-1])*smooth((p-t[i-1])/(t[i]-t[i-1]));return 0;}
 public static double strike(int kind,double p){return track(p,kind==HEAVY?new double[]{0,.19,.37,.46,.66,1}:kind==SHOVE?new double[]{0,.13,.29,.42,.67,1}:new double[]{0,.10,.25,.34,.57,1},kind==HEAVY?new double[]{0,-.32,.25,1,.30,0}:kind==SHOVE?new double[]{0,-.12,1,.92,.15,0}:new double[]{0,-.22,1,.76,-.08,0});}
 public static double lift(double p){return StompMotion21.lift(p);}
 public static double stamp(double p){return StompMotion21.stamp(p);}
 public static long duration(int kind){return kind==HEAVY?600_000_000L:kind==STOMP?StompMotion21.DURATION:kind==SHOVE?520_000_000L:kind==INTERACT?460_000_000L:340_000_000L;}
 public static double impact(int kind){return kind==HEAVY?.46:kind==STOMP?StompMotion21.PLANT:kind==SHOVE?.29:.25;}
 public record Frame(double x,double y,double z,double rx,double ry,double rz){}
 public static Frame arm(int kind,int side,int combo,double p){int sign=side==0?1:-1;boolean dominant=side==Math.floorMod(combo,2);double a=strike(kind,p)*(dominant?1:.045);
  if(kind==SEARCH){double wave=Math.sin(p*Math.PI*2+side*Math.PI);return new Frame(sign*(.30+.05*wave),-.64+.06*wave,-.33-.09*wave,-66-9*wave,sign*12,sign*(8+5*wave));}
  if(kind==SHOVE){a=strike(kind,p);return new Frame(sign*(.41-.14*a),-.61+.12*a,-.16-.48*a,-67-9*a,sign*(12-9*a),sign*(5+4*a));}
  if(kind==INTERACT){double reach=track(p,new double[]{0,.17,.39,.58,1},new double[]{0,.20,1,.82,0});return new Frame(sign*(.43-(side==0?.22:.03)*reach),-.66+(side==0?.15:.025)*reach,-.14-(side==0?.48:.03)*reach,-54-(side==0?24:3)*reach,sign*(19-(side==0?29:1)*reach),sign*(4+(side==0?22:2)*reach));}
  if(kind==HEAVY||kind==LIGHT){double strike=Math.max(0,a),load=Math.max(0,-a);if(kind==HEAVY&&dominant)return new Frame(sign*(.43+.29*load-.37*strike),-.66+.11*strike-.08*load,-.14+.08*load-.36*strike,-54-28*strike+8*load,sign*(19+60*load-62*strike),sign*(4+34*strike));if(kind==LIGHT&&dominant)return new Frame(sign*(.43+.10*load-.15*strike),-.66+.10*strike,-.14+.07*load-.61*strike,-54-23*strike+4*load,sign*(19+9*load-21*strike),sign*(4+12*strike));double brace=strike(kind,p);return new Frame(sign*(.43+.035*brace),-.66-.045*brace,-.14+.035*brace,-54+7*brace,sign*(19+5*brace),sign*(4+5*brace));}
  if(kind==STOMP)return new Frame(sign*(.43+.12*lift(p)),-.68+.10*lift(p)-.10*stamp(p),-.14+.09*lift(p),-54+12*lift(p),sign*(19+14*lift(p)),sign*(4+9*lift(p)));
  return new Frame(sign*(.43-.10*a),-.66+.17*a,-.14-.55*a,-54-17*a,sign*(19-16*a),sign*(4+6*a));
 }
 public static Frame leg(double p){double lift=lift(p),down=stamp(p),visible=smooth(Math.min(p/.10,(1-p)/.16));return new Frame(.24,-1.1+.72*visible+.30*lift-.38*down,-.46-.10*down,22*lift-4*down,0,-4*lift);}
 public static double camera(double seconds){if(seconds<=0||seconds>.36)return 0;return (1-Math.exp(-seconds*75))*Math.exp(-seconds*16)*Math.sin(seconds*19);}
}
