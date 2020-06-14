package net.tslat.aoainfo;

import net.minecraftforge.common.config.Config;

@Config(modid = "aoainfo", type = Config.Type.INSTANCE, name = "aoainfo")
@Config.LangKey("gui.aoainfoconfig.title")
public class AoAInfoConfig {
	@Config.Comment("Set this to false to disable additional tooltip info")
	@Config.LangKey("gui.aoainfoconfig.displayAdvancedTooltips")
	public static boolean displayAdvancedTooltips = true;
}
