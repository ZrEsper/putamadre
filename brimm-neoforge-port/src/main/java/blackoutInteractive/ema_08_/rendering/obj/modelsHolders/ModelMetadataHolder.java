package blackoutInteractive.ema_08_.rendering.obj.modelsHolders;

class ModelMetadataHolder {
    private final Object metadata;

    protected <T> ModelMetadataHolder(T m) {
        this.metadata = m;
    }

    public <T> T getMetadata() {
        return (T)this.metadata;
    }

    @Deprecated
    public <T> T getMetadata(Class<T> type) {
        return type.cast(this.metadata);
    }
}

