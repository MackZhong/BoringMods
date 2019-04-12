package net.mack.mousewheelie.util;

import net.fabricmc.fabric.impl.itemgroup.CreativeGuiExtensions;
import net.fabricmc.fabric.impl.itemgroup.FabricCreativeGuiComponents;
import net.minecraft.client.gui.ingame.CreativePlayerInventoryScreen;

public final class FabricCreativeGuiHelper {
	CreativeGuiExtensions fabricExtensions;

	public FabricCreativeGuiHelper(CreativePlayerInventoryScreen screen) {
		fabricExtensions = (CreativeGuiExtensions) screen;
	}

	public void nextPage() {
		fabricExtensions.fabric_nextPage();
	}

	public void previousPage() {
        fabricExtensions.fabric_previousPage();
	}

	public int getCurrentPage() {
		return fabricExtensions.fabric_currentPage();
	}

	public int getPageForTabIndex(int index) {
		return index < 12 ? 0 : (index - 12) / (12 - FabricCreativeGuiHelper.getCommonItemGroupsSize()) + 1;
	}

	public static final int getCommonItemGroupsSize() {
		return FabricCreativeGuiComponents.COMMON_GROUPS.size();
	}
}
