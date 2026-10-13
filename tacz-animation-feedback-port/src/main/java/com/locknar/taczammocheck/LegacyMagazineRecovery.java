package com.locknar.taczammocheck;

import com.locknar.taczammocheck.TaczGunCompat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

final class LegacyMagazineRecovery {
    private LegacyMagazineRecovery() {
    }

    static void recover(ItemStack gun) {
        if (!gun.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA) || !TaczGunCompat.isTaczGun(gun)) {
            return;
        }
        CompoundTag tag = com.locknar.taczammocheck.port.Tags.read(gun);
        if (tag.getBoolean("LocknarAmmoCheck_InFlight")) {
            tag.remove("TaCZMag_StoredMagazine");
            tag.remove("LocknarAmmoCheck_InFlight");
            TaczGunCompat.setCurrentAmmoCount(gun, 0);
        }
        if (tag.contains("LocknarAmmoCheck_WaitingRounds") && TaczGunCompat.setCurrentAmmoCount(gun, tag.getInt("LocknarAmmoCheck_WaitingRounds"))) {
            tag.remove("LocknarAmmoCheck_WaitingRounds");
        }
        gun.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(tag));
    }
}
