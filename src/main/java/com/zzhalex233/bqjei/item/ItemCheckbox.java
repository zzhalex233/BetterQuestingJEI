package com.zzhalex233.bqjei.item;

import betterquesting.core.BetterQuesting;
import com.zzhalex233.bqjei.Reference;
import net.minecraft.item.Item;

public class ItemCheckbox extends Item {
    public ItemCheckbox() {
        setMaxStackSize(1);
        setCreativeTab(BetterQuesting.tabQuesting);
        setRegistryName(Reference.MOD_ID, "checkbox");
        setTranslationKey(Reference.MOD_ID + ".checkbox");
    }
}
