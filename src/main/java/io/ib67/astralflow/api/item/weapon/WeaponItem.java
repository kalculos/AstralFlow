package io.ib67.astralflow.api.item.weapon;

import io.ib67.astralflow.hook.HookType;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.LogicalHolder;
import lombok.Builder;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Predicate;

@Builder
@Getter
public class WeaponItem implements LogicalHolder {
    private final int damage;
    private final float criticalChance;
    private final float criticalMultiplier;
    private final Predicate<Entity> entitySelector;
    private final ItemKey itemKey;
    private final ItemStack prototype;


    {
        HookType.ENTITY_DAMAGE.register(this::onEntityDamage);
    }

    private void onEntityDamage(EntityDamageByEntityEvent event) {
        if (entitySelector.test(event.getEntity())) {
            var damage = (float) this.damage;
            if (Math.random() > criticalChance) {
                damage = damage * criticalMultiplier;
            }
            event.setDamage(damage);
        }
    }

    @Override
    public ItemKey getId() {
        return itemKey;
    }
}
