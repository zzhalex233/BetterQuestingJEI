package com.zzhalex233.bqjei.proxy;

import com.zzhalex233.bqjei.integration.jei.JEIEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        if (Loader.isModLoaded("jei")) {
            MinecraftForge.EVENT_BUS.register(JEIEventHandler.INSTANCE);
        }
    }
}
