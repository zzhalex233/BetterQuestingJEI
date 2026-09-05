package com.zzhalex233.bqjei;

import com.zzhalex233.bqjei.config.BQJEIConfig;
import com.zzhalex233.bqjei.proxy.IProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = Reference.MOD_ID,
        name = Reference.MOD_NAME,
        version = Reference.VERSION,
        dependencies = Reference.DEPENDENCIES,
        guiFactory = "com.zzhalex233.bqjei.client.gui.ConfigGuiFactory"
)
public class BetterQuestingJEI {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MOD_NAME);

    @Mod.Instance(Reference.MOD_ID)
    public static BetterQuestingJEI instance;

    @SidedProxy(
            modId = Reference.MOD_ID,
            clientSide = "com.zzhalex233.bqjei.proxy.ClientProxy",
            serverSide = "com.zzhalex233.bqjei.proxy.CommonProxy"
    )
    public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        BQJEIConfig.init(event.getSuggestedConfigurationFile());
        proxy.preInit(event);
        LOGGER.info("{} initialized", Reference.MOD_NAME);
    }
}
