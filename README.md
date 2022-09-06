[![CodeFactor](https://www.codefactor.io/repository/github/jakkoble/twitchwhitelist/badge)](https://www.codefactor.io/repository/github/jakkoble/twitchwhitelist)
# TwitchWhitelist
This is a customisable PaperMC Plugin to **Whitelist Players** via **Twitch Channel Points Rewards**.
</br>
## Setup
1. Setup a PaperMC Minecraft Server like **[here](https://docs.papermc.io/paper/getting-started)**
2. Download the **[latest Release File](https://github.com/jakkoble/TwitchWhitelist/releases/latest)**
3. Put the downloaded **TwitchWhitelist.jar** into the `/plugins/` folder of the created PaperMC Server
4. Start the Server via your Start Script (`start.bat` or `start.sh`)
5. After the Server finished starting, stop it (Type `stop` in Console)
6. Navigate in your `/plugin/` folder and then in the `/TwitchWhitelist/` folder
7. Open the config.yml File set it up for your needs (You must set the `token` and `channelID` => See **[Further Information](#further-information)**)
8. Start the Server again and everything should work, have fun :)
</br>

## Channel Reward Setup
In Order the Bot listens to the right Channel Points Reward, be sure to setup a Channel Points Reward and put the **Name of the Reward** in the `config.yml` under `chanelRewardName`. 

Be sure to **enable** the Option `Require Viewer to Enter Text` in the Channel Points Reward Edit Menu.

**[One Example](https://i.imgur.com/7CFZNzM.png)**: Name of the Channel Points Reward would be **Minecraft Whitelist Ticket**.
</br>
</br>

## Whitelist Command
Permission: `whitelist.cmd`
#### Commands
`whitelist on` — Enable the Whitelist on your Server </br>
`whitelist off` — Disable the Whitelist on your Server </br>
`whitelist list` — Get an overview over all Whitelisted Players </br>
`whitelist add playerName` — Add a Player to the Whitelist manually by the Minecraft Player Name </br>
`whitelist remove playerName` — Remove a Player from the Whitelist by the Minecraft Player Name </br>
</br>


## Further Information
You can use either a second Twitch Account or your own Twitch Account for the `token`. You can get your **Bot Chat Token** **[here](https://twitchtokengenerator.com/)**. 

The `channelID` is the ID of your Twitch Account, get it **[here](https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/)**.

This Plugin works with a Custom Whitelist, so Vanila Whitelist must be turned off with `whitelist off` in Console.
</br>
</br>


If you need further help, feel free to **[open a new Issue](https://github.com/Jakkoble/TwitchWhitelist/issues/new)**. In case you like my project, please give this Repository a ⭐.
