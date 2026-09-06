package com.zzhalex233.bqjei.client;

import com.zzhalex233.bqjei.BetterQuestingJEI;
import com.zzhalex233.bqjei.Reference;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public final class ClientEventHandler {
    private ClientEventHandler() {
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomModelResourceLocation(
                BetterQuestingJEI.CHECKBOX,
                0,
                new ModelResourceLocation(BetterQuestingJEI.CHECKBOX.getRegistryName(), "inventory")
        );
    }
}
