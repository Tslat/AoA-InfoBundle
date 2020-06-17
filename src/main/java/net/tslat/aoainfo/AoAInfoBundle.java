package net.tslat.aoainfo;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = "aoainfo", version = AoAInfoBundle.VERSION, useMetadata = true, acceptedMinecraftVersions = "1.12.2", dependencies = "required-after:forge@[14.23.5.2846,);")
public class AoAInfoBundle {
	public static final String VERSION = "1.1.2";

	@Mod.EventHandler
	public void fmlPreInit(final FMLPreInitializationEvent preInit) {
		MinecraftForge.EVENT_BUS.register(new AoAInfoEventHandler());
		MinecraftForge.EVENT_BUS.register(new AoAInfoHudRenderer());
	}
}