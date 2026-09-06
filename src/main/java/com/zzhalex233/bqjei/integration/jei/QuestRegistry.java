package com.zzhalex233.bqjei.integration.jei;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import betterquesting.api2.storage.DBEntry;
import betterquesting.questing.QuestDatabase;
import com.zzhalex233.bqjei.config.BQJEIConfig;
import mezz.jei.api.IRecipeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public enum QuestRegistry {
    INSTANCE;

    public final ArrayList<QuestWrapper> list = new ArrayList<>();

    public synchronized List<QuestWrapper> build() {
        list.clear();
        collect(list);
        return new ArrayList<>(list);
    }

    @SuppressWarnings("deprecation")
    public synchronized void refresh() {
        IRecipeRegistry recipeRegistry = BQJEIPlugin.runtime == null ? null : BQJEIPlugin.runtime.getRecipeRegistry();

        if (recipeRegistry != null && !list.isEmpty()) {
            for (QuestWrapper wrapper : list) {
                recipeRegistry.removeRecipe(wrapper, QuestCategory.UID);
            }
        }

        list.clear();
        collect(list);

        if (recipeRegistry != null) {
            for (QuestWrapper wrapper : list) {
                recipeRegistry.addRecipe(wrapper, QuestCategory.UID);
            }
        }
    }

    @SuppressWarnings("deprecation")
    public synchronized void clear() {
        IRecipeRegistry recipeRegistry = BQJEIPlugin.runtime == null ? null : BQJEIPlugin.runtime.getRecipeRegistry();

        if (recipeRegistry != null) {
            for (QuestWrapper wrapper : list) {
                recipeRegistry.removeRecipe(wrapper, QuestCategory.UID);
            }
        }

        list.clear();
    }

    private static void collect(List<QuestWrapper> recipes) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft == null ? null : minecraft.player;
        UUID playerId = player == null ? null : QuestingAPI.getQuestingUUID(player);
        Set<Integer> visibleQuestIds = getCachedVisibleQuestIds(player);

        for (DBEntry<IQuest> entry : QuestDatabase.INSTANCE.getEntries()) {
            IQuest quest = entry.getValue();
            if (quest == null) {
                continue;
            }

            boolean visible = isQuestVisible(entry.getID(), quest, player, playerId, visibleQuestIds);
            if (!visible && !BQJEIConfig.showHiddenQuestInfo) {
                continue;
            }

            boolean hidden = !visible && playerId != null;
            QuestWrapper wrapper = QuestWrapper.create(entry.getID(), quest, hidden);
            if (wrapper != null && quest.getProperty(NativeProps.NAME) != null) {
                recipes.add(wrapper);
            }
        }
    }

    private static Set<Integer> getCachedVisibleQuestIds(EntityPlayer player) {
        Set<Integer> visibleQuestIds = new HashSet<>();
        if (player == null || CapabilityProviderQuestCache.CAP_QUEST_CACHE == null) {
            return visibleQuestIds;
        }

        QuestCache cache = player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, null);
        if (cache == null) {
            return visibleQuestIds;
        }

        for (int questId : cache.getVisibleQuests()) {
            visibleQuestIds.add(questId);
        }

        return visibleQuestIds;
    }

    private static boolean isQuestVisible(int questId, IQuest quest, EntityPlayer player, UUID playerId, Set<Integer> visibleQuestIds) {
        if (visibleQuestIds.contains(questId)) {
            return true;
        }

        return playerId != null && QuestCache.isQuestShown(quest, playerId, player);
    }
}
