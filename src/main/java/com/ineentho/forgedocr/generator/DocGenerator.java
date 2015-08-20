package com.ineentho.forgedocr.generator;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DocGenerator {
    public static void generate() {
        Gson gson = new Gson();

        List<DocBlock> docBlocks = new ArrayList<DocBlock>();


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

            renderBlock(block);
        }
        PrintWriter out;
        new File("doc/blocks").mkdirs();
        try {
            out = new PrintWriter("doc/blocks.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
            return;
        }


        out.println(gson.toJson(docBlocks));
        out.close();

        System.exit(0);
    }

    private static void renderBlock(Block block) {
        Item item = Item.getItemFromBlock(block);
        renderItem(item);
    }

    private static void renderItem(Item item) {
    }
}
