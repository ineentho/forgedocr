package com.ineentho.forgedocr;

import com.ineentho.forgedocr.event.DocrEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.lwjgl.input.Keyboard;

import java.util.Timer;
import java.util.TimerTask;

@Mod(modid = Docr.MODID, version = Docr.VERSION)
public class Docr
{
    public static final String MODID = "forgedocr";
    public static final String VERSION = "0.1";


    @EventHandler
    public void onPost(FMLPostInitializationEvent event) {
        addCheckerTask();

        MinecraftForge.EVENT_BUS.register(new DocrEvents());
    }

    /**
     * Keep checking if the game has finished loading.
     */
    private void addCheckerTask() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                    @Override
                    public void run() {
                        if (Minecraft.getMinecraft().currentScreen instanceof GuiMainMenu) {
                            if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                                startWorld();
                        } else {
                            addCheckerTask();
                        }
                    }
                });
            }
        }, 2000);
    }

    /**
     * Automatically load a new world
     */
    private void startWorld() {
        WorldSettings worldSettings = new WorldSettings(0, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT);
        Minecraft.getMinecraft().launchIntegratedServer("docr-world", "Docr World", worldSettings);

    }
}
