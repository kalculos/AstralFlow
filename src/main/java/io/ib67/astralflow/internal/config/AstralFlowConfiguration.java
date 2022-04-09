/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
 *
 *   This library is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This library is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *   USA
 */

package io.ib67.astralflow.internal.config;

import com.google.gson.annotations.SerializedName;
import io.ib67.astralflow.internal.listener.crafts.RecipeListener;
import io.ib67.astralflow.internal.storage.impl.MachineStorageType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Configuration for the AstralFlow.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ApiStatus.AvailableSince("0.1.0")
public final class AstralFlowConfiguration {
    public static final int CONFIG_CURRENT_VERSION = 1;

    /**
     * Config version number, for {@link io.ib67.astralflow.internal.config.ConfigMigrator}
     */
    private final int version = CONFIG_CURRENT_VERSION;
    /**
     * Which language is using
     */
    private final Language locale;
    /**
     * SHould we kick players until our initialization is done?
     */
    @SerializedName("allow-player-join-before-init")
    private final boolean allowPlayerJoinBeforeInit = false;

    /**
     * The interval to trig {@link io.ib67.astralflow.hook.HookType#SAVE_DATA}
     */
    @SerializedName("data-save-intervals")
    private final int dataSaveIntervals = 300;

    /**
     * Settings about recipe and crafting.
     */
    @SerializedName("recipe-settings")
    private final RecipeSetting recipeSetting;

    /**
     * Some optimizations
     */
    @SerializedName("optimization-settings")
    private final Optimization optimization = new Optimization();

    public static AstralFlowConfiguration defaultConfiguration(Path itemStorageDir, Path machineStorageIndexes) { // todo: itemStorageDir is unneeded anymore
        Objects.requireNonNull(itemStorageDir, "ItemStorageDir cannot be null");
        Objects.requireNonNull(machineStorageIndexes, "MachineStorageIndexes cannot be null");

        return new AstralFlowConfiguration(
                new Language(),
                new RecipeSetting()
        );
    }

    /**
     * Optimizations.
     */
    @Getter
    public static final class Optimization {
        /**
         * How much machine slots should be initialized at start-up
         * This feature may help if your server has tons of machines that are in spawn chunks since it tries to avoid resizing the hashMap.
         */
        @SerializedName("initial-machine-capacity")
        private final int initialMachineCapacity = 32;

        /**
         * Can machine map be resized?
         * Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit.
         * Machines usually have lesser elements, so it's recommended to enable this feature.
         * If you don't know what this means, don't change this value.
         */
        @SerializedName("chunk-map-resizing")
        private final boolean allowMachineMapResizing = true;

        /**
         * How much chunk slots should be initialized at start-up
         * This feature determines the default capacity of the hashmap holding chunks, Higher value may provide a better performance but may cause higher memory usage.
         * You can decrease this to save memory.
         */
        @SerializedName("chunk-map-capacity")
        private final int chunkMapCapacity = 512;

        /**
         * Can chunk map be resized?
         * Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit.
         * If you don't know what this means, don't change this value.
         */
        @SerializedName("chunk-map-resizing")
        private final boolean allowChunkMapResizing = false;

        /**
         * The default serializer to use for machine storage.
         */
        @SerializedName("machine-storage-serializer")
        private final MachineStorageType defaultMachineStorageType = MachineStorageType.JSON;
    }

    /**
     * Settings about recipes
     */
    @Getter
    public static final class RecipeSetting {
        /**
         * Should we add our custom recipes into vanilla crafting tables?
         * P.S We won't really add recipes in it, only to simulate the process.
         * For further details, please see {@link RecipeListener}
         */
        @SerializedName("inject-vanilla-crafting")
        private final boolean injectVanillaCraftingTable = true;
        /**
         * Should we override vanilla recipes if there are same match results?
         * This only affects if {@link RecipeSetting#injectVanillaCraftingTable} is true.
         */
        @SerializedName("override-vanilla-recipe")
        private final boolean overrideVanillaRecipe = true;

        /**
         * Should we add vanilla items into our ore dictionaries?
         * This may be very helpful for many extensions that use vanilla items.
         */
        @SerializedName("add-vanilla-oredict")
        private final boolean addVanillaOreDict = true;
    }
}
