package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import xyz.bluspring.robitutils.util.BankManager
import xyz.bluspring.robitutils.util.Utils

object DepositCommand : PlayerCommandExecutor {
    override fun run(sender: Player, args: Array<out Any>) {
        val inventoryCount = Utils.getPlayerArvCashInInventory(sender)
        val amount = args[0] as Int

        if (amount < 0) {
            CommandAPI.fail("You gave a negative value!")
            return
        }

        if (amount > inventoryCount) {
            CommandAPI.fail("The value you gave (${args[0]}) is higher than the total Arvs in your inventory ($inventoryCount)!")
            return
        }

        if (BankManager.bank.containsKey(sender.uniqueId))
            BankManager.bank[sender.uniqueId] = (BankManager.bank[sender.uniqueId] ?: 0) + amount
        else
            BankManager.bank[sender.uniqueId] = amount

        Utils.removeArvCashFromInventory(sender, amount)

        BankManager.save()
        sender.sendMessage("${ChatColor.GREEN}Successfully deposited a total of ${ChatColor.AQUA}ARV $amount${ChatColor.GREEN} to your bank!")
    }
}