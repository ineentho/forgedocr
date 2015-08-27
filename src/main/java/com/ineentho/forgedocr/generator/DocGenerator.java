package com.ineentho.forgedocr.generator;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameData;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocGenerator {

    private static int framebufferID;

    public static void generate() {
        Gson gson = new Gson();

        framebufferID = EXTFramebufferObject.glGenFramebuffersEXT();

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

            if (block instanceof BlockFire)
                continue;
            System.out.println("Rendering block " + location.getResourcePath() + " " + block);
            renderBlock(block, new File("doc/blocks/" + docBlock.domain + "-" + docBlock.path + ".png"));
        }

        for (Object loc : items) {
            ResourceLocation location = (ResourceLocation) loc;
            Item item = GameData.getItemRegistry().getObject(location);

            System.out.println("Rendering item " + location.getResourcePath() + " " + item);
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
    private static IntBuffer pixelBuffer = null;
    private static int[] pixelValues = null;

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

        GlStateManager.enableLighting();
        GlStateManager.enableDepth();

        try {
            Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(item), 0, 0);
        } catch (Exception e) {
            System.out.println("Could not render " + item);
            return;
        }


        Framebuffer fb = Minecraft.getMinecraft().getFramebuffer();

        int k = size * size ;
        if (pixelBuffer == null || pixelBuffer.capacity() < k) {
            pixelBuffer = BufferUtils.createIntBuffer(k);
            pixelValues = new int[k];
        }

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        pixelBuffer.clear();

       // if (OpenGlHelper.isFramebufferEnabled()) {
        //    GlStateManager.bindTexture(fb.framebufferTexture);
         //   GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        //} else {
            GL11.glReadPixels(0, 0, size , size , GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        //}

        pixelBuffer.get(pixelValues);
        TextureUtil.processPixelValues(pixelValues, size , size );

        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        if (OpenGlHelper.isFramebufferEnabled()) {
            for (int y = 0; y < size; ++y)
                for (int x = 0; x < size; ++x)
                    img.setRGB(x, y, pixelValues[y * size + x]);
        } else {
            img.setRGB(0, 0, size , size, pixelValues, 0, size );
        }

        try {
            ImageIO.write(img, "PNG", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
