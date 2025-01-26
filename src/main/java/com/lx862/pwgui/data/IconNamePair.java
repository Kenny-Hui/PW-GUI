package com.lx862.pwgui.data;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import java.awt.*;

public enum IconNamePair {
    MINECRAFT("Minecraft", GUIHelper.convertImage(Util.getAssets("/components/minecraft.png"))),
    FABRIC("Fabric", GUIHelper.convertImage(Util.getAssets("/components/fabric.png"))),
    FORGE("Forge", GUIHelper.convertImage(Util.getAssets("/components/forge.png"))),
    NEOFORGE("NeoForge", GUIHelper.convertImage(Util.getAssets("/components/neoforge.png"))),
    QUILT("Quilt", GUIHelper.convertImage(Util.getAssets("/components/quiltmc.png"))),
    LITELOADER("Liteloader", GUIHelper.convertImage(Util.getAssets("/components/liteloader.png"))),
    MODRINTH("Modrinth", GUIHelper.convertImage(Util.getAssets("/services/modrinth.png"))),
    CURSEFORGE("CurseForge", GUIHelper.convertImage(Util.getAssets("/services/curseforge.png")));

    public final String name;
    public final Image image;

    IconNamePair(String name, Image image) {
        this.name = name;
        this.image = image;
    }
}