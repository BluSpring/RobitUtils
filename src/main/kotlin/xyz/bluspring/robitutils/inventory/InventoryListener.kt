package xyz.bluspring.robitutils.inventory

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.inventory.InventoryView
import xyz.bluspring.robitutils.util.LicenseManager
import xyz.bluspring.robitutils.util.LicenseType
import java.util.*

object InventoryListener : Listener {
    val inventory = mutableMapOf<Player, InventoryView>()

    // This needs to check if anything within the player inventory that is opened has an expired license.
    @EventHandler
    fun onInventoryOpen(ev: InventoryOpenEvent) {
        if (inventory.containsValue(ev.view))
            return

        ev.inventory.forEach { item ->
            if (item == null)
                return@forEach

            if (item.type != Material.PAPER || !item.hasItemMeta())
                return@forEach

            if (item.itemMeta.customModelData != 2 && item.itemMeta.customModelData != 3)
                return@forEach

            if (!item.itemMeta.hasLore())
                return@forEach

            val id = item.itemMeta.lore()?.get(1) ?: return@forEach

            val plainId = PlainTextComponentSerializer.plainText().serialize(id)
            if (!LicenseManager.licenses.any { license -> license.id == plainId })
                return@forEach

            val license = LicenseManager.licenses.first { license -> license.id == plainId }

            if (System.currentTimeMillis() > license.expiryDate) {
                val meta = item.itemMeta
                // Sets the model to be the "expired" variant
                meta.setCustomModelData(meta.customModelData + 2)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = license.expiryDate

                meta.lore()?.set(0,
                    Component.translatable("item.robitpack.license.expires")
                        .color(TextColor.color(170, 0, 0))
                        .decoration(TextDecoration.ITALIC, false)
                        .args(
                            Component.text(calendar.get(Calendar.DAY_OF_MONTH)),
                            Component.text(calendar.get(Calendar.MONTH)),
                            Component.text(calendar.get(Calendar.YEAR))
                        ) as Component
                )
            }
        }
    }

    @EventHandler
    fun onInventoryClick(ev: InventoryClickEvent) {
        if (!inventory.containsValue(ev.view))
            return

        val player = ev.inventory.viewers[0] as Player

        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage("${ChatColor.RED}Your inventory is full!")

            return
        }

        if (ev.currentItem != null) {
            if (ev.currentItem?.itemMeta?.hasCustomModelData() != true)
                return

            when (ev.currentItem?.itemMeta?.customModelData) {
                2 -> {
                    ev.isCancelled = true
                    ev.inventory.close()

                    val license = LicenseManager.createLicense(LicenseType.REDSTONE_HANDLE, player.hasPermission("robitutils.forge"))
                    player.inventory.addItem(license)
                }

                3 -> {
                    ev.isCancelled = true
                    ev.inventory.close()

                    val license = LicenseManager.createLicense(LicenseType.REDSTONE_PURCHASE, player.hasPermission("robitutils.forge"))
                    player.inventory.addItem(license)
                }

                else -> {
                    ev.isCancelled = true
                    ev.inventory.close()

                    player.sendMessage("${ChatColor.RED}You somehow selected an invalid item.... I have no clue how, but good job?")

                    return
                }
            }

            player.sendMessage("${ChatColor.GREEN}Successfully made license!")
        }
    }

    @EventHandler
    fun onInventoryClose(ev: InventoryCloseEvent) {
        if (!inventory.containsKey(ev.player))
            return

        inventory.remove(ev.player)
    }
}