package com.locknar.taczammocheck;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.UnmodifiableConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlWriter;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.LoggerFactory;

public final class ConfigMigration {
    public static final String FILE_NAME = "locknar_doorkick-common.toml";
    private static final String LEGACY_ID = "locknar_tacz_ammo_check";

    private ConfigMigration() {
    }

    public static void migrate(Path directory) {
        Path target = directory.resolve(FILE_NAME);
        if (Files.exists(target, new LinkOption[0])) {
            return;
        }
        Path temporary = null;
        try {
            CommentedConfig merged22 = CommentedConfig.inMemory();
            boolean found = false;
            for (String suffix : new String[]{"-common.toml", "-tactical.toml"}) {
                Path source = directory.resolve(LEGACY_ID + suffix);
                if (!Files.isRegularFile(source, new LinkOption[0])) continue;
                try (CommentedFileConfig config = (CommentedFileConfig)CommentedFileConfig.builder((Path)source).sync().build();){
                    config.load();
                    for (CommentedConfig.Entry entry : config.entrySet()) {
                        merged22.set(List.of(entry.getKey()), entry.getValue());
                    }
                    found = true;
                }
            }
            if (!found) {
                return;
            }
            temporary = Files.createTempFile(directory, "locknar_doorkick-migration-", ".tmp", new FileAttribute[0]);
            new TomlWriter().write((UnmodifiableConfig)merged22, temporary, WritingMode.REPLACE);
            Files.move(temporary, target, new CopyOption[0]);
            if (temporary == null) return;
        }
        catch (Exception error) {
            LoggerFactory.getLogger((String)"locknar_doorkick").warn("Could not migrate legacy configuration; original files were preserved.", (Throwable)error);
            return;
        }
        try {
            Files.deleteIfExists(temporary);
            return;
        }
        catch (IOException merged22) {
            return;
        }
        finally {
            if (temporary != null) {
                try {
                    Files.deleteIfExists(temporary);
                }
                catch (IOException iOException) {}
            }
        }
    }

    public static Path animationOverride(String relativePath) {
        return ConfigMigration.animationOverride(FMLPaths.CONFIGDIR.get(), relativePath);
    }

    static Path animationOverride(Path directory, String relativePath) {
        Path current = directory.resolve("locknar_doorkick").resolve(relativePath);
        if (Files.isRegularFile(current, new LinkOption[0])) {
            return current;
        }
        Path legacy = directory.resolve(LEGACY_ID).resolve(relativePath);
        return Files.isRegularFile(legacy, new LinkOption[0]) ? legacy : current;
    }
}
