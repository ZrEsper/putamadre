package dev.zomboid.isotacz;
public final class Rules {
 public static boolean rayBox(double ax,double ay,double az,double bx,double by,double bz,int x,int y,int z){double near=0,far=1;double[] a={ax,ay,az},d={bx-ax,by-ay,bz-az},lo={x-.1,y,z-.1},hi={x+1.1,y+1,z+1.1};for(int i=0;i<3;i++){if(Math.abs(d[i])<1e-9){if(a[i]<lo[i]||a[i]>hi[i])return false;}else{double p=(lo[i]-a[i])/d[i],q=(hi[i]-a[i])/d[i];near=Math.max(near,Math.min(p,q));far=Math.min(far,Math.max(p,q));if(near>far)return false;}}return true;}
 public static double wrap(double a){return ((a%360)+540)%360-180;}
 public static boolean sprint(double forward,double side){return Math.hypot(forward,side)>=0.79;}
 public static double[] angles(double x,double y,double z){double horizontal=Math.hypot(x,z);return new double[]{Math.toDegrees(Math.atan2(z,x))-90,-Math.toDegrees(Math.atan2(y,horizontal))};}
 public static double[] project(double x,double y,double w,int width,int height){if(w<=0.001)return null;double nx=x/w,ny=y/w;if(Math.abs(nx)>1||Math.abs(ny)>1)return null;return new double[]{(nx+1)*width/2,(1-ny)*height/2};}
 public record Ray(double[] origin,double[] direction){}
 public static Ray cursor(double x,double y,double z,double yaw,double pitch,double nx,double ny,double aspect,double fov,boolean ortho,double zoom){double a=Math.toRadians(yaw),b=Math.toRadians(pitch),sy=Math.sin(a),cy=Math.cos(a),sp=Math.sin(b),cp=Math.cos(b);double[] forward={-sy*cp,-sp,cy*cp},right={-cy,0,-sy},up={-sy*sp,cp,cy*sp};double ox=nx*aspect*(ortho?zoom*2:Math.tan(Math.toRadians(fov)/2)),oy=ny*(ortho?zoom*2:Math.tan(Math.toRadians(fov)/2));double[] origin={x,y,z},dir=forward.clone();for(int i=0;i<3;i++){double offset=right[i]*ox+up[i]*oy;if(ortho)origin[i]+=offset;else dir[i]+=offset;}double norm=Math.sqrt(dir[0]*dir[0]+dir[1]*dir[1]+dir[2]*dir[2]);for(int i=0;i<3;i++)dir[i]/=norm;return new Ray(origin,dir);}
 public static boolean inView(double dx,double dz,double yaw){return dx*dx+dz*dz<=1||Math.abs(wrap(angles(dx,0,dz)[0]-yaw))<=70;}
 public static double turnSpeed(double previous,double pixelsPerSecond,double dt){return Math.max(90,Math.max(90+Math.min(270,Math.max(0,pixelsPerSecond)*.15),previous*Math.exp(-4*Math.max(0,dt))));}
 public static double edgeYaw(double yaw,double horizontal,double dt){return edgeYaw(yaw,horizontal,dt,90);}
 public static double edgeYaw(double yaw,double horizontal,double dt,double speed){return wrap(yaw+horizontal*speed*Math.max(0,Math.min(.05,dt)));}
 public static double[] edge(double x,double y,double width,double height){double sx=x>=0&&x<=5?-1:x>=width-5&&x<=width?1:0,sy=y>=0&&y<=5?1:y>=height-5&&y<=height?-1:0,n=Math.hypot(sx,sy);return n==0?new double[]{0,0}:new double[]{sx/n,sy/n};}
 public static boolean showAim(boolean iso,boolean armed,boolean held,boolean menu){return iso&&armed&&held&&!menu;}
 public static boolean blockFirstPerson(boolean iso,boolean armed,Object perspective){return iso&&armed&&"FIRST_PERSON".equals(String.valueOf(perspective));}
 public static double[] behind(double yaw,double pitch,double distance){double y=Math.toRadians(yaw),p=Math.toRadians(pitch);return new double[]{Math.sin(y)*Math.cos(p)*distance,Math.sin(p)*distance,-Math.cos(y)*Math.cos(p)*distance};}
}
