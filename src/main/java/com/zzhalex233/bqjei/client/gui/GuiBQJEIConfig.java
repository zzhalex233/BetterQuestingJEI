package com.zzhalex233.bqjei.client.gui;

import com.zzhalex233.bqjei.Reference;
import com.zzhalex233.bqjei.config.BQJEIConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collections;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiBQJEIConfig extends GuiConfig {
    public GuiBQJEIConfig(GuiScreen parent) {
        super(parent, getConfigElements(BQJEIConfig.getConfig()), Reference.MOD_ID, false, false, Reference.MOD_NAME);
    }

    private static List<IConfigElement> getConfigElements(Configuration config) {
        if (config == null) {
            return Collections.emptyList();
        }

        return new ConfigElement(config.getCategory("bqjei")).getChildElements();
    }
}
