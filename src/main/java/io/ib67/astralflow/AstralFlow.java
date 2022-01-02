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
import io.ib67.astralflow.config.AstralFlowConfiguration;
import io.ib67.astralflow.config.Language;
import io.ib67.astralflow.manager.IMachineManager;
import io.ib67.astralflow.util.internal.LanguageSerializer;
import io.ib67.util.bukkit.Log;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public final class AstralFlow extends JavaPlugin implements AstralFlowAPI {
    private Gson configSerializer;
    private AstralFlowConfiguration configuration;
    @Getter
    private IMachineManager machineManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Log.info("Loading &aConfiguration");
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        loadConfig();
        Log.info("Loading &aMachines");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadMachineManager() {
        // determine machine storage
    }

    @SneakyThrows
    private void loadConfig() {
        configSerializer = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Language.class, new LanguageSerializer(getDataFolder().toPath().resolve("locale")))
                .create();
        // extract config.
        var confFile = new File(getDataFolder(), "config.json");
        if (!confFile.exists()) {
            confFile.createNewFile();
            Files.write(confFile.toPath(), configSerializer.toJson(new AstralFlowConfiguration()).getBytes(StandardCharsets.UTF_8));
        }
        try (
                var config = new FileInputStream(confFile)
        ) {
            configuration = configSerializer.fromJson(new String(config.readAllBytes()), AstralFlowConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            Log.warn("Cannot load configuration. Falling back to default values");
            configuration = new AstralFlowConfiguration();
        }
    }
}
