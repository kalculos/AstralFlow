package io.ib67.astralflow.internal.config;

import lombok.SneakyThrows;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.function.UnaryOperator;


public final class ConfigManager<T> {
    private CommentedConfigurationNode moduleConfigRoot;
    private HoconConfigurationLoader moduleConfigLoader;
    private Path confFile;

    @SneakyThrows
    public ConfigManager(Path confFile, UnaryOperator<ConfigurationOptions> op) {
        this.confFile = confFile;
        this.moduleConfigLoader = HoconConfigurationLoader.builder()
                .path(confFile)
                .defaultOptions(op)
                .build();
        moduleConfigRoot = moduleConfigLoader.load();
    }

    @SneakyThrows
    public void save() {
        moduleConfigLoader.save(moduleConfigRoot);
    }

    @SneakyThrows
    public <A extends T> A getConfig(Class<A> configClass) {
        return moduleConfigRoot.get(configClass);
    }

    @SneakyThrows
    public void reload() {
        this.moduleConfigLoader = HoconConfigurationLoader.builder()
                .path(confFile)
                .build();
        moduleConfigRoot = moduleConfigLoader.load();
    }

    @SneakyThrows
    public void saveConfig(T config) {
        moduleConfigRoot.set(config);
        save();
    }

}