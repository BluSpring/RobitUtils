package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.bluspring.robitutils.util.BankManager
import xyz.bluspring.robitutils.util.Utils

object DepositAllCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val inventoryCount = Utils.getPlayerArvCashInInventory(sender)

        if (inventoryCount == 0) {
            sender.sendMessage("${ChatColor.AQUA}You don't have any ArvCash!")
            return
        }

        if (BankManager.bank.containsKey(sender.uniqueId))
            BankManager.bank[sender.uniqueId] = (BankManager.bank[sender.uniqueId] ?: 0) + inventoryCount
        else
            BankManager.bank[sender.uniqueId] = inventoryCount

        BankManager.save()
        Utils.removeArvCashFromInventory(sender, -1)
        sender.sendMessage("${ChatColor.GREEN}Successfully deposited a total of ${ChatColor.AQUA}ARV $inventoryCount${ChatColor.GREEN} to your bank!")
    }
}