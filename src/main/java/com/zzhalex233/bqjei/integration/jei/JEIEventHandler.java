package com.zzhalex233.bqjei.integration.jei;

import betterquesting.api.events.DatabaseEvent;
import betterquesting.api2.cache.CapabilityProviderQuestCache;
import betterquesting.api2.cache.QuestCache;
import com.zzhalex233.bqjei.Reference;
import com.zzhalex233.bqjei.config.BQJEIConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Arrays;

@SideOnly(Side.CLIENT)
public enum JEIEventHandler {
    INSTANCE;

    private int[] visibleQuests = new int[0];

    @SubscribeEvent
    public void onQuestDatabaseUpdate(DatabaseEvent.Update event) {
        if (event.getType() == DatabaseEvent.DBType.QUEST || event.getType() == DatabaseEvent.DBType.ALL) {
            scheduleRefresh();
        }
    }

    @SubscribeEvent
    public void onQuestDatabaseLoad(DatabaseEvent.Load event) {
        if (event.getType() == DatabaseEvent.DBType.QUEST || event.getType() == DatabaseEvent.DBType.ALL) {
            scheduleRefresh();
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (Reference.MOD_ID.equals(event.getModID())) {
            BQJEIConfig.sync();
            visibleQuests = getVisibleQuests();
            scheduleRefresh();
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        int[] currentVisibleQuests = getVisibleQuests();
        if (!Arrays.equals(visibleQuests, currentVisibleQuests)) {
            visibleQuests = currentVisibleQuests;
            scheduleRefresh();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        visibleQuests = new int[0];
        Minecraft.getMinecraft().addScheduledTask(() -> QuestRegistry.INSTANCE.clear());
    }

    private static void scheduleRefresh() {
        Minecraft.getMinecraft().addScheduledTask(() -> QuestRegistry.INSTANCE.refresh());
    }

    private static int[] getVisibleQuests() {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityPlayer player = minecraft == null ? null : minecraft.player;
        if (player == null || CapabilityProviderQuestCache.CAP_QUEST_CACHE == null) {
            return new int[0];
        }

        QuestCache cache = player.getCapability(CapabilityProviderQuestCache.CAP_QUEST_CACHE, null);
        return cache == null ? new int[0] : cache.getVisibleQuests();
    }
}
