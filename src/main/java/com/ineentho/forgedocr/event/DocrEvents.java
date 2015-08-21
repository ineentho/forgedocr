package com.ineentho.forgedocr.event;

import com.ineentho.forgedocr.generator.DocGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Timer;
import java.util.TimerTask;

public class DocrEvents {
    private boolean firstUpdate = true;
    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        if (firstUpdate) {
            firstUpdate = false;
            DocGenerator.generate();
        }
    }
}
