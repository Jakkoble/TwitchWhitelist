[![CodeFactor](https://www.codefactor.io/repository/github/jakkoble/twitchwhitelist/badge)](https://www.codefactor.io/repository/github/jakkoble/twitchwhitelist)
# TwitchWhitelist
This is a customisable 1.8-1.21 Spigot Plugin to **Whitelist Players** via **Twitch Channel Points Rewards**.
<br />
<br />

## Setup
1. Setup a **PaperMC (recommended)** or **SpigotMC** Minecraft Server like **[here](https://docs.papermc.io/paper/getting-started#downloading-paper)**
2. Download the **[latest Release File](https://github.com/jakkoble/TwitchWhitelist/releases/latest)**
3. Put the downloaded **TwitchWhitelist.jar** into the `/plugins/` folder of the created PaperMC Server
4. Start the Server via your Start Script (`start.bat` or `start.sh`)
5. After the Server finished starting, stop it (Type `stop` in Console)
6. Navigate in your `/plugin/` folder and then in the `/TwitchWhitelist/` folder
7. Open the `config.yml` File set it up for your needs (You must set the `token` and `channelID` => See **[Further Information](#further-information)**)
8. Start the Server again and everything should work, have fun :)
<br />

## Configuration Notes
`enabled` — Whether the plugin is enabled or not (Possible Values: true, false)<br />
`token` — [Further Information](#further-information)<br />
`channelID` — [Further Information](#further-information)<br />
`offlineServer` —  Whether you are running an offline server or not (Possible Values: true, false)<br />
`ticketsPerUser` — Amount of tickets, a single user can redeem. (Possible Values: >= 1)<br />
`serverName` — This name will be printed within the successMesssage, alreadyWhitelistedMessage and tooManyPlayersWhitelisted Message<br />
`channelRewardName` — The name of your Twitch Channel Points Reward (see [Channel Reward Setup](#channel-reward-setup))<br />
`sendMessage` — Whether the plugin should respond on redeems on your Twitch channel (Possible Values: true, false)<br />
`successMessage` — Message for a successful whitelist action (on Twitch)<br />
`playerNotFoundMessage` — Message when a player with the given name was not found (on Twitch)<br />
`alreadyWhitelistedMessage` — Message when a player with the given name is already whitelisted<br />
`tooManyPlayersWhitelisted` — Message when a twitch user has already reached the Ticket Limit (ticketsPerUser Entry)<br />
`notWhitelistedMessage` — Message that gets displayed when a player tries to connect to the server and he is not whitelisted yet<br />

**%s is a placeholder for dynamic parts (e. g. player name, server name) => do not mix the order if there are multiple placeholders**
<br />
<br />

## Channel Reward Setup
In Order the Bot listens to the right Channel Points Reward, be sure to setup a Channel Points Reward and put the **Name of the Reward** in the `config.yml` under `chanelRewardName`. 

Be sure to **enable** the Option `Require Viewer to Enter Text` in the Channel Points Reward Edit Menu.

**[One Example](https://i.imgur.com/7CFZNzM.png)**: Name of the Channel Points Reward would be **Minecraft Whitelist Ticket**.
<br />
<br />

## Whitelist Command
Permission: `whitelist.cmd`
#### Commands
`whitelist on` — Enable the Whitelist on your Server <br />
`whitelist off` — Disable the Whitelist on your Server <br />
`whitelist list` — Get an overview over all Whitelisted Players <br />
`whitelist add playerName` — Add a Player to the Whitelist manually by the Minecraft Player Name <br />
`whitelist remove playerName` — Remove a Player from the Whitelist by the Minecraft Player Name <br />
<br />
<br />


## Further Information
You can either use a second Twitch Account or your own Twitch Account for the `token`. You can get your **Bot Chat Token** => Access Token **[here](https://twitchtokengenerator.com/)**. 

The `channelID` is the ID of your Twitch Account, get it **[here](https://www.streamweasels.com/tools/convert-twitch-username-to-user-id/)**.

**Attention: By enabeling Offline Servers the Plugin will not work on Online Servers until you disable it again.** <br />
You open yourself an security risk with offline servers. To solve this use third party solutions like **[AuthMeReloaded](https://www.spigotmc.org/resources/authmereloaded.6269/)**.
<br />
<br />


If you need further help, feel free to **[open a new Issue](https://github.com/Jakkoble/TwitchWhitelist/issues/new)**. In case you like my project, please give this Repository a ⭐.
