package de.jakkoble

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class WhitelistCommand : CommandExecutor, TabCompleter {
   override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
      if (args.isEmpty()) {
         sender.sendUsage()
         return true
      }
      when(args[0]) {
         "on" -> {
            if (enabled) {
               sendPlayerMessage(sender, "$prefix TwitchWhitelist is already turned on.")
               return true
            }
            Config().setData(ConfigEntry.ENABLED, true)
            enabled = true
            sendPlayerMessage(sender, "$prefix You have enabled the TwitchWhitelist.")
            TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_AQUA}Whitelist is now turned on.")
            return true
         }
         "off" -> {
            if (!enabled) {
               sendPlayerMessage(sender, "$prefix TwitchWhitelist is already turned off.")
               return true
            }
            Config().setData(ConfigEntry.ENABLED, false)
            enabled = false
            sendPlayerMessage(sender, "$prefix You have disabled the TwitchWhitelist.")
            TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.DARK_AQUA}Whitelist is now turned off.")
            return true
         }
         "list" -> {
            sender.sendMessage("")
            val playerNames = Whitelist().playerNames()
            if (playerNames.isEmpty()) sender.sendMessage("No Player is Whitelisted")
            else {
               sender.sendMessage("${ChatColor.GOLD}There ${if(playerNames.size == 1) "is one Player" else "are ${playerNames.size} Players"} whitelisted.")
               val message = StringBuilder()
               playerNames.forEach {
                  if (Whitelist().userDataByName(it)?.uuid == "OFFLINEPLAYER") message.append("(Offline Player) ")
                  message.append(if (playerNames.indexOf(it) != playerNames.size - 1) "$it, " else it)
               }
               sender.sendMessage(message.substring(0))
            }
            sender.sendMessage("")
            return true
         }
      }
      if (args.size != 2) {
         sender.sendUsage()
         return true
      }
      val playerName = args[1]
      when(args[0]) {
         "add" -> {
            if (offlineServer) {
               if (!Whitelist().whitelist(UserData(
                     name = playerName,
                     uuid = "OFFLINEPLAYER",
                     twitchUserID = channelID
                  ))) {
                  sendPlayerMessage(sender, "$prefix The Player $playerName is already Whitelisted. (Offline Server)")
                  TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted. (Offline Server)")
                  return true
               }
               sendPlayerMessage(sender, "$prefix You have added $playerName to the Whitelist. (Offline Server)")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist. (Offline Server)")
               return true
            }
            val userData = playerName.getUserDataFromName()
            if (userData == null || playerName.length > 25) {
               sendPlayerMessage(sender, "$prefix There is no Player called $playerName")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $playerName.")
               return true
            }
            if (Whitelist().whitelist(UserData(
                  name = playerName,
                  uuid = userData.id,
                  twitchUserID = channelID))) {
               sendPlayerMessage(sender, "$prefix You have added $playerName to the Whitelist.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist.")
            } else {
               sendPlayerMessage(sender, "$prefix The Player $playerName is already Whitelisted.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted.")
            }
         }
         "remove" -> {
            val userData = Whitelist().userDataByName(playerName)
            if (userData == null || playerName.length > 25) {
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $playerName.")
               sendPlayerMessage(sender, "$prefix There is no Player called $playerName")
               return true
            }
            if (Whitelist().unwhitelist(userData.uuid, userData.name)) {
               sendPlayerMessage(sender, "$prefix You have removed $playerName from the Whitelist.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.RED}Player $playerName removed from Whitelist.")
            } else {
               sendPlayerMessage(sender, "$prefix The Player $playerName is not Whitelisted.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is not Whitelisted.")
            }
         }
      }
      return true
   }
   private fun CommandSender.sendUsage() {
      sendMessage("")
      sendMessage("${ChatColor.GOLD}Usage")
      sendMessage("whitelist on")
      sendMessage("whitelist off")
      sendMessage("whitelist list")
      sendMessage("whitelist add playerName")
      sendMessage("whitelist remove playerName")
      sendMessage("")
   }
   override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
      val tabList = mutableListOf<String>()
      if (args.size == 1) {
         tabList.addAll(listOf("on", "off", "list", "add", "remove"))
         return tabList
      } else if (args.size == 2 && (args[0] == "remove"))
         tabList.addAll(Whitelist().playerNames())
      return tabList
   }
   private fun sendPlayerMessage(sender: CommandSender, message: String) {
      if (sender !is Player) return
      sender.sendMessage(message)
   }
}