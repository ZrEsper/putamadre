package com.locknar.taczammocheck;

public record AmmoCheckCartridgeState(int magazineAmmo, Boolean chamberLoaded) {
    public static final AmmoCheckCartridgeState UNKNOWN = new AmmoCheckCartridgeState(-1, null);

    public boolean shouldHide(String source, int minimumAmmo) {
        return switch (source) {
            case "magazine" -> {
                if (this.magazineAmmo >= 0 && this.magazineAmmo < Math.max(1, minimumAmmo)) {
                    yield true;
                }
                yield false;
            }
            case "chamber" -> Boolean.FALSE.equals(this.chamberLoaded);
            default -> false;
        };
    }
}
