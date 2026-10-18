package net.irisshaders.iris;
import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
public class Iris {public static class Manager{private WorldRenderingPipeline pipeline;public WorldRenderingPipeline getPipelineNullable(){return pipeline;}public void use(WorldRenderingPipeline p){pipeline=p;}}private static final Manager m=new Manager();public static Manager getPipelineManager(){return m;}}
