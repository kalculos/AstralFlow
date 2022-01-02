/*
 *
 *
 *  *
 *  *     AstralFlow - Storage utilities for spigot servers.
 *  *     Copyright (C) 2022 iceBear67
 *  *
 *  *     This library is free software; you can redistribute it and/or
 *  *     modify it under the terms of the GNU Lesser General Public
 *  *     License as published by the Free Software Foundation; either
 *  *     version 2.1 of the License, or (at your option) any later version.
 *  *
 *  *     This library is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  *     Lesser General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Lesser General Public
 *  *     License along with this library; if not, write to the Free Software
 *  *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *  *     USA
 *
 */

package io.ib67.astralflow;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.ib67.Util;
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.config.Language;
import io.ib67.astralflow.listener.BlockListener;
import io.ib67.astralflow.listener.MachineListener;
import io.ib67.astralflow.manager.FactoryManagerImpl;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.manager.MachineManagerImpl;
import io.ib67.astralflow.scheduler.TickScheduler;
import io.ib67.astralflow.storage.IMachineStorage;
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

public final class AstralFlow extends JavaPlugin implements AstralFlowAPI {
    private Gson configSerializer;
    private AstralFlowConfiguration configuration;
    @Getter
    private IMachineManager machineManager;
    private final Path machineDir = getDataFolder().toPath().resolve("machines");
    private final Path languageDir = getDataFolder().toPath().resolve("locales");
    @Getter
    private IFactoryManager factories;
    private TickScheduler scheduler;
    public static AstralFlowAPI getInstance() {
        return AstralFlow.getPlugin(AstralFlow.class);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Log.info("Loading &aConfigurations");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        machineDir.toFile().mkdirs();
        languageDir.toFile().mkdirs();
        loadFactoryManager(); // FileStorage needs.
        loadConfig();
        Log.info("Loading &aMachines");
        loadMachineManager();
        loadAllMachines();
        scheduler = new TickScheduler(machineManager);
        scheduler.runTaskTimer(this, 0L, 1L); // Every tick.
        loadListeners();
        // Load StorageLoader in other sourceset.
        Util.runCatching(() -> Class.forName("astralflow.storage.StorageLoader", true, getClassLoader()).getDeclaredConstructor().newInstance()).alsoPrintStack();
    }

    @Override
    public void onDisable() {
        // save data.
        machineManager.saveMachines();
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
        Log.info(machineManager.getAllMachines().size() + " machines were detected.");
    }

    private void loadAllMachines() {
        machineManager.getAllMachines().forEach(machineManager::getMachine);
    }

    @SneakyThrows
    private void loadConfig() {
        configSerializer = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Language.class, new LanguageSerializer(languageDir))
                .registerTypeHierarchyAdapter(IMachineStorage.class, new MachineStorageSerializer(machineDir, factories))
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
}
