package com.ineentho.forgedocr.generator;

import codechicken.core.ClientUtils;
import codechicken.lib.gui.GuiDraw;
import codechicken.nei.ItemPanel;
import codechicken.nei.NEIClientUtils;
import codechicken.nei.config.*;
import codechicken.nei.guihook.GuiContainerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;


public class DocGenerator {
    public static void generate() {
        System.out.println("Dumping items using NEI");
        //temPanelDumper dumper = new ItemPanelDumper("dumper");
        //Minecraft.getMinecraft().displayGuiScreen(iconDumper);
       /* GuiInventory inv = new GuiInventory(Minecraft.getMinecraft().thePlayer);
        Minecraft.getMinecraft().displayGuiScreen(inv);
        OptionList.getOptionList("nei.options").openGui(NEIClientUtils.getGuiContainer(), true);
        ItemPanelDumper itemPanelDumper = new ItemPanelDumper("tools.dump.itempanel");
        Minecraft.getMinecraft().displayGuiScreen(new GuiItemIconDumper(itemPanelDumper, 16));*/

        double iconSize = 16;

        Dimension d = GuiDraw.displayRes();
        GlStateManager.matrixMode(5889);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, (double) d.width * 16.0D / iconSize, (double) d.height * 16.0D / iconSize, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(5888);
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.clear(16640);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GuiContainerManager.drawItem(50, 50, new ItemStack(Blocks.crafting_table));
        GL11.glFlush();
    }
}
