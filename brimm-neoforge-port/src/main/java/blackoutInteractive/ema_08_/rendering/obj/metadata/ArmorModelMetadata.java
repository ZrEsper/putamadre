package blackoutInteractive.ema_08_.rendering.obj.metadata;

import blackoutInteractive.ema_08_.rendering.geom.MatrixRTS;
import blackoutInteractive.ema_08_.rendering.obj.ArmorModelType;
import java.util.Objects;

public record ArmorModelMetadata(ArmorModelType type, MatrixRTS wearing_transform, MatrixRTS workbench_transform) {
    public ArmorModelMetadata {
        Objects.requireNonNull(type, "Missing armor model type");
        Objects.requireNonNull(wearing_transform, "Missing wearing transform");
        Objects.requireNonNull(workbench_transform, "Missing workbench transform");
    }
}

