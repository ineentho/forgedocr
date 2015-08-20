package com.ineentho.forgedocr.event;

import com.ineentho.forgedocr.generator.DocGenerator;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class DocrEvents {
    private boolean firstUpdate = true;
    @SubscribeEvent
    public void onLoaded(TickEvent.PlayerTickEvent event) {
        if (event.player.worldObj.isRemote && event.phase == TickEvent.Phase.END && firstUpdate) {
            firstUpdate = false;
            DocGenerator.generate();
        }
    }
}
