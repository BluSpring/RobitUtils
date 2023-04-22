package xyz.bluspring.robitutils.commands

import dev.jorel.commandapi.executors.CommandExecutor
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import xyz.bluspring.robitutils.util.BankManager
import xyz.bluspring.robitutils.util.LicenseManager

object ReloadCommand : CommandExecutor {
    override fun run(sender: CommandSender, args: Array<out Any>) {
        BankManager.load()
        LicenseManager.load()

        sender.sendMessage("${ChatColor.GREEN}Successfully reloaded config!")
    }
}