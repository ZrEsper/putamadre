package net.flashlight.battery;
/** Column-major affine/projective math, no engine globals and no mutation of game matrices. */
public final class Matrix6{
 public static double[] identity(){double[]a=new double[16];for(int i=0;i<4;i++)a[i*5]=1;return a;}
 public static double[] read(Object m){float[]a=new float[16];R.call(m,"get",a);double[]out=new double[16];for(int i=0;i<16;i++)out[i]=a[i];return out;}
 public static double[] mul(double[]a,double[]b){double[]c=new double[16];for(int x=0;x<4;x++)for(int y=0;y<4;y++)for(int k=0;k<4;k++)c[x*4+y]+=a[k*4+y]*b[x*4+k];return c;}
 public static double[] point(double[]m,double x,double y,double z){double w=m[3]*x+m[7]*y+m[11]*z+m[15];if(Math.abs(w)<1e-12)throw new IllegalArgumentException("zero homogeneous w");return new double[]{(m[0]*x+m[4]*y+m[8]*z+m[12])/w,(m[1]*x+m[5]*y+m[9]*z+m[13])/w,(m[2]*x+m[6]*y+m[10]*z+m[14])/w};}
 public static double[] inverse(double[]m){double[][]a=new double[4][8];for(int r=0;r<4;r++){for(int c=0;c<4;c++)a[r][c]=m[c*4+r];a[r][r+4]=1;}for(int c=0;c<4;c++){int pivot=c;for(int r=c+1;r<4;r++)if(Math.abs(a[r][c])>Math.abs(a[pivot][c]))pivot=r;if(Math.abs(a[pivot][c])<1e-10)throw new IllegalArgumentException("singular render matrix");double[]row=a[c];a[c]=a[pivot];a[pivot]=row;double s=a[c][c];for(int k=0;k<8;k++)a[c][k]/=s;for(int r=0;r<4;r++)if(r!=c){s=a[r][c];for(int k=0;k<8;k++)a[r][k]-=s*a[c][k];}}double[]out=new double[16];for(int r=0;r<4;r++)for(int c=0;c<4;c++)out[c*4+r]=a[r][c+4];return out;}
 public static double[] xyz(Object v){return new double[]{((Number)R.get(v,"x")).doubleValue(),((Number)R.get(v,"y")).doubleValue(),((Number)R.get(v,"z")).doubleValue()};}
}
