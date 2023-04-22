package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.bluspring.robitutils.util.Utils

object InventoryBalanceCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val total = Utils.getPlayerArvCashInInventory(sender)
        sender.sendMessage("${ChatColor.GREEN}You currently have a total of ${ChatColor.AQUA}ARV $total ${ChatColor.GREEN}in your inventory.")
    }
}