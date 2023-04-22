package xyz.bluspring.robitutils

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.JsonParser
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandPermission
import dev.jorel.commandapi.arguments.IntegerArgument
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Sign
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.NotNull
import xyz.bluspring.robitutils.commands.*
import xyz.bluspring.robitutils.inventory.InventoryListener
import xyz.bluspring.robitutils.util.BankManager
import xyz.bluspring.robitutils.util.License
import xyz.bluspring.robitutils.util.LicenseManager
import java.time.LocalTime
import java.util.*

class RobitUtils : JavaPlugin() {
    override fun onLoad() {
        CommandAPICommand("deposit").apply {
            withAliases("dep")

            withSubcommand(CommandAPICommand("all").apply {
                executesPlayer(DepositAllCommand)
            })
        }.register()

        CommandAPICommand("deposit").apply {
            withAliases("dep")

            withArguments(IntegerArgument("amount"))

            executesPlayer(DepositCommand)
        }.register()

        CommandAPICommand("withdraw").apply {
            withArguments(IntegerArgument("amount"))

            executesPlayer(WithdrawCommand)
        }.register()

        CommandAPICommand("balance").apply {
            withAliases("bal", "money")

            withSubcommand(CommandAPICommand("inventory").apply {
                withAliases("inv")

                executesPlayer(InventoryBalanceCommand)
            })

            executesPlayer(BalanceCommand)
        }.register()

        CommandAPICommand("license").apply {
            withPermission("robitutils.license")

            executesPlayer(LicenseCommand)
        }.register()

        CommandAPICommand("robit_reload").apply {
            withPermission(CommandPermission.OP)

            executes(ReloadCommand)
        }.register()
    }

    override fun onEnable() {
        plugin = this
        this.saveDefaultConfig()

        this.server.pluginManager.registerEvents(InventoryListener, this)
        BankManager.load()
        LicenseManager.load()

        /*this.server.scheduler.runTaskTimer(this, Runnable {
            if (LocalTime.now().hour == 0 && LocalTime.now().minute >= 0 && (System.currentTimeMillis() - this.config.getLong("lastUpdate") >= 4.32e7)) {
                this.config.set("lastUpdate", System.currentTimeMillis())

                val httpReq = "https://discord.com/api/v9/channels/channelidhere/messages?limit=100"
                    .httpGet()
                    .header("Authorization" to "Bot whywasthereatokenhere")

                httpReq.responseString { _, response, result ->
                    when (result) {
                        is Result.Failure -> {
                            if (response.statusCode != 200) {
                                println("Failed to get Rain's randomized sign!")
                                result.getException().printStackTrace()

                                return@responseString
                            }
                        }

                        is Result.Success -> {
                            val data = JsonParser().parse(result.get()).asJsonArray

                            val messages = data.map { it.asJsonObject.get("content").asString }

                            fun choose() {
                                val chosenMessage = messages.random()

                                if (chosenMessage.startsWith("Baking Cookies")) {
                                    choose()

                                    return
                                }

                                val msgChunked = chosenMessage.chunked(15)
                                if (msgChunked.size > 4) {
                                    println("Message \"$chosenMessage\" is too long!")
                                    choose()

                                    return
                                }

                                this.config.getMapList("rainUpdateBlocks").forEach {
                                    val loc = Location.deserialize(it as @NotNull MutableMap<String, Any>)

                                    if (!loc.block.type.name.contains("SIGN"))
                                        return@forEach

                                    val state = (loc.block.state as Sign)
                                    msgChunked.forEachIndexed { i, s ->
                                          state.line(i, Component.text(s))
                                    }
                                }
                            }

                            choose()
                        }
                    }
                }.join()
            }
        }, 0L, 12_000L)*/

        this.server.scheduler.runTaskTimer(this, Runnable {
            this.server.onlinePlayers.forEach {
                it.inventory.forEach inventoryItem@{ item ->
                    if (item == null)
                        return@inventoryItem

                    if (item.type != Material.PAPER)
                        return@inventoryItem

                    if (!item.hasItemMeta())
                        return@inventoryItem

                    if (!item.itemMeta.hasCustomModelData())
                        return@inventoryItem

                    if (item.itemMeta.customModelData != 2 && item.itemMeta.customModelData != 3)
                        return@inventoryItem

                    if (!item.itemMeta.hasLore())
                        return@inventoryItem

                    val id = item.itemMeta.lore()?.get(1) ?: return@inventoryItem

                    val plainId = PlainTextComponentSerializer.plainText().serialize(id)
                    if (!LicenseManager.licenses.any { license -> license.id == plainId })
                        return@inventoryItem

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

                        item.itemMeta = meta
                        it.updateInventory()
                    }
                }
            }
        }, 0L, 36_000L)
    }

    companion object {
        lateinit var plugin: JavaPlugin

        init {
            ConfigurationSerialization.registerClass(License::class.java, "License")
        }
    }
}