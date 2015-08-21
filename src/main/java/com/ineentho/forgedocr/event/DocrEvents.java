package com.ineentho.forgedocr.event;

import com.ineentho.forgedocr.generator.DocGenerator;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Timer;
import java.util.TimerTask;


public class DocrEvents {
    private boolean firstUpdate = true;
    private int renderNr = 0;
    @SubscribeEvent
    public void renderOverlay(RenderGameOverlayEvent event) {
        renderNr++;
        if (renderNr == 200) {
            firstUpdate = false;
            DocGenerator.generate();
            /*if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });
                    }
                }, 1);
            }*/
        }
    }
}
