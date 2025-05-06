package com.lx862.pwgui.pwcore.data;

import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import java.awt.*;

public enum IconNamePair {
    MINECRAFT("Minecraft", GUIHelper.convertImage(Util.getAssets("/assets/components/minecraft.png"))),
    FABRIC("Fabric", GUIHelper.convertImage(Util.getAssets("/assets/components/fabric.png"))),
    FORGE("Forge", GUIHelper.convertImage(Util.getAssets("/assets/components/forge.png"))),
    NEOFORGE("NeoForge", GUIHelper.convertImage(Util.getAssets("/assets/components/neoforge.png"))),
    QUILT("Quilt", GUIHelper.convertImage(Util.getAssets("/assets/components/quiltmc.png"))),
    LITELOADER("Liteloader", GUIHelper.convertImage(Util.getAssets("/assets/components/liteloader.png"))),
    MODRINTH("Modrinth", GUIHelper.convertImage(Util.getAssets("/assets/services/modrinth.png"))),
    CURSEFORGE("CurseForge", GUIHelper.convertImage(Util.getAssets("/assets/services/curseforge.png")));

    public final String name;
    public final Image image;

    IconNamePair(String name, Image image) {
        this.name = name;
        this.image = image;
    }
}