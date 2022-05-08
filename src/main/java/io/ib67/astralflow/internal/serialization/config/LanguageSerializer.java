package io.ib67.astralflow.internal.serialization.config;

import io.ib67.astralflow.internal.config.ConfigManager;
import io.ib67.astralflow.internal.config.Language;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;
import java.nio.file.Path;

@RequiredArgsConstructor
public final class LanguageSerializer implements TypeSerializer<Language> {
    private final Path localeDir;

    @Override
    public Language deserialize(Type type, ConfigurationNode node) throws SerializationException {
        var name = node.getString();
        if (name == null) {
            throw new SerializationException("Language name is null");
        }
        return loadLanguage(name);
    }

    private Language loadLanguage(String name) {
        var cnf = new ConfigManager<>(localeDir.resolve(name + ".conf"), t -> t);
        return cnf.getConfig(Language.class);
    }

    @Override
    public void serialize(Type type, @Nullable Language obj, ConfigurationNode node) throws SerializationException {
        if (obj == null) {
            throw new SerializationException("Language is null");
        }
        node.set(obj.getName());
    }

    @Override
    public @Nullable Language emptyValue(Type specificType, ConfigurationOptions options) {
        return new Language();
    }
}
