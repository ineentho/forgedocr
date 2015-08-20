package com.ineentho.forgedocr.generator;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
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
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(item), 0, 0);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();

        GL11.glReadBuffer(GL11.GL_FRONT);
        int width = Display.getDisplayMode().getWidth();
        int height = Display.getDisplayMode().getHeight();
        int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);


        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int i = (x + (width * y)) * bpp;
                int r = buffer.get(i) & 0xFF;
                int g = buffer.get(i + 1) & 0xFF;
                int b = buffer.get(i + 2) & 0xFF;
                image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
            }
        }

        try {
            ImageIO.write(image, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
