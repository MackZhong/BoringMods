package net.mack.boringmods.mixin;

import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.container.Container;
import net.minecraft.container.Slot;
import net.minecraft.container.SlotActionType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TextComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ContainerScreen.class)
public abstract class MixinContainerScreen extends Screen {

    @Shadow
    protected abstract Slot getSlotAt(double double_1, double double_2);

    @Shadow
    @Final
    protected Container container;

    @Shadow
    protected abstract void onMouseClick(Slot slot_1, int int_1, int int_2, SlotActionType slotActionType_1);

    @Shadow
    @Final
    protected TextComponent name;

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        if (super.mouseScrolled(mouseX, mouseY, scrollAmount)) {
            return true;
        }

        Slot focusSlot = this.getSlotAt(mouseX, mouseY);
        if (null == focusSlot)
            return false;

        ItemStack focusStack = focusSlot.getStack();
        boolean players = focusSlot.inventory instanceof PlayerInventory;
        boolean directUp = scrollAmount > 0;
        boolean sendOut = (players && directUp) || (!players && !directUp);
        if (sendOut) {
            if (this.isControlPressed()) {
                for (Slot slot : this.container.slotList) {
                    if ((slot.inventory == focusSlot.inventory) &&
                            slot.getStack().isEqualIgnoreTags(focusStack)) {
                        this.onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
                    }
                }
            } else if (this.isShiftPressed()) {
                this.onMouseClick(focusSlot, focusSlot.id, 0, SlotActionType.QUICK_MOVE);
            } else
                this.sendItem(focusSlot);
        } else {
            if (this.isControlPressed() || this.isShiftPressed()) {
                for (Slot slot : this.container.slotList) {
                    if (slot.inventory == focusSlot.inventory)
                        continue;
                    if (slot.getStack().isEqualIgnoreTags(focusStack)) {
                        this.onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
                        if (!this.isControlPressed())
                            break;
                    }
                }
            } else {
                Slot moveTo = null;
                int size = Integer.MAX_VALUE;
                for (Slot slot : this.container.slotList) {
                    if (slot.inventory == focusSlot.inventory)
                        continue;
                    if (slot.getStack().isEqualIgnoreTags(focusStack) &&
                            slot.getStack().getAmount() < size) {
                        size = slot.getStack().getAmount();
                        moveTo = slot;
                        if (size == 1)
                            break;
                    }
                }
                if (null != moveTo)
                    this.sendItem(moveTo);
            }
        }
        return true;
    }

    private void sendItem(Slot slot) {
        this.onMouseClick(slot, slot.id, 0, SlotActionType.PICKUP);
        this.onMouseClick(slot, slot.id, 1, SlotActionType.PICKUP);
        this.onMouseClick(slot, slot.id, 0, SlotActionType.QUICK_MOVE);
        this.onMouseClick(slot, slot.id, 0, SlotActionType.PICKUP);
    }
}
