package io.ib67.astralflow.util.internal;

import com.google.gson.*;
import io.ib67.astralflow.manager.IFactoryManager;
import io.ib67.astralflow.storage.IMachineStorage;
import io.ib67.astralflow.storage.impl.FileMachineStorage;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Type;
import java.nio.file.Path;

// todo more storage support
@RequiredArgsConstructor
public class MachineStorageSerializer implements JsonDeserializer<IMachineStorage>, JsonSerializer<IMachineStorage> {
    private final Path storage;
    private final IFactoryManager factory;

    @Override
    public IMachineStorage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return new FileMachineStorage(storage, factory);
    }

    @Override
    public JsonElement serialize(IMachineStorage src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive("filestorage");
    }
}
