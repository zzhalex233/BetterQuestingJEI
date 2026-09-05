package com.zzhalex233.bqjei.integration.jei;

import betterquesting.core.BetterQuesting;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class BQJEIPlugin implements IModPlugin {
    static IJeiRuntime runtime;
    static IModRegistry registry;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        registry.addRecipeCategories(new QuestCategory(registry.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void register(IModRegistry registry) {
        BQJEIPlugin.registry = registry;

        registry.handleRecipes(QuestWrapper.class, recipe -> recipe, QuestCategory.UID);
        registry.addRecipes(QuestRegistry.INSTANCE.build(), QuestCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BetterQuesting.questBook), QuestCategory.UID);
        registry.addRecipeCatalyst(new ItemStack(BetterQuesting.submitStation), QuestCategory.UID);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        BQJEIPlugin.runtime = runtime;
        QuestRegistry.INSTANCE.refresh();
    }
}
