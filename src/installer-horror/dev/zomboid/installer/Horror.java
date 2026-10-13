package dev.zomboid.installer;
import javax.swing.*;import java.awt.*;import java.awt.image.BufferedImage;import java.util.Random;import javax.sound.sampled.*;import java.nio.file.*;
public final class Horror {
 public static final Color INK=new Color(11,13,13),PAPER=new Color(225,222,209),RUST=new Color(177,59,49);
 public static class Scene extends JPanel {
  private BufferedImage texture;public Scene(LayoutManager layout){super(layout);setOpaque(false);}
  protected void paintComponent(Graphics g){int w=getWidth(),h=getHeight();if(texture==null||texture.getWidth()!=w||texture.getHeight()!=h){texture=new BufferedImage(Math.max(1,w),Math.max(1,h),BufferedImage.TYPE_INT_RGB);Graphics2D a=texture.createGraphics();a.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);a.setPaint(new GradientPaint(0,0,new Color(24,31,29),w,h,new Color(5,7,8)));a.fillRect(0,0,w,h);Random random=new Random(42);int ground=(int)(h*.77);for(int x=-30;x<w;x+=38+random.nextInt(30)){int bw=35+random.nextInt(40),bh=50+random.nextInt(Math.max(1,h/3));a.setColor(new Color(8,13,13));a.fillRect(x,ground-bh,bw,bh);a.fillRect(x+5,ground-bh-8,bw-10,8);for(int xx=x+8;xx<x+bw-4;xx+=12)for(int yy=ground-bh+12;yy<ground;yy+=20){a.setColor(random.nextInt(16)==0?new Color(62,49,28):new Color(17,24,22));a.fillRect(xx,yy,4,8);}}a.setPaint(new GradientPaint(0,ground-80,new Color(18,29,27,0),0,h,new Color(9,12,12,245)));a.fillRect(0,ground-80,w,h-ground+80);a.setColor(new Color(65,78,67,12));for(int i=0;i<7;i++)a.fillOval(random.nextInt(Math.max(1,w))-w/2,ground-45+i*14,w,35);for(int i=0;i<w*h/130;i++){int shade=random.nextBoolean()?75:0;a.setColor(new Color(shade,shade,shade,random.nextInt(17)));a.fillRect(random.nextInt(Math.max(1,w)),random.nextInt(Math.max(1,h)),1,1);}a.setColor(new Color(160,169,162,12));for(int i=0;i<150;i++){int x=random.nextInt(Math.max(1,w)),y=random.nextInt(Math.max(1,h));a.drawLine(x,y,x-4,y+15);}a.dispose();}g.drawImage(texture,0,0,null);super.paintComponent(g);}
 }
 public static class Audio {
  private Clip ambient,click;private boolean enabled;private long lastClick;
  private Clip load(Path p)throws Exception {try(AudioInputStream stream=AudioSystem.getAudioInputStream(p.toFile())){Clip c=AudioSystem.getClip();c.open(stream);if(c.isControlSupported(FloatControl.Type.MASTER_GAIN)){FloatControl control=(FloatControl)c.getControl(FloatControl.Type.MASTER_GAIN);control.setValue(Math.max(control.getMinimum(),-15f));}return c;}}
  public boolean start(Path root){if(GraphicsEnvironment.isHeadless())return false;try{ambient=load(root.resolve("audio/ambiente.wav"));click=load(root.resolve("audio/click.wav"));enabled=true;ambient.loop(Clip.LOOP_CONTINUOUSLY);return true;}catch(Exception e){close();return false;}}
  public boolean enabled(){return enabled;}
  public boolean available(){return ambient!=null;}
  public void toggle(){if(ambient==null)return;enabled=!enabled;if(enabled)ambient.loop(Clip.LOOP_CONTINUOUSLY);else{ambient.stop();if(click!=null)click.stop();}}
  public void click(){if(!enabled||click==null||System.nanoTime()-lastClick<120000000)return;lastClick=System.nanoTime();click.stop();click.setFramePosition(0);click.start();}
  public void close(){enabled=false;if(ambient!=null){ambient.stop();ambient.close();ambient=null;}if(click!=null){click.stop();click.close();click=null;}}
 }
}
