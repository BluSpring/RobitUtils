package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.executors.PlayerCommandExecutor
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import xyz.bluspring.robitutils.inventory.InventoryListener

object LicenseCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val container = Bukkit.createInventory(null, 27, Component.text("License-o-tron"))

        val purchasingLicense = ItemStack(Material.PAPER)
        val purchasingLicenseMeta = purchasingLicense.itemMeta

        purchasingLicenseMeta.setCustomModelData(3)
        purchasingLicenseMeta.displayName(Component.translatable("item.robitpack.license_redstone_purchase").decoration(TextDecoration.ITALIC, false))

        purchasingLicense.itemMeta = purchasingLicenseMeta

        container.setItem(10, purchasingLicense)

        val handlingLicense = ItemStack(Material.PAPER)
        val handlingLicenseMeta = purchasingLicense.itemMeta

        handlingLicenseMeta.setCustomModelData(2)
        handlingLicenseMeta.displayName(Component.translatable("item.robitpack.license_redstone_handle").decoration(TextDecoration.ITALIC, false))

        handlingLicense.itemMeta = handlingLicenseMeta

        container.setItem(16, handlingLicense)

        // The rest of the command will be handled in the InventoryListener class.
        InventoryListener.inventory[sender] = sender.openInventory(container)!!
    }
}