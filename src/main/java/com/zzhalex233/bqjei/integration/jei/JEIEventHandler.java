package com.zzhalex233.bqjei.integration.jei;

import betterquesting.api.events.DatabaseEvent;
import com.zzhalex233.bqjei.Reference;
import com.zzhalex233.bqjei.config.BQJEIConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public enum JEIEventHandler {
    INSTANCE;

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
            scheduleRefresh();
        }
    }

    @SubscribeEvent
    public void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Minecraft.getMinecraft().addScheduledTask(() -> QuestRegistry.INSTANCE.clear());
    }

    private static void scheduleRefresh() {
        Minecraft.getMinecraft().addScheduledTask(() -> QuestRegistry.INSTANCE.refresh());
    }
}
