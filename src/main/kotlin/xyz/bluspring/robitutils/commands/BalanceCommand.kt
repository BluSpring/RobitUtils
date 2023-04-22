package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.bluspring.robitutils.util.BankManager

object BalanceCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val total = BankManager.getPlayer(sender)
        sender.sendMessage("${ChatColor.GREEN}You currently have a total of ${ChatColor.AQUA}ARV $total ${ChatColor.GREEN}in your bank.")
    }
}