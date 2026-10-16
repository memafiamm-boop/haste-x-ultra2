package com.hastex.ultra;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Simple JSON config, hot-reloaded when the file changes (no restart needed). */
public final class HasteXUltraConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final long CHECK_INTERVAL_TICKS = 40;

    /** Automatically sprint while moving forward. */
    public boolean autoSprint = true;
    /** Also force sprint while the player is flying (creative/spectator). */
    public boolean sprintWhileFlying = false;

    private static volatile HasteXUltraConfig current = new HasteXUltraConfig();
    private static long lastModified = -1;
    private static long tickCounter = 0;

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve(HasteXUltraClient.MOD_ID + ".json");
    }

    public static HasteXUltraConfig get() {
        return current;
    }

    public static void load() {
        Path p = path();
        try {
            if (Files.exists(p)) {
                HasteXUltraConfig loaded = GSON.fromJson(Files.readString(p), HasteXUltraConfig.class);
                if (loaded != null) current = loaded;
            } else {
                Files.createDirectories(p.getParent());
                Files.writeString(p, GSON.toJson(current));
            }
            lastModified = Files.getLastModifiedTime(p).toMillis();
        } catch (IOException | RuntimeException e) {
            HasteXUltraClient.LOGGER.warn("Could not read {}; using defaults/previous values", p, e);
        }
    }

    /** Call once per client tick; reloads the file if it changed on disk. */
    public static void tickReload() {
        if (++tickCounter % CHECK_INTERVAL_TICKS != 0) return;
        try {
            Path p = path();
            if (Files.exists(p) && Files.getLastModifiedTime(p).toMillis() != lastModified) {
                load();
                HasteXUltraClient.LOGGER.info("Config reloaded");
            }
        } catch (IOException ignored) {
        }
    }
}
