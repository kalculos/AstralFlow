/*
 *
 *   AstralFlow - Storage utilities for spigot servers.
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
import io.ib67.Util;
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.config.Language;
import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.hook.event.HookEvent;
import io.ib67.astralflow.listener.BlockListener;
import io.ib67.astralflow.listener.MachineListener;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.manager.*;
import io.ib67.astralflow.manager.impl.FactoryManagerImpl;
import io.ib67.astralflow.manager.impl.ItemManagerImpl;
import io.ib67.astralflow.manager.impl.MachineManagerImpl;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.ItemStateStorage;
import io.ib67.astralflow.util.internal.ItemStorageSerializer;
import io.ib67.astralflow.util.internal.LanguageSerializer;
import io.ib67.astralflow.util.internal.MachineStorageSerializer;
import io.ib67.util.bukkit.Log;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public final class AstralFlow extends JavaPlugin implements AstralFlowAPI {
    private AstralFlowConfiguration configuration;
    @Getter
    private IMachineManager machineManager;
    private final Path machineDir = getDataFolder().toPath().resolve("machines");
    private final Path languageDir = getDataFolder().toPath().resolve("locales");
    private final Path itemDir = getDataFolder().toPath().resolve("items");
    @Getter
    private IFactoryManager factories;
    private ITickManager tickManager;
    @Getter
    private ItemManager itemManager;

    public static AstralFlowAPI getInstance() {
        return AstralFlow.getPlugin(AstralFlow.class);
    }

    private static final Map<HookType<?>, List<Consumer<?>>> HOOKS = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Log.info("Loading &aConfigurations");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        machineDir.toFile().mkdirs();
        languageDir.toFile().mkdirs();
        itemDir.toFile().mkdirs();
        loadFactoryManager(); // FileStorage needs.
        loadConfig();
        Log.info("Loading &aMachines");
        loadMachineManager();
        //scheduler = new TickScheduler(machineManager);
        //scheduler.runTaskTimer(this, 0L, 1L); // Every tick.
        tickManager = new TickManager();
        Log.info("Loading &aItems");
        loadItemManager();
        loadListeners();
        // Load StorageLoader in other sourceset.
        Util.runCatching(() -> Class.forName("astralflow.storage.StorageLoader", true, getClassLoader()).getDeclaredConstructor().newInstance()).alsoPrintStack();
        loadAllMachines();
    }

    @Override
    public void onDisable() {
        // save data.
        for (Consumer<?> hook : getHooks(HookType.PLUGIN_SHUTDOWN)) {
            hook.accept(null);
        }
    }

    private void loadListeners() {
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new MachineListener(), this);
    }

    private void loadFactoryManager() {
        factories = new FactoryManagerImpl();
    }

    private void loadMachineManager() {
        var machineStorage = configuration.getStorage();
        machineManager = new MachineManagerImpl(machineStorage);
        Log.info(machineManager.getAllMachines().size() + " machines were found.");
    }

    private void loadItemManager() {
        var itemStorage = configuration.getItemStorage();
        itemManager = new ItemManagerImpl(itemStorage);
        Log.info(itemStorage.getStates().size() + " items were found.");
    }

    private void loadAllMachines() {
        var machines = new ArrayList<>(machineManager.getAllMachines());
        machines.forEach(machineManager::getMachine);
        for (IMachine loadedMachine : machineManager.getLoadedMachines()) {
            tickManager.registerTickable(loadedMachine).requires(IMachine::isActivated);
        }
        Log.info(machineManager.getLoadedMachines().size() + " machines were loaded");
    }

    @SneakyThrows
    private void loadConfig() {
        Gson configSerializer = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Language.class, new LanguageSerializer(languageDir))
                .registerTypeHierarchyAdapter(IMachineStorage.class, new MachineStorageSerializer(machineDir, factories))
                .registerTypeHierarchyAdapter(ItemStateStorage.class, new ItemStorageSerializer(itemDir, factories))
                .create();
        // extract config.
        var confFile = new File(getDataFolder(), "config.json");
        if (!confFile.exists() || confFile.length() == 0) {
            confFile.createNewFile();
            Files.write(confFile.toPath(), configSerializer.toJson(AstralFlowConfiguration.defaultConfiguration(machineDir)).getBytes(StandardCharsets.UTF_8));
        }
        try (
                var config = new FileInputStream(confFile)
        ) {
            configuration = configSerializer.fromJson(new String(config.readAllBytes()), AstralFlowConfiguration.class);
            if (configuration == null) {
                throw new IOException("Can't parse config");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn("Cannot load configuration. Falling back to default values");
            configuration = AstralFlowConfiguration.defaultConfiguration(machineDir);
        }

    }

    @Override
    public <T extends HookEvent> void addHook(HookType<T> type, Runnable runnable) {
        addHook(type, t -> runnable.run());
    }

    @Override
    public <T extends HookEvent> void addHook(HookType<T> type, Consumer<T> runnable) {
        HOOKS.computeIfAbsent(type, k -> new ArrayList<>()).add(runnable);
    }

    public <T extends HookEvent> Collection<? extends Consumer<T>> getHooks(HookType<T> hook) {
        return (Collection<? extends Consumer<T>>) HOOKS.get(hook);
    }
}
