package com.ineentho.forgedocr.generator;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameData;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocGenerator {
    public static void generate() {
        Gson gson = new Gson();

        List<DocBlock> docBlocks = new ArrayList<DocBlock>();

        new File("doc/blocks").mkdirs();
        new File("doc/items").mkdirs();

        Set items = GameData.getItemRegistry().getKeys();
        Set blocks = GameData.getBlockRegistry().getKeys();

        for (Object loc : blocks) {
            ResourceLocation location = (ResourceLocation) loc;
            Block block = GameData.getBlockRegistry().getObject(location);

            DocBlock docBlock = new DocBlock();
            docBlock.domain = location.getResourceDomain();
            docBlock.path = location.getResourcePath();
            docBlock.displayName = block.getLocalizedName();

            docBlocks.add(docBlock);

            renderBlock(block, new File("doc/blocks/" + docBlock.domain + "-" + docBlock.path + ".png"));
        }

        for (Object loc : blocks) {
            ResourceLocation location = (ResourceLocation) loc;
            Item item = GameData.getItemRegistry().getObject(location);

            try {
                renderItem(item, new File("doc/items/" + location.getResourceDomain() + "-" + location.getResourcePath() + ".png"));
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
        PrintWriter out;
        try {
            out = new PrintWriter("doc/blocks.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            FMLCommonHandler.instance().exitJava(0, false);
            return;
        }


        out.println(gson.toJson(docBlocks));
        out.close();

        FMLCommonHandler.instance().exitJava(0, false);
    }

    private static void renderBlock(Block block, File file) {
        Item item = Item.getItemFromBlock(block);

        if (item != null) {
            try {
                renderItem(item, file);
            } catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void renderItem(Item item, File file) throws LWJGLException {

        int size = 256;

        GlStateManager.viewport(0, 0, size, size);
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, 16, 16, 0.0D, 1000.0D, 3000.0D);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.clear(16640);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //GuiContainerManager.drawItem(0, 0, new ItemStack(item));
        GlStateManager.translate(0, 0, 0.0F);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(item), 0, 0);



        GL11.glReadBuffer(GL11.GL_FRONT_AND_BACK);
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(size* size* bpp);
        GL11.glReadPixels(0, 0, size, size, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);


        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                int i = (x + (size* y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, size - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
