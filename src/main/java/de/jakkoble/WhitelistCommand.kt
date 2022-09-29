package de.jakkoble

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class WhitelistCommand : CommandExecutor, TabCompleter {
   override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
      if (args.isEmpty()) {
         sender.sendUsage()
         return true
      }
      when(args[0]) {
         "on" -> {
            if (enabled) {
               sender.sendMessage("$prefix TwitchWhitelist is already turned on.")
               return true
            }
            Config().setData(ConfigEntry.ENABLED, true)
            sender.sendMessage("$prefix You have enabled the TwitchWhitelist.")
            return true
         }
         "off" -> {
            if (!enabled) {
               sender.sendMessage("$prefix TwitchWhitelist is already turned off.")
               return true
            }
            Config().setData(ConfigEntry.ENABLED, false)
            sender.sendMessage("$prefix You have disabled the TwitchWhitelist.")
            return true
         }
         "list" -> {
            sender.sendMessage("")
            val playerNames = Whitelist().playerNames()
            if (playerNames.isEmpty()) sender.sendMessage("No Player is Whitelisted")
            else {
               sender.sendMessage("${ChatColor.GOLD}There ${if(playerNames.size == 1) "is one Player" else "are ${playerNames.size} Players"} whitelisted.")
               val message = StringBuilder()
               playerNames.forEach { message.append(if (playerNames.indexOf(it) != playerNames.size - 1) "$it, " else it) }
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
            val userData = Whitelist().userDataByName(playerName)
            if (userData == null || playerName.length > 25) {
               sender.sendMessage("$prefix There is no Player called $playerName")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $playerName.")
               return true
            }
            if (Whitelist().whitelist(UserData(
                  name = playerName,
                  uuid = userData.uuid,
                  twitchUserID = channelID))) {
               sender.sendMessage("$prefix You have added $playerName to the Whitelist.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.GREEN}Added Player $playerName to the Whitelist.")
            } else {
               sender.sendMessage("$prefix The Player $playerName is already Whitelisted.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is already Whitelisted.")
            }
         }
         "remove" -> {
            val userData = Whitelist().userDataByName(playerName)
            if (userData == null || playerName.length > 25) {
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}There is no Player called $playerName.")
               sender.sendMessage("$prefix There is no Player called $playerName")
               return true
            }
            if (Whitelist().unwhitelist(userData.uuid)) {
               sender.sendMessage("$prefix You have removed $playerName from the Whitelist.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.RED}Player $playerName removed from Whitelist.")
            } else {
               sender.sendMessage("$prefix The Player $playerName is not Whitelisted.")
               TwitchWhitelist.INSTANCE.server.consoleSender.sendMessage("${ChatColor.YELLOW}Player $playerName is not Whitelisted.")
            }
         }
      }
      return true
   }
   private fun CommandSender.sendUsage() {
      sendMessage("")
      sendMessage("$prefix ${ChatColor.YELLOW}Usage")
      sendMessage("$prefix whitelist on")
      sendMessage("$prefix whitelist off")
      sendMessage("$prefix whitelist list")
      sendMessage("$prefix whitelist add playerName")
      sendMessage("$prefix whitelist remove playerName")
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
}