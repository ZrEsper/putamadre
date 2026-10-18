package net.irisshaders.iris.pipeline;
public interface WorldRenderingPipeline {Object getSodiumPrograms();void setPhase(String value);void setOverridePhase(String value);String getPhase();boolean shouldDisableOcclusionCulling();void fail();default void onSetShaderTexture(int id){}}
