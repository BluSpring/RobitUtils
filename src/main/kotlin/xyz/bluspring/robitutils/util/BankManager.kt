package xyz.bluspring.robitutils.util

import org.bukkit.entity.Player
import xyz.bluspring.robitutils.RobitUtils
import java.lang.Integer.parseInt
import java.util.*

object BankManager {
    val bank = mutableMapOf<UUID, Int>()

    fun load() {
        RobitUtils.plugin.config.getStringList("bank").forEach {
            val uuid = UUID.fromString(it.split("::")[0])
            val amount = parseInt(it.split("::")[1])

            bank[uuid] = amount
        }
    }

    fun save() {
        val list = mutableListOf<String>()
        bank.forEach {
            list.add("${it.key}::${it.value}")
        }

        RobitUtils.plugin.config.set("bank", list)
        RobitUtils.plugin.saveConfig()
    }

    fun getPlayer(player: Player): Int {
        return if (!bank.containsKey(player.uniqueId)) {
            bank[player.uniqueId] = 0
            0
        } else {
            bank[player.uniqueId]!!
        }
    }
}