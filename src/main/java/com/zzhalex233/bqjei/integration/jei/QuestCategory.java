package com.zzhalex233.bqjei.integration.jei;

import betterquesting.core.BetterQuesting;
import com.zzhalex233.bqjei.Reference;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class QuestCategory implements IRecipeCategory<QuestWrapper> {
    public static final String UID = Reference.MOD_ID + ".quests";
    public static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/jei/quest.png");

    private final IDrawable background;
    private final IDrawable icon;

    public QuestCategory(IGuiHelper guiHelper) {
        background = guiHelper.createDrawable(TEXTURE, 0, 0, 144, 74);
        icon = guiHelper.createDrawableIngredient(new ItemStack(BetterQuesting.questBook));
    }

    @Override
    public String getUid() {
        return UID;
    }

    @Override
    public String getTitle() {
        return I18n.format("bqjei.quests");
    }

    @Override
    public String getModName() {
        return Reference.MOD_NAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayout layout, QuestWrapper entry, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        stacks.addTooltipCallback(entry);

        int inputSlots = entry.getInputSlotCount();
        for (int i = 0; i < inputSlots; i++) {
            stacks.init(i, true, (i % 3) * 18, (i / 3) * 18 + 20);
        }

        for (int i = 0; i < entry.getOutputSlotCount(); i++) {
            stacks.init(i + inputSlots, false, (i % 3) * 18 + 90, (i / 3) * 18 + 20);
        }

        stacks.set(ingredients);
    }
}
