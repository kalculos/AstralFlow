/*
 *
 *   AstralFlow - The plugin who is turning bukkit into mod-pack
 *   Copyright (C) 2022 iceBear67
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

package io.ib67.astralflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.external.AstralExtension;
import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.config.Language;
import io.ib67.astralflow.extension.ExtensionRegistryImpl;
import io.ib67.astralflow.extension.IExtensionRegistry;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.internal.*;
import io.ib67.astralflow.internal.config.ConfigMigrator;
import io.ib67.astralflow.item.OreDictImpl;
import io.ib67.astralflow.item.recipe.IRecipeRegistry;
import io.ib67.astralflow.item.recipe.RecipeRegistryImpl;
import io.ib67.astralflow.listener.BlockListener;
import io.ib67.astralflow.listener.MachineListener;
import io.ib67.astralflow.listener.WorldListener;
import io.ib67.astralflow.listener.crafts.RecipeListener;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.manager.impl.FactoryManagerImpl;
import io.ib67.astralflow.manager.impl.ItemRegistryImpl;
import io.ib67.astralflow.manager.impl.MachineManagerImpl;
import io.ib67.astralflow.manager.impl.TickManager;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.ItemStateStorage;
import io.ib67.astralflow.texture.ITextureRegistry;
import io.ib67.util.Util;
import io.ib67.util.bukkit.Log;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

import static io.ib67.astralflow.config.AstralFlowConfiguration.CONFIG_CURRENT_VERSION;

public final class AstralFlow extends JavaPlugin implements AstralFlowAPI {
    private AstralFlowConfiguration configuration;
    @Getter
    private IMachineManager machineManager;
    private final Path machineIndex = getDataFolder().toPath().resolve("machines.index");
    private final Path languageDir = getDataFolder().toPath().resolve("locales");
    private final Path itemDir = getDataFolder().toPath().resolve("items");
    @Getter
    private IFactoryManager factories;
    private ITickManager tickManager;
    @Getter
    private ItemRegistry itemRegistry;
    @Getter
    private final IRecipeRegistry recipeRegistry = new RecipeRegistryImpl();
    @Getter
    private final IExtensionRegistry extensionRegistry = new ExtensionRegistryImpl();

    private static AstralFlow instance;

    AstralFlow(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file); // for mock bukkit
    }
    
    public AstralFlow() {
        super();
    }

    private static final Map<HookType<?>, List<Consumer<?>>> HOOKS = new HashMap<>();
    @Getter
    private static volatile boolean initialized = false; // volatile to prevent opcode reshuffle

    public static AstralFlowAPI getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (initialized) {
            Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
            throw new IllegalStateException("AstralFlow IS NOT ALLOWED to be reloaded. We'll shutdown this server for security issues later");
        }
        instance = this;
        // logo
        for (String logo : AstralConstants.LOGO) {
            Bukkit.getLogger().info(ChatColor.AQUA + logo); // removing prefix.
        }
        Log.info("Welcome to AstralFlow!");
        Log.info("Loading &aConfigurations");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        if (Util.runCatching(() -> machineIndex.toFile().createNewFile()).alsoPrintStack().isFailed()) {
            setEnabled(false);
            return;
        }
        Log.info("Loading &aComponents");
        languageDir.toFile().mkdirs();
        itemDir.toFile().mkdirs();
        loadFactoryManager(); // FileStorage needs.
        loadConfig();
        loadMachineManager();
        //scheduler = new TickScheduler(machineManager);
        //scheduler.runTaskTimer(this, 0L, 1L); // Every tick.
        tickManager = new TickManager();
        loadItemManager();
        loadListeners();

        if (configuration.getRecipeSetting().isInjectVanillaCraftingTable()) {
            injectVanillaCraft();
        }
        Bukkit.getScheduler().runTask(this, () -> {
            boolean reloadChunks = false;
            if (Bukkit.getWorlds().stream().mapToInt(world -> world.getLoadedChunks().length).sum() > 0) {
                Log.warn("There are chunks loaded in the world, which may cause unexpected behavior due to unregistered machines..");
                Warnings.warnUnstableServerSoft();
                reloadChunks = true;
            }
            /* LOAD MODULES */
            Log.info("Loading &aModules");
            extensionRegistry.getExtensions().removeIf(extension -> {
                try {
                    extension.init();
                    Log.info("Loaded extension: " + extension.getInfo());
                } catch (Throwable t) {
                    t.printStackTrace();
                    Log.warn("Failed to load extension: " + extension.getInfo());
                    Log.warn("Issue Tracker URL: " + extension.getInfo().issueTrackerUrl());
                    Log.warn("This module will be ignored.");
                    return false;
                }
                return true;
            });
            if (reloadChunks) {
                Log.warn("Reloading chunks to fix unregistered machines.");
                for (World world : Bukkit.getWorlds())
                    for (Chunk chunk : world.getLoadedChunks())
                        chunk.unload();
            }
            for (Consumer<?> hook : getHooks(HookType.ASTRALFLOW_STARTUP_COMPLETED)) {
                hook.accept(null);
            }
            initialized = true;
        });

    }

    private void injectVanillaCraft() {
        Log.info("Injecting vanilla crafting table");
        Bukkit.getPluginManager().registerEvents(new RecipeListener(recipeRegistry), this);
    }

    @Override
    public void onDisable() {
        // save data.
        if (!initialized) {
            Log.warn("We're not initialized! Won't do anything.");
            return;
        }
        Log.info("Disabling Modules");
        for (AstralExtension extension : extensionRegistry.getExtensions()) {
            try {
                extension.terminate();
            } catch (Throwable t) {
                Log.warn("Failed to terminate extension: " + extension.getInfo());
                Log.warn("Issue Tracker URL: " + extension.getInfo().issueTrackerUrl());
            }
        }
        Log.info("Saving Data");
        for (Consumer<?> hook : getHooks(HookType.PLUGIN_SHUTDOWN)) {
            hook.accept(null);
        }
        for (Consumer<?> hook : getHooks(HookType.SAVE_DATA)) {
            hook.accept(null);
        }


    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new MachineListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    private void loadFactoryManager() {
        factories = new FactoryManagerImpl();
    }

    private void loadMachineManager() {
        var machineStorage = configuration.getStorage();
        machineManager = new MachineManagerImpl(machineStorage);
    }

    private void loadItemManager() {
        var itemStorage = configuration.getItemStorage();
        itemRegistry = new ItemRegistryImpl(itemStorage, new OreDictImpl());
    }

    @SneakyThrows
    private void loadConfig() {
        Gson configSerializer = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Language.class, new LanguageSerializer(languageDir))
                .registerTypeHierarchyAdapter(IMachineStorage.class, new MachineStorageSerializer(machineIndex, factories))
                .registerTypeHierarchyAdapter(ItemStateStorage.class, new ItemStorageSerializer(itemDir, factories))
                .create();
        // extract config.
        var confFile = new File(getDataFolder(), "config.json");
        if (!confFile.exists() || confFile.length() == 0) {
            confFile.createNewFile();
            Files.writeString(confFile.toPath(), configSerializer.toJson(AstralFlowConfiguration.defaultConfiguration(itemDir, machineIndex)));
        }
        try (
                var config = new FileInputStream(confFile)
        ) {
            var confString = new String(config.readAllBytes());
            configuration = configSerializer.fromJson(confString, AstralFlowConfiguration.class);
            if (configuration == null) {
                throw new IOException("Can't parse config");
            }
            if (configuration.getVersion() != CONFIG_CURRENT_VERSION) {
                Log.warn("Configuration version mismatch! Expected " + CONFIG_CURRENT_VERSION + " but got " + configuration.getVersion());
                Log.warn("Looking for automatic solutions..");
                if (configuration.getVersion() > CONFIG_CURRENT_VERSION) {
                    Log.warn("Launching Configuration migrator.");
                    var migrator = new ConfigMigrator(new JsonParser().parse(confString).getAsJsonObject());
                    var conf = migrator.migrate(AstralFlowConfiguration.defaultConfiguration(itemDir, machineIndex));
                    Log.info("Migration complete.");
                    Files.writeString(confFile.toPath(), configSerializer.toJson(conf));
                    configuration = conf;
                } else {
                    // higher.
                    Log.warn("Can't migrate configuration. Please update AstralFlow.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn("Cannot load configuration. Falling back to default values");
            configuration = AstralFlowConfiguration.defaultConfiguration(itemDir, machineIndex);
        }

    }

    @Override
    public ITickManager getTickManager() {
        return tickManager;
    }

    @Override
    public ITextureRegistry getTextureRegistry() {
        return null; // // TODO: 2022/2/20
    }

    @Override
    public AstralFlowConfiguration getSettings() {
        return configuration;
    }

    @Override
    public <T extends HookEvent> void addHook(HookType<T> type, Runnable runnable) {
        addHook(type, t -> runnable.run());
    }

    @Override
    public <T extends HookEvent> void addHook(HookType<T> type, Consumer<T> runnable) {
        HOOKS.computeIfAbsent(type, k -> new ArrayList<>()).add(runnable);
    }

    @SuppressWarnings("unchecked")
    public <T extends HookEvent> Collection<? extends Consumer<T>> getHooks(HookType<T> hook) {
        Object o = HOOKS.getOrDefault(hook, Collections.emptyList());
        return (List<? extends Consumer<T>>) o;
    }
}
