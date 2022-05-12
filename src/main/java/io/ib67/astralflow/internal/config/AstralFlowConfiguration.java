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
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

import java.nio.file.Path;
import java.util.Objects;

/**
 * Configuration for the AstralFlow.
 */
//@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@ApiStatus.AvailableSince("0.1.0")
@ConfigSerializable
public final class AstralFlowConfiguration {
    public static int CONFIG_CURRENT_VERSION = 1;
    /**
     * Config version number, for {@link io.ib67.astralflow.internal.config.ConfigMigrator}
     */
    @Comment("Config version number, do not change")
    public int version = CONFIG_CURRENT_VERSION;
    /**
     * Which language is using
     */
    @Comment("Which language to use for the plugin")
    public Language locale = new Language();
    /**
     * The interval to trig {@link io.ib67.astralflow.hook.HookType#SAVE_DATA}
     */
    @Comment("How often should we save data, in ticks. Set -1 to disable")
    @SerializedName("data-save-intervals")
    public int dataSaveIntervals = 300;
    /**
     * Settings about recipe and crafting.
     */
    @Comment("Settings about recipe and crafting.")
    @SerializedName("recipe-settings")
    public RecipeSetting recipeSetting = new RecipeSetting();
    /**
     * Some optimizations
     */
    @SerializedName("optimization-settings")
    @Comment("Some optimizations")
    public Optimization optimization = new Optimization();
    /**
     * Settings about security
     */
    @SerializedName("security-settings")
    @Comment("Settings about security")
    public Security securitySetting = new Security();

    public AstralFlowConfiguration() {
        // empty constructor for CDN
    }

    public static AstralFlowConfiguration defaultConfiguration(Path machineStorageIndexes) {
        Objects.requireNonNull(machineStorageIndexes, "MachineStorageIndexes cannot be null");

        return new AstralFlowConfiguration();
    }

    /**
     * Security settings.
     */
    @ConfigSerializable
    public static class Security {
        /**
         * SHould we kick players until our initialization is done?
         */
        @Comment("Should we kick players until our initialization is done?")
        @SerializedName("allow-player-join-before-init")
        public boolean allowPlayerJoinBeforeInit = false;
        /**
         * How many ticks is a round of leak check.
         * If you don't know any better, just set it to 100.
         */
        @Comment("""
                How many ticks is a round of leak check. Set -1 to disable
                If you don't know any better, just set it to 100.
                """)
        public int leakCheckInterval = 100;

        @Comment("""
                Should we check for update?
                We'll connect to GitHub API to check for update. If you're in China (i.e User Locale == zh_CN ) we'll try to access GitHub via mirror provided by Inlined Lambdas.
                Newer available updates will be sent to admins (astralflow.notification.update) so that they can know what's new and contact admin to update plugin.
                Note: Update Check Services will know your IP, running AstralFlow version. But services cannot identify who you're, what you do, etc. You don't have to worry about that so much.
                """)
        public boolean updateCheck = true;
    }

    /**
     * Optimizations.
     */
    @ConfigSerializable
    public static class Optimization {
        /**
         * How much machine slots should be initialized at start-up
         * This feature may help if your server has tons of machines that are in spawn chunks since it tries to avoid resizing the hashMap.
         */
        @Comment("""
                How much machine slots should be initialized at start-up
                This feature may help if your server has tons of machines that are in spawn chunks since it tries to avoid resizing the hashMap.
                If you don't know any better, just set it to default.
                """)
        @SerializedName("initial-machine-capacity")
        public int initialMachineCapacity = 32;

        /**
         * Can machine map be resized?
         * Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit.
         * Machines usually have lesser elements, so it's recommended to enable this feature.
         * If you don't know what this means, don't change this value.
         */
        @Comment("""
                Can we resize the machine map?
                Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit.
                Machines usually have lesser elements, so it's recommended to enable this feature. If you don't know what this means, don't change this value.
                """)
        @SerializedName("machine-map-resizing")
        public boolean allowMachineMapResizing = true;

        /**
         * How much chunk slots should be initialized at start-up
         * This feature determines the default capacity of the hashmap holding chunks, Higher value may provide a better performance but may cause higher memory usage.
         * You can decrease this to save memory.
         */
        @Comment("""
                How much chunk slots should be initialized at start-up
                This feature determines the default capacity of the hashmap holding chunks, Higher value may provide a better performance but may cause higher memory usage.
                You can decrease this to save memory. If you don't know any better, just set it to default.
                """)
        @SerializedName("chunk-map-capacity")
        public int chunkMapCapacity = 64;

        /**
         * Can chunk map be resized?
         * Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit.
         * If you don't know what does it mean, don't change this value.
         */
        @Comment("""
                Can we resize the chunk map?
                Resizing happens when the elements amount reaches capacity * 0.75F. For resizing, the map will copy all values and re-process them, which may cause a performance hit if there're lots of chunk (with machines) loaded.
                For larger servers, it's recommended to disable this feature. If you don't know what does it mean, don't change this value.
                """)
        @SerializedName("chunk-map-resizing")
        public boolean allowChunkMapResizing = true;

        /**
         * The default serializer to use for machine storage.
         */
        @SerializedName("machine-storage-serializer")
        @Comment("The default serializer to use for machine storage.")
        public MachineStorageType defaultMachineStorageType = MachineStorageType.JSON;

        /**
         * How many exceptions in ticks for us to take action for these exceptional machines? (Deactivation)
         * NOTE: This value WILL NOT decrease at present. If you want to catch frequently occurring exceptions, you can increase this value.
         * If you want to completely ban errored machines, set this value to 0.
         */
        @Comment("""
                How many exceptions in ticks for us to take action for these exceptional machines?
                NOTE: This value WILL NOT decrease at present. If you want to catch frequently occurring exceptions, you can increase this value.
                If you want to completely ban errored machines, set this value to 0.
                """)
        public int machineTickExceptionLimit = 4;
    }

    /**
     * Settings about recipes
     */
    @ConfigSerializable
    public static class RecipeSetting {
        /**
         * Should we add our custom recipes into vanilla crafting tables?
         * P.S We won't really add recipes in it, only to simulate the process.
         * For further details, please see {@link RecipeListener}
         */
        @Comment("""
                Should we add our custom recipes into vanilla crafting tables?
                P.S. We won't really add recipes in it, only to simulate the process, and there are can be some issues with some plugins related to recipes.
                """)
        @SerializedName("inject-vanilla-crafting")
        public boolean injectVanillaCraftingTable = true;
        /**
         * Should we override vanilla recipes if there are same match results?
         * This only affects if {@link RecipeSetting#injectVanillaCraftingTable} is true.
         */
        @Comment("""
                Should we override vanilla recipes if there are same matched results?
                This only affects if injectVanillaCraftingTable is true.
                """)
        @SerializedName("override-vanilla-recipe")
        public boolean overrideVanillaRecipe = true;

        /**
         * Should we add vanilla items into our ore dictionaries?
         * This may be very helpful for many extensions that use vanilla items.
         */
        @Comment("""
                Should we add vanilla items into our ore dictionaries?
                This may be very helpful for many extensions that use vanilla items.
                """)
        @SerializedName("add-vanilla-oredict")
        public boolean addVanillaOreDict = true;
    }
}
