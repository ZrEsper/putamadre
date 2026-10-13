package blackoutInteractive.ema_08_.rendering.overlay;

import blackoutInteractive.ema_08_.rendering.geom.MatrixRTS;
import blackoutInteractive.ema_08_.rendering.overlay.OverlayPos;
import java.util.Objects;

public record OverlayLocation(OverlayPos pos, MatrixRTS localTransform) {
    public OverlayLocation {
        Objects.requireNonNull(pos, "Pos cannot be null");
        Objects.requireNonNull(localTransform, "Local transform cannot be null");
    }
}

