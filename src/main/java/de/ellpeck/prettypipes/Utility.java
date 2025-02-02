package de.ellpeck.prettypipes;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.WorldlyContainerHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public final class Utility {

    public static <T extends BlockEntity> T getBlockEntity(Class<T> type, BlockGetter world, BlockPos pos) {
        var tile = world.getBlockEntity(pos);
        return type.isInstance(tile) ? (T) tile : null;
    }

    public static void dropInventory(BlockEntity tile, IItemHandler inventory) {
        var pos = tile.getBlockPos();
        for (var i = 0; i < inventory.getSlots(); i++) {
            var stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
                Containers.dropItemStack(tile.getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack);
        }
    }

    public static Direction getDirectionFromOffset(BlockPos pos, BlockPos other) {
        var diff = pos.subtract(other);
        return Direction.fromNormal(diff.getX(), diff.getY(), diff.getZ());
    }

    public static void addTooltip(String name, List<Component> tooltip) {
        if (Screen.hasShiftDown()) {
            var content = I18n.get("info." + PrettyPipes.ID + "." + name).split("\n");
            for (var s : content)
                tooltip.add(Component.literal(s).setStyle(Style.EMPTY.applyFormat(ChatFormatting.GRAY)));
        } else {
            tooltip.add(Component.translatable("info." + PrettyPipes.ID + ".shift").setStyle(Style.EMPTY.applyFormat(ChatFormatting.DARK_GRAY)));
        }
    }

    public static ItemStack transferStackInSlot(AbstractContainerMenu container, IMergeItemStack merge, Player player, int slotIndex, Function<ItemStack, Pair<Integer, Integer>> predicate) {
        var inventoryStart = (int) container.slots.stream().filter(slot -> slot.container != player.getInventory()).count();
        var inventoryEnd = inventoryStart + 26;
        var hotbarStart = inventoryEnd + 1;
        var hotbarEnd = hotbarStart + 8;

        var slot = container.slots.get(slotIndex);
        if (slot != null && slot.hasItem()) {
            var newStack = slot.getItem();
            var currentStack = newStack.copy();

            if (slotIndex >= inventoryStart) {
                // shift into this container here
                // mergeItemStack with the slots that newStack should go into
                // return an empty stack if mergeItemStack fails
                var slots = predicate.apply(newStack);
                if (slots != null) {
                    if (!merge.mergeItemStack(newStack, slots.getLeft(), slots.getRight(), false))
                        return ItemStack.EMPTY;
                }
                // end custom code
                else if (slotIndex >= inventoryStart && slotIndex <= inventoryEnd) {
                    if (!merge.mergeItemStack(newStack, hotbarStart, hotbarEnd + 1, false))
                        return ItemStack.EMPTY;
                } else if (slotIndex >= inventoryEnd + 1 && slotIndex < hotbarEnd + 1 && !merge.mergeItemStack(newStack, inventoryStart, inventoryEnd + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!merge.mergeItemStack(newStack, inventoryStart, hotbarEnd + 1, false)) {
                return ItemStack.EMPTY;
            }
            if (newStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (newStack.getCount() == currentStack.getCount())
                return ItemStack.EMPTY;
            slot.onTake(player, newStack);
            return currentStack;
        }
        return ItemStack.EMPTY;
    }

    public static ListTag serializeAll(Collection<? extends INBTSerializable<CompoundTag>> items) {
        var list = new ListTag();
        for (INBTSerializable<CompoundTag> item : items)
            list.add(item.serializeNBT());
        return list;
    }

    public static void sendBlockEntityToClients(BlockEntity tile) {
        var world = (ServerLevel) tile.getLevel();
        var entities = world.getChunkSource().chunkMap.getPlayers(new ChunkPos(tile.getBlockPos()), false);
        var packet = ClientboundBlockEntityDataPacket.create(tile, BlockEntity::saveWithoutMetadata);
        for (var e : entities)
            e.connection.send(packet);
    }

    public static <T extends INBTSerializable<CompoundTag>> List<T> deserializeAll(ListTag list, Function<CompoundTag, T> supplier) {
        List<T> items = new ArrayList<>();
        for (var i = 0; i < list.size(); i++) {
            var item = supplier.apply(list.getCompound(i));
            if (item != null)
                items.add(item);
        }
        return items;
    }

    public static IItemHandler getBlockItemHandler(Level world, BlockPos pos, Direction direction) {
        var state = world.getBlockState(pos);
        var block = state.getBlock();
        if (!(block instanceof WorldlyContainerHolder holder))
            return null;
        var inventory = holder.getContainer(state, world, pos);
        if (inventory == null)
            return null;
        return new SidedInvWrapper(inventory, direction);
    }

    public interface IMergeItemStack {

        boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection);
    }
}
