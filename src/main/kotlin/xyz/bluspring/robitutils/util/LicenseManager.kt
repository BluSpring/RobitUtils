package xyz.bluspring.robitutils.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import xyz.bluspring.robitutils.RobitUtils
import java.util.*

object LicenseManager {
    val licenses = mutableListOf<License>()

    fun save() {
        RobitUtils.plugin.config.set("licenses", licenses)

        RobitUtils.plugin.saveConfig()
    }

    fun load() {
        licenses.clear()

        RobitUtils.plugin.config.getMapList("licenses").forEach {
            licenses.add(License.deserialize(it as MutableMap<String, Any>))
        }
    }

    fun createLicense(type: LicenseType, forged: Boolean = false): ItemStack {
        val license = License(
            type,
            System.currentTimeMillis() + (1.21e9).toLong(),
            forged,
            UUID.randomUUID().toString()
        )

        licenses.add(license)
        save()

        val itemStack = ItemStack(Material.PAPER)
        val itemMeta = itemStack.itemMeta

        if (type == LicenseType.REDSTONE_HANDLE) {
            itemMeta.setCustomModelData(2)
            if (!forged || (Random()).nextInt(100) == 0)
                itemMeta.displayName(Component.translatable("item.robitpack.license_redstone_handle").decoration(TextDecoration.ITALIC, false))
            else
                itemMeta.displayName(Component.translatable("item.robitpack.license_redstone_handle.${Random().nextInt(7) + 1}").decoration(TextDecoration.ITALIC, false))
        } else if (type == LicenseType.REDSTONE_PURCHASE) {
            itemMeta.setCustomModelData(3)

            if (!forged || (Random()).nextInt(100) == 0)
                itemMeta.displayName(Component.translatable("item.robitpack.license_redstone_purchase").decoration(TextDecoration.ITALIC, false))
            else
                itemMeta.displayName(Component.translatable("item.robitpack.license_redstone_purchase.${Random().nextInt(7) + 1}").decoration(TextDecoration.ITALIC, false))
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS)

        if (!forged)
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 15, true)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = license.expiryDate

        itemMeta.lore(
            mutableListOf(
                Component.translatable("item.robitpack.license.expires", TextColor.color(85, 255, 85))
                    .args(
                        Component.text("${calendar.get(Calendar.DAY_OF_MONTH)}"),
                        Component.text("${calendar.get(Calendar.MONTH)}"),
                        Component.text("${calendar.get(Calendar.YEAR)}")
                    ).decoration(TextDecoration.ITALIC, false) as Component,

                Component.text(license.id, TextColor.color(0, 170, 0)).decoration(TextDecoration.ITALIC, false) as Component
            )
        )

        itemStack.itemMeta = itemMeta

        return itemStack
    }
}