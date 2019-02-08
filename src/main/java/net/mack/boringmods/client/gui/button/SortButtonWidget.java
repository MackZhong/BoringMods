package net.mack.boringmods.client.gui.button;

import com.google.common.collect.SortedSetMultimap;
import com.mojang.blaze3d.platform.GlStateManager;
import javafx.collections.transformation.SortedList;
import net.mack.boringmods.impl.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.*;

public class SortButtonWidget extends ButtonWidget {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    private final Identifier texture;
    private final int u;
    private final int v;
    private final int hoverVOffset;
    private final Container container;
    private boolean pressed;
    private int screenLeft;
    private int screenTop;

    public SortButtonWidget(int sId, int left, int top, int width, int height, int texU, int texV, int hoverOffset, Identifier tex,
                            Container cont, int screenLeft, int screenTop) {
        super(sId, left, top, width, height, "Sort");

        this.u = texU;
        this.v = texV;
        this.hoverVOffset = hoverOffset;
        this.texture = tex;
        this.container = cont;
        this.screenLeft = screenLeft;
        this.screenTop = screenTop;
    }

    public void setPos(int int_1, int int_2) {
        this.x = int_1;
        this.y = int_2;
    }

    public void draw(int int_1, int int_2, float float_1) {
        this.hovered = int_1 >= this.x && int_2 >= this.y && int_1 < this.x + this.width && int_2 < this.y + this.height;
        MinecraftClient minecraftClient_1 = MinecraftClient.getInstance();
        minecraftClient_1.getTextureManager().bindTexture(this.texture);
        GlStateManager.disableDepthTest();
        int int_3 = this.v;
        if (this.hovered) {
            int_3 += this.hoverVOffset;
        }

        this.drawTexturedRect(this.x, this.y, this.u, int_3, this.width, this.height);

        if (this.pressed) {
            for (int i = 0; i < this.container.slotList.size(); i++) {
                Slot slot = this.container.slotList.get(i);
                String text = String.format("%d", i);
                minecraftClient_1.fontRenderer.drawWithShadow(text, slot.xPosition + this.screenLeft, slot.yPosition + this.screenTop, 0xffaabbcc);
            }
        }
        GlStateManager.enableDepthTest();
    }

    public void onPressed(double x, double y) {
        this.pressed = true;
        List<ItemStack> stacks =  new ArrayList<>();//this.container.getStacks();
//        logger.info(String.format("Pressed, %d stacks", stacks.size()));
//        List<Slot> slots = new ArrayList<>();
        for (int i = 9; i < Math.min(this.container.slotList.size(), 36); ++i) {
            Slot slot = this.container.getSlot(i);
            if(slot.hasStack())
                stacks.add(slot.getStack());
        }
        stacks.sort(new Comparator<ItemStack>() {
            @Override
            public int compare(ItemStack o1, ItemStack o2) {
                return o1.getDisplayName().getFormattedText().compareToIgnoreCase(o2.getDisplayName().getFormattedText());
            }
        });
        for (int i = 9; i < Math.min(stacks.size(), 36); ++i) {
            this.container.setStackInSlot(i, stacks.get(i - 9));
        }

//
//        this.container.updateSlotStacks(stacks);
        this.container.sendContentUpdates();
    }

    public void onReleased(double x, double y){
        logger.info("Released");
        this.pressed = false;
    }
}
