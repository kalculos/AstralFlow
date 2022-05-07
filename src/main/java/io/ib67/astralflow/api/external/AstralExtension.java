/*
 *
 *   AstralFlow - The plugin enriches bukkit servers
 *   Copyright (C) 2022 The Inlined Lambdas and Contributors
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

package io.ib67.astralflow.api.external;

import io.ib67.astralflow.AstralFlow;
import io.ib67.astralflow.api.AstralFlowAPI;
import io.ib67.astralflow.api.item.armor.ArmorCategory;
import io.ib67.astralflow.api.item.armor.ArmorItem;
import io.ib67.astralflow.api.item.dummy.DummyCategory;
import io.ib67.astralflow.api.item.dummy.DummyItem;
import io.ib67.astralflow.api.item.machine.MachineCategory;
import io.ib67.astralflow.api.item.machine.MachineItem;
import io.ib67.astralflow.api.item.weapon.WeaponBase;
import io.ib67.astralflow.api.item.weapon.WeaponCategory;
import io.ib67.astralflow.item.ItemKey;
import io.ib67.astralflow.item.ItemKeys;
import io.ib67.astralflow.item.builder.ItemBuilder;
import io.ib67.astralflow.item.builder.ItemCategory;
import io.ib67.astralflow.item.recipe.choices.AstralItemChoice;
import io.ib67.astralflow.item.recipe.choices.ExactItemChoice;
import io.ib67.astralflow.item.recipe.choices.MaterialChoice;
import io.ib67.astralflow.item.recipe.choices.OreDictChoice;
import io.ib67.astralflow.machines.IMachine;
import io.ib67.astralflow.machines.factories.IMachineFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


/**
 * A class that represents an extension.<br />
 * <p>
 * Registering your own contents' by subclassing this first.<br />
 * Remember to call {@link #registerThis()} in your constructor.<br />
 * Also check the doc.
 */
@ApiStatus.AvailableSince("0.1.0")
public abstract class AstralExtension {
    private final ExtensionInfo info;
    protected final AstralFlowAPI flow = AstralFlow.getInstance();

    public AstralExtension(@NotNull ExtensionInfo info) {
        Objects.requireNonNull(info, "extension info cannot be null");
        this.info = info;
    }

    public final ExtensionInfo getInfo() {
        return info;
    }

    /**
     * Called when the extension is in INIT phase. You can register your machines and items here.
     */
    public void init() {

    }

    /**
     * Called when your extension and astralflow itself is going to be disabled. (Generally server shutdown.)
     */
    public void terminate() {

    }

    /**
     * Register this extension into astralflow.<br />
     * Call it in your constructor
     */
    protected final void registerThis() {
        flow.getExtensionRegistry().registerExtension(this);
    }

    /**
     * Short-cut for {@link ItemBuilder#of(ItemCategory)}
     *
     * @param itemCategory
     * @param <C>          the category
     * @param <T>          the item type
     * @return the item builder
     */
    protected final <C extends ItemCategory<T>, T> ItemBuilder<C, T> item(C itemCategory) {
        return ItemBuilder.of(itemCategory);
    }

    /**
     * Short-cut for {@link ItemBuilder#ofSimpleWeapon()} }
     *
     * @return the item builder
     */
    protected final ItemBuilder<WeaponCategory, WeaponBase> itemSimpleWeapon() {
        return ItemBuilder.ofSimpleWeapon();
    }

    /**
     * Short-cut for {@link ItemBuilder#ofSimpleArmor()}
     *
     * @return the item builder
     */
    protected final ItemBuilder<ArmorCategory, ArmorItem> itemSimpleArmor() {
        return ItemBuilder.ofSimpleArmor();
    }

    /**
     * Short-cut for {@link ItemBuilder#ofDummyItem()} )}
     *
     * @return the item builder
     */
    protected final ItemBuilder<DummyCategory, DummyItem> itemDummy() {
        return ItemBuilder.ofDummyItem();
    }

    /**
     * Short-cut for {@link ItemBuilder#ofMachineItem()}
     *
     * @return the item builder
     */
    protected final ItemBuilder<MachineCategory, MachineItem> itemMachine() {
        return ItemBuilder.ofMachineItem();
    }

    /**
     * Short-cut to create a {@link MaterialChoice}
     *
     * @param materials the materials
     * @return the material choice
     */
    protected final MaterialChoice materialChoice(Material... materials) {
        return new MaterialChoice(materials);
    }

    /**
     * Short-cut to create a {@link ExactItemChoice}
     *
     * @param itemStacks the item stacks
     * @return the exact item choice
     */
    protected final ExactItemChoice exactChoice(ItemStack... itemStacks) {
        return new ExactItemChoice(itemStacks);
    }

    /**
     * Shprt-cut to create a {@link OreDictChoice}
     *
     * @param oredicts the oredicts
     * @return the ore dict choice
     */
    protected final OreDictChoice oreDictChoice(String... oredicts) {
        return new OreDictChoice(oredicts);
    }

    /**
     * Short-cut to create a {@link OreDictChoice}
     *
     * @param items the items
     * @return the ore dict choice
     */
    protected final AstralItemChoice astralItemChoice(ItemKey... items) {
        return new AstralItemChoice(items);
    }

    /**
     * Short-cut to create a {@link ItemKey}
     *
     * @param namespace the namespace
     * @param id        the id
     * @return the item key
     */
    protected final ItemKey itemKey(String namespace, String id) {
        return ItemKeys.from(namespace, id);
    }

    /**
     * Register a machine factory.
     *
     * @param machineClass   the machine class
     * @param machineFactory the machine factory
     * @param <M>            the machine type
     */
    protected final <M extends IMachine> void registerFactory(Class<M> machineClass, IMachineFactory<M> machineFactory) {
        AstralFlow.getInstance().getFactories().register(machineClass, machineFactory);
    }
}
