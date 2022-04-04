package io.ib67.astralflow.api.item.dummy;

import io.ib67.astralflow.item.builder.ItemCategory;
import io.ib67.astralflow.item.builder.ItemPrototype;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;

public enum DummyCategory implements ItemCategory<DummyItem> {
    INSTANCE;

    @Override
    public ItemPrototypeFactory getFactory(DummyItem item) {
        return ItemPrototype.builder()
                .id(item.key())
                .prototype(item.item())
                .holder(null)
                .build();
    }
}
