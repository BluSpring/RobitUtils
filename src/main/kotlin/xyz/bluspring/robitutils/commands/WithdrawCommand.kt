package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.bluspring.robitutils.util.BankManager
import xyz.bluspring.robitutils.util.Utils

object WithdrawCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val amount = args[0] as Int
        val bankTotal = BankManager.getPlayer(sender)

        if (bankTotal == 0) {
            CommandAPI.fail("Your bank is empty!")
            return
        }

        if (amount > bankTotal) {
            CommandAPI.fail("The amount specified ($amount) is larger than the amount in your bank ($bankTotal)!")
            return
        }

        val withdrawn = Utils.addArvCashToInventory(sender, amount)
        BankManager.bank[sender.uniqueId] = (BankManager.bank[sender.uniqueId] ?: 0) - (amount - withdrawn)
        BankManager.save()

        sender.sendMessage("${ChatColor.GREEN}Successfully withdrew ${ChatColor.AQUA}ARV ${amount - withdrawn} ${ChatColor.GREEN}from your bank!")
        if (withdrawn > 0)
            sender.sendMessage("${ChatColor.YELLOW}(Your inventory appears to be full, so we left a total of ${ChatColor.AQUA}ARV $withdrawn ${ChatColor.YELLOW}in your bank.)")
    }
}