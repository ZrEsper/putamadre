package blackoutInteractive.ema_08_.birgadierWrapper;

import blackoutInteractive.ema_08_.birgadierWrapper.AbstractExtensibleCommandPart;
import java.util.Objects;

final class SubCommand
extends AbstractExtensibleCommandPart {
    protected final String subName;

    protected SubCommand(String subName) {
        this.subName = Objects.requireNonNull(subName);
    }
}

