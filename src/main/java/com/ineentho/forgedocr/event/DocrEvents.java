package com.ineentho.forgedocr.event;

import com.ineentho.forgedocr.generator.DocGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Timer;
import java.util.TimerTask;

public class DocrEvents {
    private boolean firstUpdate = true;
    @SubscribeEvent
    public void onLoaded(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END && firstUpdate && Minecraft.getMinecraft().theWorld != null) {
            firstUpdate = false;
            DocGenerator.generate();
        }
    }
}
