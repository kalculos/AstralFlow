package io.ib67.astralflow.api.item.weapon;

import io.ib67.astralflow.item.builder.ItemCategory;
import io.ib67.astralflow.item.builder.ItemPrototype;
import io.ib67.astralflow.item.factory.ItemPrototypeFactory;

public class WeaponCategory implements ItemCategory<WeaponItem> {
    @Override
    public ItemPrototypeFactory getFactory(WeaponItem item) {
        return ItemPrototype.builder()
                .holder(item)
                .id(item.getId())
                .prototype(item.getPrototype())
                .build();
    }
}
