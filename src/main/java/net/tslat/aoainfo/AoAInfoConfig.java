package net.tslat.aoainfo;

import net.minecraftforge.common.config.Config;

@Config(modid = "aoainfo", type = Config.Type.INSTANCE, name = "aoainfo")
@Config.LangKey("gui.aoainfoconfig.title")
public class AoAInfoConfig {
	@Config.Comment("Set this to false to disable additional tooltip info")
	@Config.LangKey("gui.aoainfoconfig.displayAdvancedTooltips")
	public static boolean displayAdvancedTooltips = true;

	@Config.Comment("Set this to false to disable the ammo HUD that shows when holding a weapon that uses ammo")
	@Config.LangKey("gui.aoainfoconfig.displayAmmoHud")
	public static boolean displayAmmoHud = true;
}
