package dev.zomboid.gore;
/** Fixed 20Hz physics; collision resolver returns actual swept AABB displacement. */
public final class OrganMotion16 {
 public interface Collision {double[] move(double x,double y,double z,double radius,double dx,double dy,double dz);}
 public static final class Body{public double x,y,z,px,py,pz,vx,vy,vz,radius,angle,spin;public int age,rest;public boolean sleeping,landed;
  public Body(double x,double y,double z,double r){this.x=px=x;this.y=py=y;this.z=pz=z;radius=r;}}
 public static boolean step(Body b,Collision collision){
 b.px=b.x;b.py=b.y;b.pz=b.z;b.age++;if(b.age>600)return false;if(b.sleeping)return true;
 b.vy=Math.max(-.65,b.vy-.04);double[]m=collision.move(b.x,b.y,b.z,b.radius,b.vx,b.vy,b.vz);
 if(m==null)return false;for(double v:m)if(!Double.isFinite(v))return false;
 b.x+=m[0];b.y+=m[1];b.z+=m[2];boolean floor=b.vy<0&&Math.abs(m[1]-b.vy)>1e-7;
 if(Math.abs(m[0]-b.vx)>1e-7)b.vx=-b.vx*.18;if(Math.abs(m[2]-b.vz)>1e-7)b.vz=-b.vz*.18;
 if(Math.abs(m[1]-b.vy)>1e-7)b.vy=Math.abs(b.vy)<.07?0:-b.vy*.22;
 b.vx*=floor?.62:.985;b.vz*=floor?.62:.985;b.angle+=b.spin;b.spin*=floor?.65:.99;
 if(floor){b.landed=true;if(Math.abs(b.vx)+Math.abs(b.vy)+Math.abs(b.vz)<.018)b.rest++;else b.rest=0;}
 else b.rest=0;if(b.rest>=8){b.sleeping=true;b.vx=b.vy=b.vz=b.spin=0;}return true;
 }
}
