package xaero.pz;

import java.io.*;
import java.nio.file.*;
import java.lang.reflect.*;
import java.util.*;

/** Client-only discovery mask. No edits to Minecraft worlds or Xaero terrain caches. */
public final class Fog {
    static final int CELL = 2, RADIUS = 6;
    static final TreeMap<Integer,TreeSet<Integer>> rows = new TreeMap<>();
    static Path file;
    static String key;
    static double lastX=Double.NaN,lastZ;
    static long lastSave,lastUpdate;
    static boolean dirty, warned;
    private static final Map<String,Method> methods = new HashMap<>();
    static { Runtime.getRuntime().addShutdownHook(new Thread(Fog::flush,"Xaero PZ exploration save")); }
    static Object field(Object o,String name) throws Exception {
        Class<?> c=o instanceof Class?(Class<?>)o:o.getClass();
        while(c!=null) {try {Field f=c.getDeclaredField(name);f.setAccessible(true);return f.get(o instanceof Class?null:o);}catch(NoSuchFieldException e){c=c.getSuperclass();}}
        throw new NoSuchFieldException(name);
    }
    static Object call(Object o,String name,Object...args) throws Exception {
        Class<?> c=o instanceof Class?(Class<?>)o:o.getClass();
        String id=c.getName()+"#"+name+Arrays.toString(Arrays.stream(args).map(a->a==null?null:a.getClass()).toArray());
        Method m=methods.get(id);
        if(m==null) outer: for(Method candidate:c.getMethods()) {
            if(!candidate.getName().equals(name)||candidate.getParameterCount()!=args.length)continue;
            Class<?>[] p=candidate.getParameterTypes();
            for(int i=0;i<p.length;i++) {Class<?> t=p[i];if(t.isPrimitive())t=t==int.class?Integer.class:t==float.class?Float.class:t==double.class?Double.class:t==long.class?Long.class:t==boolean.class?Boolean.class:t;if(args[i]!=null&&!t.isInstance(args[i]))continue outer;}
            m=candidate;m.setAccessible(true);methods.put(id,m);break;
        }
        if(m==null)throw new NoSuchMethodException(id);
        return m.invoke(o instanceof Class?null:o,args);
    }
    static Object mc()throws Exception{return call(Class.forName("net.minecraft.client.Minecraft"),"getInstance");}
    static void error(Exception e){if(!warned){warned=true;System.err.println("[Xaero PZ] Exploration/HUD error: "+e);e.printStackTrace();}}
    static void set(Object o,String name,Object value)throws Exception{Field f=o.getClass().getField(name);f.set(o,value);}
    static synchronized void activate(Path next) throws IOException {
        if(next.equals(file))return;
        flush();rows.clear();file=next;lastX=Double.NaN;lastUpdate=0;dirty=false;
        if(Files.exists(file))try(DataInputStream in=new DataInputStream(new BufferedInputStream(Files.newInputStream(file)))) {
            if(in.readInt()!=0x505A4631)throw new IOException("Unknown exploration format");
            int n=in.readInt();if(n<0||n>20_000_000)throw new IOException("Invalid exploration count");
            for(int i=0;i<n;i++){int x=in.readInt(),z=in.readInt();rows.computeIfAbsent(z,k->new TreeSet<>()).add(x);}
        }
    }
    public static synchronized void flush() {
        if(!dirty||file==null)return;
        try {Files.createDirectories(file.getParent());Path tmp=file.resolveSibling(file.getFileName()+".tmp");
            try(DataOutputStream out=new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)))){
                out.writeInt(0x505A4631);out.writeInt(rows.values().stream().mapToInt(Set::size).sum());
                for(var row:rows.entrySet())for(int x:row.getValue()){out.writeInt(x);out.writeInt(row.getKey());}
            }
            try {Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,file,StandardCopyOption.REPLACE_EXISTING);}
            dirty=false;lastSave=System.currentTimeMillis();
        }catch(Exception e){error(e);}
    }
    static void reveal(double x,double z) {
        int x0=(int)Math.floor((x-RADIUS)/CELL),x1=(int)Math.floor((x+RADIUS)/CELL);
        int z0=(int)Math.floor((z-RADIUS)/CELL),z1=(int)Math.floor((z+RADIUS)/CELL);
        for(int cz=z0;cz<=z1;cz++)for(int cx=x0;cx<=x1;cx++){
            double dx=cx*CELL+CELL*.5-x,dz=cz*CELL+CELL*.5-z;
            if(dx*dx+dz*dz<=RADIUS*RADIUS&&rows.computeIfAbsent(cz,k->new TreeSet<>()).add(cx))dirty=true;
        }
    }
    static void walk(double x,double z) {
        double distance=Double.isNaN(lastX)?0:Math.hypot(x-lastX,z-lastZ);
        // Only connect a short walk. A teleport never reveals its intervening route.
        if(distance>0&&distance<=12){int steps=(int)Math.ceil(distance);for(int i=1;i<=steps;i++)reveal(lastX+(x-lastX)*i/steps,lastZ+(z-lastZ)*i/steps);}
        else reveal(x,z);
        lastX=x;lastZ=z;
    }
    static Object world()throws Exception {
        Object session=call(Class.forName("xaero.map.WorldMapSession"),"getCurrentSession");
        return session==null?null:call(call(session,"getMapProcessor"),"getMapWorld");
    }
    static synchronized void update(Object dimension) throws Exception {
        Object mc=mc(),player=field(mc,"player"),level=field(mc,"level"),world=world();
        if(player==null||level==null||world==null)return;
        Object actual=call(level,"dimension");
        Object dim=call(world,"getDimension",dimension==null?actual:dimension);
        if(dim==null)return;
        Path dir=((Path)call(dim,"getMainFolderPath")).resolve("pz-exploration");
        String multi=String.valueOf(call(dim,"getCurrentMultiworld"));
        String suffix=java.util.HexFormat.of().formatHex(java.security.MessageDigest.getInstance("SHA-256").digest(multi.getBytes(java.nio.charset.StandardCharsets.UTF_8))).substring(0,24);
        activate(dir.resolve(suffix+".bin"));
        // Viewing another dimension cannot discover it from the current player's coordinates.
        if(!actual.equals(dimension==null?actual:dimension))return;
        long now=System.currentTimeMillis();if(now-lastUpdate<100)return;lastUpdate=now;
        Object abilities=call(player,"getAbilities");
        if(Boolean.TRUE.equals(field(abilities,"flying"))||Boolean.TRUE.equals(call(player,"isSpectator"))){lastX=Double.NaN;return;}
        walk(((Number)call(player,"getX")).doubleValue(),((Number)call(player,"getZ")).doubleValue());
        if(dirty&&now-lastSave>=5000)flush();
    }
    @FunctionalInterface interface Rect {void draw(int x1,int z1,int x2,int z2)throws Exception;}
    /** Exact unknown runs, including completely empty rows, without scanning the entire viewport. */
    static void mask(int left,int top,int right,int bottom,Rect draw)throws Exception {
        if(left>=right||top>=bottom)return;
        int rowMin=Math.floorDiv(top,CELL),rowMax=Math.floorDiv(bottom-1,CELL);
        int previous=top;
        for(var row:rows.subMap(rowMin,true,rowMax,true).entrySet()){
            int z=Math.max(top,row.getKey()*CELL),end=Math.min(bottom,(row.getKey()+1)*CELL);
            if(previous<z)draw.draw(left,previous,right,z);
            int start=left;
            for(int cx:row.getValue().subSet(Math.floorDiv(left,CELL),true,Math.floorDiv(right-1,CELL),true)){
                int x=Math.max(left,cx*CELL);if(start<x)draw.draw(start,z,x,end);start=Math.min(right,(cx+1)*CELL);
            }
            if(start<right)draw.draw(start,z,right,end);previous=end;
        }
        if(previous<bottom)draw.draw(left,previous,right,bottom);
    }
    public static synchronized void mini(Object pose,Object buffer,int x,int z,double radius,Object dim) {
        try {update(dim);Object matrix=call(call(pose,"last"),"pose");Class<?> helper=Class.forName("xaero.hud.render.util.RenderBufferUtil");
            int reach=(int)Math.ceil(radius)+4;
            mask(x-reach,z-reach,x+reach,z+reach,(a,b,c,d)->call(helper,"addColoredRect",matrix,buffer,(float)(a-x),(float)(b-z),c-a,d-b,0xff181c16));
        }catch(Exception e){error(e);}
    }
    public static synchronized void big(Object gui,Object matrix,Object buffer,int x,int z,double left,double top,double right,double bottom) {
        try {Object processor=field(gui,"mapProcessor"),world=call(processor,"getMapWorld");Object dimension=call(world,"getCurrentDimensionId");update(dimension);
            Class<?> helper=Class.forName("xaero.map.graphics.MapRenderHelper");
            mask((int)Math.floor(left)-4,(int)Math.floor(top)-4,(int)Math.ceil(right)+4,(int)Math.ceil(bottom)+4,
                (a,b,c,d)->call(helper,"fillIntoExistingBuffer",matrix,buffer,a-x,b-z,c-x,d-z,0.094f,0.110f,0.086f,1f));
        }catch(Exception e){error(e);}
    }
    public static void prepare() {try {update(null);}catch(Exception e){error(e);}}
    public static void safeColor(int[] color,int x,int z) {
        TreeSet<Integer> row=rows.get(Math.floorDiv(z,CELL));
        if(row==null||!row.contains(Math.floorDiv(x,CELL))){color[0]=24;color[1]=28;color[2]=22;}
    }
    public static void layout(Object context) {
        try {int width=(int)field(context,"screenWidth"),w=(int)field(context,"w");set(context,"x",Math.max(6,width-w-8));set(context,"y",8);set(context,"flippedVertically",false);set(context,"flippedHorizontally",true);}catch(Exception e){error(e);}
    }
    public static int smaller(int original){return Math.max(55,Math.round(original*0.80f));}
    static String clock(long ticks){long time=Math.floorMod(ticks+6000,24000);return String.format(java.util.Locale.ROOT,"%02d:%02d",time/1000,(time%1000)*60/1000);}
    static String season(String name){return switch(name){case "SPRING"->"Primavera";case "SUMMER"->"Verano";case "AUTUMN","FALL"->"Otoño";case "WINTER"->"Invierno";default->"Estación no disponible";};}
    public static void info(Object compiler) {
        try {Object level=field(mc(),"level");if(level==null)return;call(compiler,"addLine",clock(((Number)call(level,"getDayTime")).longValue()));
            try {Object state=call(Class.forName("sereneseasons.api.season.SeasonHelper"),"getSeasonState",level);call(compiler,"addWords",season(String.valueOf(call(state,"getSeason"))));}
            catch(ClassNotFoundException e){call(compiler,"addWords","Sin estaciones");}
        }catch(Exception e){error(e);}
    }
    public static void frame(Object context,Object graphics) {
        try {int x=(int)field(context,"x"),y=(int)field(context,"y"),w=(int)field(context,"w"),h=(int)field(context,"h");
            call(graphics,"fill",x-1,y-1,x+w+1,y+1,0xffb5ac8c);call(graphics,"fill",x-1,y+h-1,x+w+1,y+h+1,0xff615e4c);
            call(graphics,"fill",x-1,y,x+1,y+h,0xffb5ac8c);call(graphics,"fill",x+w-1,y,x+w+1,y+h,0xff615e4c);
        }catch(Exception e){error(e);}
    }
}
