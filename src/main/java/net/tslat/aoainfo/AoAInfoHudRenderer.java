package net.tslat.aoainfo;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.tslat.aoa3.common.registration.EnchantmentsRegister;
import net.tslat.aoa3.common.registration.ItemRegister;
import net.tslat.aoa3.common.registration.WeaponRegister;
import net.tslat.aoa3.item.armour.AdventArmour;
import net.tslat.aoa3.item.misc.HollyArrow;
import net.tslat.aoa3.item.misc.RuneItem;
import net.tslat.aoa3.item.weapon.archergun.BaseArchergun;
import net.tslat.aoa3.item.weapon.bow.BaseBow;
import net.tslat.aoa3.item.weapon.bow.Slingshot;
import net.tslat.aoa3.item.weapon.gun.BaseGun;
import net.tslat.aoa3.item.weapon.staff.BaseStaff;
import net.tslat.aoa3.item.weapon.thrown.BaseThrownWeapon;
import net.tslat.aoa3.library.Enums;
import net.tslat.aoa3.utils.RenderUtil;
import net.tslat.aoa3.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AoAInfoHudRenderer {
	private final Minecraft mc;

	private static final ResourceLocation toastTextures = new ResourceLocation("minecraft", "textures/gui/toasts.png");

	public AoAInfoHudRenderer() {
		this.mc = Minecraft.getMinecraft();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onRenderTick(final TickEvent.RenderTickEvent ev) {
		if (mc.currentScreen == null && !mc.gameSettings.hideGUI && !mc.player.isSpectator() && AoAInfoConfig.displayAmmoHud && !mc.player.isCreative()) {
			ItemStack mainHandStack = mc.player.getHeldItemMainhand();
			ItemStack offHandStack = mc.player.getHeldItemOffhand();
			Item mainHandItem = mainHandStack.getItem();
			Item offHandItem = offHandStack.getItem();
			boolean doMainHand = false;
			boolean doOffHand = false;

			if (mainHandStack != ItemStack.EMPTY && ((mainHandItem instanceof BaseGun && !(mainHandItem instanceof BaseThrownWeapon) && mainHandItem != WeaponRegister.SPECTRAL_ARCHERGUN) || mainHandItem instanceof BaseStaff || (mainHandItem instanceof ItemBow && mainHandItem != WeaponRegister.SPECTRAL_BOW)) && (!EnchantmentsRegister.BRACE.canApply(mainHandStack) || EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.BRACE, mainHandStack) == 0))
				doMainHand = true;

			if (offHandStack != ItemStack.EMPTY && ((offHandItem instanceof BaseGun && !(offHandItem instanceof BaseThrownWeapon) && offHandItem != WeaponRegister.SPECTRAL_ARCHERGUN) || offHandItem instanceof BaseStaff || (offHandItem instanceof ItemBow && offHandItem != WeaponRegister.SPECTRAL_BOW)) && (!EnchantmentsRegister.BRACE.canApply(offHandStack) || EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.BRACE, offHandStack) > 0))
				doOffHand = true;

			if (!doMainHand && !doOffHand)
				return;

			GlStateManager.pushMatrix();
			GlStateManager.disableDepth();
			GlStateManager.scale(0.5f, 0.5f, 0.5f);
			GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
			GlStateManager.enableAlpha();

			int yOffset = 0;
			int yHeight = 0;

			mc.getTextureManager().bindTexture(toastTextures);

			if (doMainHand) {
				if (mainHandItem instanceof BaseStaff) {
					yHeight = 20 * ((BaseStaff)mainHandItem).getRunes().size();
				}
				else {
					yHeight = 20;
				}
			}

			if (doOffHand) {
				if (offHandItem instanceof BaseStaff) {
					yHeight += 20 * ((BaseStaff)offHandItem).getRunes().size() + (doMainHand ? 12 : 0);
				}
				else {
					yHeight += doMainHand ? 32 : 20;
				}
			}

			RenderUtil.drawScaledCustomSizeModalRect(0, 0, 0, 0, 160, 5, 160, 5, 256, 256);
			RenderUtil.drawScaledCustomSizeModalRect(0, 5, 0, 5, 160, 5, 160, 13 + yHeight, 256, 256);
			RenderUtil.drawScaledCustomSizeModalRect(0,  13 + yHeight, 0, 27, 160, 5, 160, 5, 256, 256);

			if (doMainHand) {
				if (mainHandItem instanceof BaseStaff) {
					yOffset += renderRunesGroup("Main Hand", mainHandStack, ((BaseStaff)mainHandItem).getRunes(), yOffset);
				}
				else if (mainHandItem instanceof Slingshot) {
					yOffset += renderSlingshotGroup("Main Hand", mainHandStack, yOffset);
				}
				else if (mainHandItem instanceof BaseBow || mainHandItem instanceof BaseArchergun) {
					yOffset += renderArrowGroup("Main Hand", mainHandStack, yOffset, false);
				}
				else if (mainHandItem instanceof ItemBow) {
					yOffset += renderArrowGroup("Main Hand", mainHandStack, yOffset, true);
				}
				else {
					yOffset += renderGunGroup("Main Hand", mainHandStack, yOffset);
				}
			}

			if (doOffHand) {
				if (offHandItem instanceof BaseStaff) {
					renderRunesGroup("Offhand", offHandStack, ((BaseStaff)offHandItem).getRunes(), yOffset);
				}
				else if (offHandItem instanceof Slingshot) {
					renderSlingshotGroup("Offhand", offHandStack, yOffset);
				}
				else if (offHandItem instanceof BaseBow || offHandItem instanceof BaseArchergun) {
					renderArrowGroup("Offhand", offHandStack, yOffset, false);
				}
				else if (mainHandItem instanceof ItemBow) {
					renderArrowGroup("Offhand", offHandStack, yOffset, true);
				}
				else {
					renderGunGroup("Offhand", offHandStack, yOffset);
				}
			}

			GlStateManager.disableAlpha();
			GlStateManager.popMatrix();
		}
	}

	public int renderRunesGroup(String title, ItemStack staff, HashMap<RuneItem, Integer> runeMap, int yOffset) {
		RenderUtil.drawCenteredScaledString(mc.fontRenderer, title, 80, yOffset + 8, 1.0f, Enums.RGBIntegers.WHITE, RenderUtil.StringRenderType.OUTLINED);

		HashMap<RuneItem, Integer> availableRuneMap = new HashMap<RuneItem, Integer>(runeMap.size());

		ItemStack checkStack1;
		ItemStack checkStack2;

		if ((checkStack1 = mc.player.getHeldItemMainhand()).getItem() instanceof RuneItem) {
			if (runeMap.containsKey(checkStack1.getItem()))
				availableRuneMap.compute((RuneItem)checkStack1.getItem(), (key, val) -> val == null ? checkStack1.getCount() : val + checkStack1.getCount());
		}

		if ((checkStack2 = mc.player.getHeldItemOffhand()).getItem() instanceof RuneItem) {
			if (runeMap.containsKey(checkStack2.getItem()))
				availableRuneMap.compute((RuneItem)checkStack2.getItem(), (key, val) -> val == null ? checkStack2.getCount() : val + checkStack2.getCount());
		}

		for (int i = 0; i < mc.player.inventory.getSizeInventory(); ++i) {
			ItemStack stack = mc.player.inventory.getStackInSlot(i);

			if (runeMap.containsKey(stack.getItem()))
				availableRuneMap.compute((RuneItem)stack.getItem(), (key, val) -> val == null ? stack.getCount() : val + stack.getCount());
		}

		int archMage = EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.ARCHMAGE, staff);
		boolean nightmareArmour = true;
		int greed = EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.GREED, staff);

		for (int i = 0; i < mc.player.inventory.armorInventory.size(); i++) {
			ItemStack armourItem = mc.player.inventory.armorInventory.get(i);

			if (armourItem.getItem().getEquipmentSlot(armourItem) == EntityEquipmentSlot.HEAD) {
				if (!(armourItem.getItem() instanceof AdventArmour) || (((AdventArmour)armourItem.getItem()).setType() != Enums.ArmourSets.ALL && ((AdventArmour)armourItem.getItem()).setType() != Enums.ArmourSets.NIGHTMARE)) {
					nightmareArmour = false;

					break;
				}
			}
			else if (!(armourItem.getItem() instanceof AdventArmour) || ((AdventArmour)armourItem.getItem()).setType() != Enums.ArmourSets.NIGHTMARE) {
				nightmareArmour = false;

				break;
			}
		}

		int runeNum = 0;

		for (Map.Entry<RuneItem, Integer> entry : runeMap.entrySet()) {
			int amount = Math.max(1, entry.getValue() + greed * 2 - archMage - (nightmareArmour ? 1 : 0));
			int available = availableRuneMap.getOrDefault(entry.getKey(), 0);

			mc.getRenderItem().renderItemIntoGUI(new ItemStack(entry.getKey()), 10, yOffset + 18 + runeNum * 20);
			RenderUtil.drawOutlinedText(mc.fontRenderer, StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.amount", String.valueOf(amount)) + "      " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.available", String.valueOf(available)), 26, yOffset + 22 + runeNum * 20, available >= amount ? Enums.RGBIntegers.WHITE : Enums.RGBIntegers.RED, 1.0f);
			runeNum++;
		}

		return runeNum * 20 + 12;
	}

	public int renderGunGroup(String title, ItemStack weapon, int yOffset) {
		RenderUtil.drawCenteredScaledString(mc.fontRenderer, title, 80, yOffset + 8, 1.0f, Enums.RGBIntegers.WHITE, RenderUtil.StringRenderType.OUTLINED);

		Item ammoItem = Items.AIR;
		int ammoCount = 0;
		ItemStack checkStack;
		List<String> tooltip = new ArrayList<String>();

		tooltip.add("");

		weapon.getItem().addInformation(weapon, mc.player.world, tooltip, ITooltipFlag.TooltipFlags.NORMAL);

		for (String line : tooltip) {
			if (line.contains("Ammo: ")) {
				String ammo = line.substring(line.indexOf(":") + 2);

				switch (ammo) {
					case "Bullets":
						ammoItem = ItemRegister.LIMONITE_BULLET;
						break;
					case "Discharge Capsule":
						ammoItem = ItemRegister.DISCHARGE_CAPSULE;
						break;
					case "Seeds":
						ammoItem = Items.WHEAT_SEEDS;
						break;
					case "Spreadshot":
						ammoItem = ItemRegister.SPREADSHOT;
						break;
					case "Cannonballs":
						ammoItem = ItemRegister.CANNONBALL;
						break;
					case "Metal Slug":
					case "Metal Slugs":
						ammoItem = ItemRegister.METAL_SLUG;
						break;
					case "Cobblestone":
						ammoItem = ItemBlock.getItemFromBlock(Blocks.COBBLESTONE);
						break;
					case "Grenades":
						ammoItem = WeaponRegister.GRENADE;
						break;
					case "Carrot":
						ammoItem = Items.CARROT;
						break;
					case "Balloon":
						ammoItem = ItemRegister.BALLOON;
						break;
					case "Chili":
						ammoItem = ItemRegister.CHILLI;
						break;
					case "Nether Wart":
						ammoItem = Items.NETHER_WART;
						break;
					case "Leather Boots":
						ammoItem = Items.LEATHER_BOOTS;
						break;
					default:
						ammoItem = Items.AIR;
						break;
				}
			}
		}

		if ((checkStack = mc.player.getHeldItemMainhand()).getItem() == ammoItem)
			ammoCount += checkStack.getCount();

		if ((checkStack = mc.player.getHeldItemOffhand()).getItem() == ammoItem)
			ammoCount += checkStack.getCount();

		for (ItemStack stack : mc.player.inventory.mainInventory) {
			if (stack.getItem() == ammoItem)
				ammoCount += stack.getCount();
		}

		int greed = EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.GREED, weapon);

		mc.getRenderItem().renderItemIntoGUI(new ItemStack(ammoItem), 10, yOffset + 18);
		RenderUtil.drawOutlinedText(mc.fontRenderer, StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.amount", String.valueOf(1 + greed)) + "      " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.available", String.valueOf(ammoCount)), 26, yOffset + 22, ammoCount > 0 ? Enums.RGBIntegers.WHITE : Enums.RGBIntegers.RED, 1.0f);

		return 32;
	}

	public int renderArrowGroup(String title, ItemStack weapon, int yOffset, boolean isVanillaBow) {
		RenderUtil.drawCenteredScaledString(mc.fontRenderer, title, 80, yOffset + 8, 1.0f, Enums.RGBIntegers.WHITE, RenderUtil.StringRenderType.OUTLINED);

		ItemStack ammoItem = ItemStack.EMPTY;
		int ammoCount = 0;
		ItemStack checkStack;
		Class<? extends ItemArrow> ammoClass = isVanillaBow ? ItemArrow.class : HollyArrow.class;

		if (ammoClass.isInstance((checkStack = mc.player.getHeldItemMainhand()).getItem())) {
			ammoItem = checkStack;
			ammoCount = checkStack.getCount();
		}

		if (ammoClass.isInstance((checkStack = mc.player.getHeldItemOffhand()).getItem())) {
			if (ammoItem == ItemStack.EMPTY)
				ammoItem = checkStack;

			if (checkStack.getItem() == ammoItem.getItem())
				ammoCount += checkStack.getCount();
		}

		for (ItemStack stack : mc.player.inventory.mainInventory) {
			if (stack != ItemStack.EMPTY && ammoClass.isInstance(stack.getItem())) {
				if (ammoItem == ItemStack.EMPTY)
					ammoItem = stack;

				if (stack.getItem() == ammoItem.getItem())
					ammoCount += stack.getCount();
			}
		}

		if (ammoItem == ItemStack.EMPTY)
			ammoItem = new ItemStack((isVanillaBow ? Items.ARROW : ItemRegister.HOLLY_ARROW));

		int greed = EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.GREED, weapon);

		mc.getRenderItem().renderItemIntoGUI(ammoItem, 10, yOffset + 18);
		RenderUtil.drawOutlinedText(mc.fontRenderer, StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.amount", String.valueOf(1 + greed)) + "      " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.available", String.valueOf(ammoCount)), 26, yOffset + 22, ammoCount > 0 ? Enums.RGBIntegers.WHITE : Enums.RGBIntegers.RED, 1.0f);

		return 32;
	}

	public int renderSlingshotGroup(String title, ItemStack weapon, int yOffset) {
		RenderUtil.drawCenteredScaledString(mc.fontRenderer, title, 80, yOffset + 8, 1.0f, Enums.RGBIntegers.WHITE, RenderUtil.StringRenderType.OUTLINED);

		ItemStack ammoItem = ItemStack.EMPTY;
		int ammoCount = 0;
		ItemStack checkStack;

		if ((checkStack = mc.player.getHeldItemMainhand()).getItem() == ItemRegister.POP_SHOT || checkStack.getItem() == Items.FLINT) {
			ammoItem = checkStack;
			ammoCount = checkStack.getCount();
		}

		if ((checkStack = mc.player.getHeldItemOffhand()).getItem() == ItemRegister.POP_SHOT || checkStack.getItem() == Items.FLINT) {
			if (ammoItem == ItemStack.EMPTY)
				ammoItem = checkStack;

			if (checkStack.getItem() == ammoItem.getItem())
				ammoCount += checkStack.getCount();
		}

		for (ItemStack stack : mc.player.inventory.mainInventory) {
			if (stack != ItemStack.EMPTY && (stack.getItem() == ItemRegister.POP_SHOT || stack.getItem() == Items.FLINT)) {
				if (ammoItem == ItemStack.EMPTY)
					ammoItem = stack;

				if (stack.getItem() == ammoItem.getItem())
					ammoCount += stack.getCount();
			}
		}

		if (ammoItem == ItemStack.EMPTY)
			ammoItem = new ItemStack(ItemRegister.POP_SHOT);

		int greed = EnchantmentHelper.getEnchantmentLevel(EnchantmentsRegister.GREED, weapon);

		mc.getRenderItem().renderItemIntoGUI(ammoItem, 10, yOffset + 18);
		RenderUtil.drawOutlinedText(mc.fontRenderer, StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.amount", String.valueOf(1 + greed)) + "      " + StringUtil.getLocaleStringWithArguments("gui.aoainfo.ammoHud.available", String.valueOf(ammoCount)), 26, yOffset + 22, ammoCount > 0 ? Enums.RGBIntegers.WHITE : Enums.RGBIntegers.RED, 1.0f);

		return 32;
	}
}
