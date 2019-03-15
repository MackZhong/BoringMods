package net.mack.boringmods.client.gui.button;

import com.google.common.collect.SortedSetMultimap;
import com.mojang.blaze3d.platform.GlStateManager;
import javafx.collections.transformation.SortedList;
import net.mack.boringmods.impl.ModInitializer;
import net.mack.boringmods.mixin.MixinPlayerInventoryScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.ContainerScreen;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ingame.PlayerInventoryScreen;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.container.Container;
import net.minecraft.container.PlayerContainer;
import net.minecraft.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.*;

public class SortButtonWidget extends ButtonWidget {
    private org.apache.logging.log4j.Logger logger = org.apache.logging.log4j.LogManager.getLogger("boringmods");

    private final Container container;
    private static boolean pressed = false;
    private RecipeBookGui recipeBookGui;

    public SortButtonWidget(int sId, int left, int top, int width, int height,
                            Container cont, RecipeBookGui recipe) {
        super(left, top, width, height, "Sort");

        this.container = cont;
        this.recipeBookGui = recipe;
    }

    private int getLeft(boolean narrow, int screenWidth) {
        int left;
        if (this.recipeBookGui.isOpen() && !narrow) {
            left = 177 + (screenWidth - 376) / 2;
        } else {
            left = (screenWidth - 176) / 2;
        }

        return left;
    }

    public void draw(int cursorX, int cursorY, float float_1) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer fontRenderer = client.textRenderer;
        client.getTextureManager().bindTexture(WIDGET_TEX);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, this.opacity);
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


//        logger.info(String.format("Window Width %d, Widnow Scaled Width %d", client.window.getWidth(), client.window.getScaledWidth()));

        int left = this.getLeft(false, client.window.getScaledWidth());
        this.x = left + 144;

        int leftWidth = this.width / 2;
        int rightWidth = this.width - leftWidth;
        int topHeight = this.height / 2;
        int bottomHeight = this.height - topHeight;
        int textureId = this.getTextureId(this.isHovered());
        int texX = 0;
        int texY = 46 + textureId * 20;
        this.drawTexturedRect(this.x, this.y, texX, texY, leftWidth, topHeight);
        this.drawTexturedRect(this.x + leftWidth, this.y, 200 - rightWidth, texY, rightWidth, topHeight);
        this.drawTexturedRect(this.x, this.y + topHeight, texX, texY + 20 - bottomHeight, leftWidth, bottomHeight);
        this.drawTexturedRect(this.x + leftWidth, this.y + topHeight, 200 - rightWidth, texY + 20 - bottomHeight, rightWidth, bottomHeight);
        this.drawBackground(client, cursorX, cursorY);
        int fontColor = 14737632;
        if (!this.enabled) {
            fontColor = 10526880;
        } else if (this.isHovered()) {
            fontColor = 16777120;
        }

        this.drawStringCentered(fontRenderer, this.getText(), this.x + leftWidth, this.y + topHeight - fontRenderer.fontHeight / 2, fontColor | MathHelper.ceil(this.opacity * 255.0F) << 24);

        int top = this.y - 62;
//        left *= client.window.getScaleFactor();
        if (SortButtonWidget.pressed) {
            GlStateManager.disableDepthTest();
            for (int i = 0; i < this.container.slotList.size(); i++) {
                Slot slot = this.container.slotList.get(i);
                String text = String.format("%d", i);
                client.textRenderer.drawWithShadow(text, slot.xPosition + left, slot.yPosition + top, 0xffaabbcc);
            }
            GlStateManager.enableDepthTest();
        }
    }

    public void onPressed(double x, double y) {
        SortButtonWidget.pressed = !SortButtonWidget.pressed;
        logger.info(SortButtonWidget.pressed ? "Pressed." : "UnPressed.");
//        List<ItemStack> stacks =  new ArrayList<>();//this.container.getStacks();
////        logger.info(String.format("Pressed, %d stacks", stacks.size()));
////        List<Slot> slots = new ArrayList<>();
//        for (int i = 9; i < Math.min(this.container.slotList.size(), 36); ++i) {
//            Slot slot = this.container.getSlot(i);
//            if(slot.hasStack())
//                stacks.add(slot.getStack());
//        }
//        stacks.sort(new Comparator<ItemStack>() {
//            @Override
//            public int compare(ItemStack o1, ItemStack o2) {
//                return o1.getDisplayName().getFormattedText().compareToIgnoreCase(o2.getDisplayName().getFormattedText());
//            }
//        });
//        for (int i = 9; i < Math.min(stacks.size(), 36); ++i) {
//            this.container.setStackInSlot(i, stacks.get(i - 9));
//        }
//
////
////        this.container.updateSlotStacks(stacks);
//        this.container.sendContentUpdates();
    }

    public void onReleased(double x, double y) {
        logger.info("Released.");
        SortButtonWidget.pressed = false;
    }

    public void onPressed() {
        logger.info("SortButtonWidget onPressed.");
    }
}
