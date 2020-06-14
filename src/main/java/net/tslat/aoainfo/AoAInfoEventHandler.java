package net.tslat.aoainfo;

import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.tslat.aoa3.item.tool.axe.BaseAxe;
import net.tslat.aoa3.item.tool.pickaxe.BasePickaxe;
import net.tslat.aoa3.item.tool.shovel.BaseShovel;
import net.tslat.aoa3.item.weapon.archergun.BaseArchergun;
import net.tslat.aoa3.item.weapon.blaster.BaseBlaster;
import net.tslat.aoa3.item.weapon.bow.BaseBow;
import net.tslat.aoa3.item.weapon.greatblade.BaseGreatblade;
import net.tslat.aoa3.item.weapon.gun.BaseGun;
import net.tslat.aoa3.item.weapon.maul.BaseMaul;
import net.tslat.aoa3.item.weapon.shotgun.BaseShotgun;
import net.tslat.aoa3.item.weapon.sword.BaseSword;
import net.tslat.aoa3.utils.StringUtil;

import java.util.List;

public class AoAInfoEventHandler {
	@SubscribeEvent
	public void onTooltipRender(ItemTooltipEvent ev) {
		if (!AoAInfoConfig.displayAdvancedTooltips)
			return;

		Item item = ev.getItemStack().getItem();

		if (item.getRegistryName() == null || !item.getRegistryName().getResourceDomain().equalsIgnoreCase("aoa3"))
			return;

		List<String> tooltipLines = ev.getToolTip();

		if (item instanceof BaseSword || item instanceof BaseGreatblade || item instanceof BaseMaul || item instanceof BaseShovel || item instanceof BaseAxe || item instanceof BasePickaxe) {
			float attackSpeed = 0;
			float damage = 0;
			int lineIndex = 0;

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Attack Speed")) {
					String valueSubstring = line.substring(0, line.indexOf("Attack Speed")).replaceAll(" ", "");

					try {
						attackSpeed = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.endsWith(" Attack Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("Attack Damage")).replaceAll(" ", "");
					lineIndex = i;
					try {
						damage = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
			}

			String dpsValue = StringUtil.roundToNthDecimalPlace(attackSpeed * damage, 2);

			if (Float.parseFloat(dpsValue) == 0)
				return;

			tooltipLines.add(lineIndex + 1, TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
		}
		else if (item instanceof BaseShotgun) {
			BaseShotgun shotgun = (BaseShotgun)item;
			float dmg = (float)shotgun.getDamage();
			float firingRate = 20 / (float)shotgun.getFiringDelay();

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("x")).replaceAll(" ", "");

					try {
						dmg = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.contains("Firing Rate")) {
					String valueSubstring = line.substring(0, line.indexOf("/sec")).replaceAll(" ", "");

					try {
						firingRate = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}

					String dpsValue = StringUtil.roundToNthDecimalPlace(firingRate * dmg * shotgun.getPelletCount(), 2);

					if (Float.parseFloat(dpsValue) == 0)
						return;

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
				}
			}
		}
		else if (item instanceof BaseArchergun) {
			BaseArchergun archergun = (BaseArchergun)item;
			float dmg = (float)archergun.getDamage();
			float firingRate = 20 / (float)archergun.getFiringDelay();

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Average Ranged Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("Average Ranged Damage")).replaceAll(" ", "");

					try {
						dmg = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.contains("Firing Rate")) {
					String valueSubstring = line.substring(0, line.indexOf("/sec")).replaceAll(" ", "");

					try {
						firingRate = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}

					String dpsValue = StringUtil.roundToNthDecimalPlace(firingRate * dmg, 2);

					if (Float.parseFloat(dpsValue) == 0)
						return;

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
				}
			}
		}
		else if (item instanceof BaseGun) {
			BaseGun gun = (BaseGun)item;
			float dmg = (float)gun.getDamage();
			float firingRate = 20 / (float)gun.getFiringDelay();

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Bullet Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("Bullet Damage")).replaceAll(" ", "");

					try {
						dmg = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.contains("Firing Rate")) {
					String valueSubstring = line.substring(0, line.indexOf("/sec")).replaceAll(" ", "");

					try {
						firingRate = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}

					String dpsValue = StringUtil.roundToNthDecimalPlace(firingRate * dmg, 2);

					if (Float.parseFloat(dpsValue) == 0)
						return;

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
				}
			}
		}
		else if (item instanceof BaseBow) {
			BaseBow bow = (BaseBow)item;
			float dmg = (float)bow.getDamage();
			float firingRate = 0;

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Average Ranged Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("Average Ranged Damage")).replaceAll(" ", "");

					try {
						dmg = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.startsWith("Draw Time:")) {
					String valueSubstring = line.substring(line.indexOf(":") + 1, line.length() - 1).replaceAll(" ", "");

					try {
						firingRate = 20 / (20 * Float.parseFloat(valueSubstring));
					}
					catch (NumberFormatException ex) {}

					String dpsValue = StringUtil.roundToNthDecimalPlace(firingRate * dmg, 2);

					if (Float.parseFloat(dpsValue) == 0)
						return;

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
				}
			}
		}
		else if (item instanceof BaseBlaster) {
			BaseBlaster blaster = (BaseBlaster)item;
			float dmg = (float)blaster.getDamage();
			float firingRate = 20 / (float)blaster.getFiringDelay();
			float energyConsumption = 0;

			for (int i = 0; i < tooltipLines.size(); i++) {
				String line = tooltipLines.get(i);

				if (line.endsWith(" Blaster Damage")) {
					String valueSubstring = line.substring(0, line.indexOf("Blaster Damage")).replaceAll(" ", "");

					try {
						dmg = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}
				}
				else if (line.contains("Firing Rate")) {
					String valueSubstring = line.substring(0, line.indexOf("/sec")).replaceAll(" ", "");

					try {
						firingRate = Float.parseFloat(valueSubstring);
					}
					catch (NumberFormatException ex) {}

					String dpsValue = StringUtil.roundToNthDecimalPlace(firingRate * dmg, 2);

					if (Float.parseFloat(dpsValue) == 0)
						continue;

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltips.dps", dpsValue));
				}
				else if (line.endsWith(" Energy")) {
					String valueSubstring = line.substring(line.indexOf("Consumes") + 8, line.indexOf("Energy")).replaceAll(" ", "");

					try {
						energyConsumption = Float.parseFloat(valueSubstring);

						if (energyConsumption == 0)
							return;
					}
					catch (NumberFormatException ex) {}

					tooltipLines.set(i, line + TextFormatting.GOLD + " " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.tooltip.perSec", StringUtil.roundToNthDecimalPlace(energyConsumption, 2)));
				}
			}
		}
	}
}
