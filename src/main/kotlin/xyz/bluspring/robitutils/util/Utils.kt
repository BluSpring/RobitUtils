package xyz.bluspring.robitutils.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import kotlin.math.min

object Utils {
    fun getPlayerArvCashInInventory(player: Player): Int {
        var count = 0

        if (player.inventory.isEmpty)
            return 0

        player.inventory.storageContents.forEach {
            if (it == null)
                return@forEach

            if (it.type != Material.PAPER)
                return@forEach

            if (!it.itemMeta.hasCustomModelData() || it.itemMeta.customModelData != 1)
                return@forEach

            count += it.amount
        }

        return count
    }

    fun addArvCashToInventory(player: Player, total: Int): Int {
        val itemStack = ItemStack(Material.PAPER, total)
        val meta = itemStack.itemMeta
        meta.displayName(Component.translatable("item.robitpack.arvcash").decoration(TextDecoration.ITALIC, false))
        meta.setCustomModelData(1)
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        itemStack.itemMeta = meta

        val success = player.inventory.addItem(itemStack)

        var count = 0
        success.values.forEach {
            count += it.amount
        }

        return count
    }

    fun removeArvCashFromInventory(player: Player, total: Int) {
        if (total == -1) {
            player.inventory.contents.forEach {
                if (it == null)
                    return@forEach

                if (!it.itemMeta.hasCustomModelData())
                    return@forEach

                if (it.type == Material.PAPER && it.itemMeta.customModelData == 1) {
                    player.inventory.removeItem(it)
                }
            }

            return
        }

        var newAmount = total

        player.inventory.contents.forEach {
            if (it == null)
                return@forEach

            if (it.type == Material.PAPER && it.itemMeta.customModelData == 1) {
                val amountToRemove = min(it.amount, newAmount)

                if (amountToRemove == it.amount) {
                    player.inventory.removeItem(it)
                } else {
                    it.amount = it.amount - amountToRemove
                }

                newAmount -= amountToRemove
                if (newAmount <= 0)
                    return
            }
        }
    }

    private fun calculateTotalInStacks(total: Int): MutableList<Int> {
        val stacks = mutableListOf<Int>()
        var current = total

        for (i in 0..(total / 64)) {
            if (current - 64 < 0)
                stacks.add(current)
            else {
                current -= 64
                stacks.add(64)
            }
        }

        return stacks
    }
}