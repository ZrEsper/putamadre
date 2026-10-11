package com.cleannrooster.dungeons_iso.config;
public class Config {public static final Holder GSON=new Holder();public boolean onStartup=true,force=true,showFirstTimeGui=true,ortho;public static class Holder {public Config settings=new Config();public Config instance(){return settings;}}}
