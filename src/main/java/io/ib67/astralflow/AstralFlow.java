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

package io.ib67.astralflow;

import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.external.AstralExtension;
import io.ib67.astralflow.capability.ICapabilityService;
import io.ib67.astralflow.capability.impl.SimpleCapabilityService;
import io.ib67.astralflow.capability.wireless.impl.SimpleWirelessRegistry;
import io.ib67.astralflow.extension.IExtensionRegistry;
import io.ib67.astralflow.extension.impl.ExtensionRegistryImpl;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.server.SaveDataEvent;
import io.ib67.astralflow.internal.AstralConstants;
import io.ib67.astralflow.internal.Warnings;
import io.ib67.astralflow.internal.config.AstralFlowConfiguration;
import io.ib67.astralflow.internal.config.ConfigManager;
import io.ib67.astralflow.internal.config.Language;
import io.ib67.astralflow.internal.listener.*;
import io.ib67.astralflow.internal.listener.crafts.RecipeListener;
import io.ib67.astralflow.internal.serialization.config.LanguageSerializer;
import io.ib67.astralflow.internal.storage.IMachineStorage;
import io.ib67.astralflow.internal.storage.SimpleChunkTracker;
import io.ib67.astralflow.internal.storage.impl.chunk.ChunkBasedMachineStorage;
import io.ib67.astralflow.internal.storage.impl.chunk.MachineCache;
import io.ib67.astralflow.internal.task.SaveDataTask;
import io.ib67.astralflow.internal.update.UpdateChecker;
import io.ib67.astralflow.item.oredict.internal.CompoundOreDict;
import io.ib67.astralflow.item.oredict.internal.SimpleOreDict;
import io.ib67.astralflow.item.oredict.internal.VanillaOreDict;
import io.ib67.astralflow.item.recipe.IRecipeRegistry;
import io.ib67.astralflow.item.recipe.RecipeRegistryImpl;
import io.ib67.astralflow.machines.internal.scheduler.SimpleCatchingScheduler;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.ITickManager;
import io.ib67.astralflow.manager.ItemRegistry;
import io.ib67.astralflow.manager.impl.FactoryManagerImpl;
import io.ib67.astralflow.manager.impl.ItemRegistryImpl;
import io.ib67.astralflow.manager.impl.MachineManagerImpl;
import io.ib67.astralflow.manager.impl.SimpleTickManager;
import io.ib67.astralflow.security.ISecurityService;
import io.ib67.astralflow.security.impl.SimpleSecurityService;
import io.ib67.astralflow.security.mem.impl.SimpleLeakTracker;
import io.ib67.astralflow.texture.ITextureRegistry;
import io.ib67.astralflow.util.LogCategory;
import io.ib67.internal.util.bukkit.Log;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.Cancellable;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

import static io.ib67.astralflow.internal.config.AstralFlowConfiguration.CONFIG_CURRENT_VERSION;
import static io.ib67.astralflow.util.LogCategory.INIT;
import static io.ib67.astralflow.util.LogCategory.MIGRATOR;
import static org.inlambda.kiwi.Kiwi.fromAny;

@ApiStatus.Internal
public final class AstralFlow extends JavaPlugin implements AstralFlowAPI {
    private AstralFlowConfiguration configuration;
    @Getter
    private IMachineManager machineManager;
    private final Path machineIndex = getDataFolder().toPath().resolve("machines.index");
    private final Path languageDir = getDataFolder().toPath().resolve("locales");
    @Getter
    private IFactoryManager factories;
    @Getter
    private final ICapabilityService capabilityService = new SimpleCapabilityService(new SimpleWirelessRegistry());
    @Getter
    private ItemRegistry itemRegistry;
    @Getter
    private final IRecipeRegistry recipeRegistry = new RecipeRegistryImpl();
    @Getter
    private final IExtensionRegistry extensionRegistry = new ExtensionRegistryImpl();
    @Getter
    private ITickManager tickManager;

    private IMachineStorage machineStorage;

    @Getter
    private ISecurityService securityService;

    private static AstralFlow instance;
    private Metrics metric;

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
        if (AstralConstants.DEBUG) {
            // disable watchdog.
            Thread.getAllStackTraces().forEach((t, stackTrace) -> {
                if (t.getName().contains("Watchdog")) {
                    t.stop();
                    Log.info(LogCategory.DEBUG, "Killed Watchdog.");
                }
            });
        }
        if (initialized) {
            Bukkit.getScheduler().runTask(this, Bukkit::shutdown);
            throw new IllegalStateException("AstralFlow IS NOT ALLOWED to be reloaded. We'll shutdown this server for security issues later");
        }
        instance = this;
        // logo
        for (String logo : AstralConstants.LOGO) {
            Bukkit.getLogger().info(ChatColor.AQUA + logo); // removing prefix.
        }
        Log.info(LogCategory.INIT, "Welcome to AstralFlow!");
        // issue-62: more information.
        try (var res = getResource("buildInfo")) {
            Log.info(LogCategory.INIT, new String(res.readAllBytes()));
            if (AstralConstants.DEBUG) {
                Log.info(LogCategory.DEBUG, "Debug mode is enabled.");
            }
            if (AstralConstants.MOCKING) {
                Log.info(LogCategory.DEBUG, "Mocking mode is enabled.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn(LogCategory.INIT, "Cannot read build information. Is it a bug?");
        }
        Log.info(LogCategory.INIT, "Loading &aConfigurations");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        if (fromAny(() -> machineIndex.toFile().createNewFile()).isEmpty()) {
            setEnabled(false);
            return;
        }
        Log.info(LogCategory.INIT, "Loading &aComponents");
        languageDir.toFile().mkdirs();
        loadFactoryManager(); // FileStorage needs.
        loadConfig();
        loadSecurityService();
        var scheduler = new SimpleCatchingScheduler(configuration.optimization.machineTickExceptionLimit);
        tickManager = new SimpleTickManager(scheduler);
        loadMachineManager();
        //scheduler = new TickScheduler(machineManager);
        //scheduler.runTaskTimer(this, 0L, 1L); // Every tick.
        loadItemManager();
        loadListeners();

        if (configuration.recipeSetting.injectVanillaCraftingTable) {
            injectVanillaCraft();
        }
        Bukkit.getScheduler().runTask(this, () -> {
            boolean reloadChunks = false;
            if (Bukkit.getWorlds().stream().mapToInt(world -> world.getLoadedChunks().length).sum() > 0) {
                Log.warn(LogCategory.INIT, "There are chunks loaded in the world, which may cause unexpected behavior due to unregistered machines..");
                Warnings.warnUnstableServerSoft();
                reloadChunks = true;
            }
            /* LOAD MODULES */
            Log.info(LogCategory.INIT, "Loading &aModules");
            extensionRegistry.getExtensions().removeIf(extension -> {
                try {
                    extension.init();
                    Log.info(LogCategory.EXTENSION, "Loaded extension: " + extension.getInfo());
                } catch (Throwable t) {
                    t.printStackTrace();
                    Log.warn(LogCategory.EXTENSION, "Failed to load extension: " + extension.getInfo());
                    Log.warn(LogCategory.EXTENSION, "Issue Tracker URL: " + extension.getInfo().issueTrackerUrl());
                    Log.warn(LogCategory.EXTENSION, "This module will be ignored.");
                    return true;
                }
                return false;
            });
            if (reloadChunks) {
                Log.warn(LogCategory.INIT, "Reloading chunks to fix unregistered machines.");
                for (World world : Bukkit.getWorlds())
                    for (Chunk chunk : world.getLoadedChunks())
                        machineStorage.initChunk(chunk);
            }
            for (Consumer<?> hook : getHooks(HookType.ASTRALFLOW_STARTUP_COMPLETED)) {
                hook.accept(null);
            }
            var dataSaveInterval = getSettings().dataSaveIntervals;
            if (dataSaveInterval != -1) {
                new SaveDataTask().runTaskTimer(this, 0L, dataSaveInterval * 20L);
            }
            initialized = true;
            if (configuration.securitySetting.updateCheck) {
                var isZhCn = Locale.getDefault().toString().equals("zh_CN");
                var sourceUrl = isZhCn
                        ? "https://api.bukkit.rip/gh/AstralFlow/releases"
                        : "https://api.github.com/repos/InlinedLambdas/AstralFlow/releases";
                new UpdateChecker(sourceUrl, getDescription().getVersion()).runTaskTimerAsynchronously(this, 0L, 3 * 600 * 20L); // 30 minutes.
                if (isZhCn) {
                    Log.info(LogCategory.UPDATE_CHECKER, "检测到系统语言为中文，已经自动使用了更新镜像源。");
                }
            }
            metric = new Metrics(this, 15179);

            metric.addCustomChart(new SimplePie("machinecount", () -> String.valueOf(Math.max(5, Math.floor(machineStorage.getKeys().size() / 10) * 10))));
        });

    }

    private void loadSecurityService() {
        var leakTracker = new SimpleLeakTracker();
        var interval = configuration.securitySetting.leakCheckInterval;
        if (interval > 0) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, leakTracker::onTick, 0L, configuration.securitySetting.leakCheckInterval);
        }
        securityService = new SimpleSecurityService(leakTracker);
    }

    private void injectVanillaCraft() {
        Log.info(LogCategory.INIT, "Injecting vanilla crafting table");
        Bukkit.getPluginManager().registerEvents(new RecipeListener(recipeRegistry), this);
    }

    @Override
    public void onDisable() {
        // save data.
        if (!initialized) {
            Log.warn(LogCategory.TERMINATION, "We're not initialized! Won't do anything.");
            return;
        }
        Log.info(LogCategory.EXTENSION, "Disabling Modules");
        for (AstralExtension extension : extensionRegistry.getExtensions()) {
            try {
                extension.terminate();
            } catch (Throwable t) {
                Log.warn(LogCategory.EXTENSION, "Failed to terminate extension: " + extension.getInfo());
                Log.warn(LogCategory.EXTENSION, "Issue Tracker URL: " + extension.getInfo().issueTrackerUrl());
            }
        }
        Log.info(LogCategory.TERMINATION, "Saving Data");
        for (Consumer<?> hook : getHooks(HookType.PLUGIN_SHUTDOWN)) {
            hook.accept(null);
        }
        for (Consumer<SaveDataEvent> hook : getHooks(HookType.SAVE_DATA)) {
            hook.accept(new SaveDataEvent(true));
        }


    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new MachineListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new ItemListener(), this);
        getServer().getPluginManager().registerEvents(new EntityListener(this), this);
    }

    private void loadFactoryManager() {
        factories = new FactoryManagerImpl();
    }

    private void loadMachineManager() {
        machineStorage = new ChunkBasedMachineStorage(
                new MachineCache(machineIndex),
                factories, configuration.optimization.defaultMachineStorageType,
                configuration.optimization.chunkMapCapacity,
                configuration.optimization.allowChunkMapResizing
        );
        machineManager = new MachineManagerImpl(
                machineStorage, tickManager,
                configuration.optimization.initialMachineCapacity, configuration.optimization.allowMachineMapResizing,
                new SimpleChunkTracker(configuration.optimization.chunkMapCapacity,
                        configuration.optimization.allowChunkMapResizing),
                securityService.getLeakTracker());
    }

    private void loadItemManager() {
        itemRegistry = new ItemRegistryImpl(configuration.recipeSetting.addVanillaOreDict
                ? new CompoundOreDict(List.of(new SimpleOreDict(), new VanillaOreDict()))
                : new SimpleOreDict(),
                factories);
    }

    @SneakyThrows
    private void loadConfig() {
        // extract config.

        var confFile = new File(getDataFolder(), "config.conf");
        if (!AstralConstants.MOCKING) extractLanguage();
        ConfigManager<AstralFlowConfiguration> configHolder = new ConfigManager<>(confFile.toPath(), t -> t.serializers(e -> e.register(Language.class, new LanguageSerializer(languageDir))));
        if (!confFile.exists() || confFile.length() == 0) {
            confFile.createNewFile();
            configHolder.saveConfig(AstralFlowConfiguration.defaultConfiguration(machineIndex));
        }
        try {
            configuration = configHolder.getConfig(AstralFlowConfiguration.class);
            if (configuration.version != CONFIG_CURRENT_VERSION) {
                Log.warn(MIGRATOR, "Configuration version mismatch! Expected " + CONFIG_CURRENT_VERSION + " but got " + configuration.version);
                Log.info(MIGRATOR, "Looking for automatic solutions..");
                if (configuration.version > CONFIG_CURRENT_VERSION) {
                    Log.info(MIGRATOR, "Launching Configuration migrator.");
                    //   var migrator = new ConfigMigrator(JsonParser.parseString(confString).getAsJsonObject());
                    //   var conf = migrator.migrate(AstralFlowConfiguration.defaultConfiguration(machineIndex));
                    //    Log.info(MIGRATOR, "Migration complete.");
                    //   Files.writeString(confFile.toPath(), configSerializer.toJson(conf));
                    //    configuration = conf;
                    throw new IOException("Unsupported Feature");
                } else {
                    // higher.
                    Log.warn(MIGRATOR, "Can't migrate configuration. Please update AstralFlow.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn(INIT, "Cannot load configuration. Falling back to default values");
            configuration = AstralFlowConfiguration.defaultConfiguration(machineIndex);
        }

    }

    private void extractLanguage() {
        var defaultLang = languageDir.resolve("zh_CN.conf");
        if (!defaultLang.toFile().exists()) {
            var cnf = new ConfigManager<>(defaultLang, t -> t);
            cnf.saveConfig(new Language());
        }
        saveResource("locale.zip", true);
        try (
                var res = new ZipFile(getDataFolder().toPath().resolve("locale.zip").toFile())
        ) {
            var iter = res.entries().asIterator();
            while (iter.hasNext()) {
                var zipEntry = iter.next();
                if (AstralConstants.DEBUG) {
                    Log.info(INIT, "Extracting locale: " + zipEntry.getName());
                }
                try (var in = res.getInputStream(zipEntry)) {
                    Files.write(languageDir.resolve(zipEntry.getName()), in.readAllBytes());
                }
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            Log.warn(INIT, "Cannot load locales.");
        }
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
    public <T> void addHook(HookType<T> type, Runnable runnable) {
        addHook(type, t -> runnable.run());
    }

    @Override
    public <T> void addHook(HookType<T> type, Consumer<T> runnable) {
        HOOKS.computeIfAbsent(type, k -> new ArrayList<>()).add(runnable);
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<? extends Consumer<T>> getHooks(HookType<T> hook) {
        Object o = Collections.unmodifiableCollection(HOOKS.getOrDefault(hook, Collections.emptyList()));
        return (Collection<? extends Consumer<T>>) o;
    }

    @Override
    public <T> boolean callHooks(HookType<T> hookType, T event) {
        for (Consumer<T> hook : getHooks(hookType)) {
            try {
                hook.accept(event);
            } catch (Throwable throwable) {
                new IllegalStateException("Error while calling hook (type: " + hookType + " )", throwable).printStackTrace();
            }
            if (event instanceof Cancellable) {
                if (((Cancellable) event).isCancelled()) {
                    return true;
                }
            }
        }
        return false;
    }
}
