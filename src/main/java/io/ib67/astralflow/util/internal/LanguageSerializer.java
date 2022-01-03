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

package io.ib67.astralflow.util.internal;

import com.google.gson.*;
import io.ib67.Util;
import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.config.Language;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;

@RequiredArgsConstructor
@ApiStatus.Internal
public class LanguageSerializer implements JsonSerializer<Language>, JsonDeserializer<Language> {
    private final Path localeDir;

    @Override
    public Language deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var langName = json.getAsJsonPrimitive().getAsString();
        var lang = loadLang(langName);
        if (lang == null) {
            //Log.warn("Cannot load " + langName);
            lang = new Language();
        }
        return lang;
    }

    private Language loadLang(String langName) {
        //TODO Better Fallback & load implementation.
        var lang = localeDir.resolve(langName).toFile();
        Language language = null;
        try (
                var stream = lang.exists()
                        ? new FileInputStream(localeDir.resolve(langName + ".lang").toFile())
                        : AstralFlow.getPlugin(AstralFlow.class).getResource(langName + ".lang")
        ) {
            if (stream == null) {
                return null;
            }
            var context = new String(stream.readAllBytes());
            language = Util.BukkitAPI.gsonForBukkit().fromJson(context, Language.class);

        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
        }
        return language;
    }

    @Override
    public JsonElement serialize(Language src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.getName());
    }
}
