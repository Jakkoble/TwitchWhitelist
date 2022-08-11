package de.jakkoble

import de.jakkoble.Whitelist.isListed
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class TwitchWhitelist : JavaPlugin(), Listener {
   companion object {
      lateinit var instance: TwitchWhitelist
      val config = Config()
   }
   private val twitchBot = TwitchBot()
   override fun onEnable() {
      instance = this
      twitchBot.connect()
   }
   override fun onDisable() {
      twitchBot.disconnect()
   }
   @EventHandler
   fun onPlayerJoin(event: PlayerJoinEvent) {
      if (!event.player.isListed()) event.player.kick(Component.text(TwitchWhitelist.config.getData("notWhitelistedMessage")))
   }
}