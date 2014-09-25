Wombot Dicebot
==============

This project is a Java-based IRC Dicebot. It is designed to be run as a program on a host computer, using a properties file to identify the server it should connect to, channels it should join on that server, and other various options about its behavior.If you find a bug or have a request for a feature, go right ahead and file an issue here on GitHub about it. Thanks for taking a look!

This project requires the Java 8 runtime.

###Downloading the bot
The latest version of the bot can be downloaded from the project's [release page](https://github.com/chamelaeon/Dicebot/releases). Pick the "dicebot-X-Y-Z-bin.zip" file to download, where X-Y-Z is the version of the bot you want to get (e.g. 1-1-0). Unzip the file to somewhere safe.

###Running the bot
You will need Java 8 installed and on your classpath to continue. Unzip the bundle to wherever you want to save it. Inside the base directory are three directories: bin, etc, and repo. Bin contains the actual executable files to use to run the bot - use "dicebot" for Linux or Mac machines and "dicebot.bat" for Windows machines. By default the scripts point to configuration files in the "etc" directory, but they can be edited to point to any file you like. The files it uses are "dicesuke.properties" and "dramaCards.properties". 

###Important features
- **Rejoin**: After being disconnected, the bot will always attempt to rejoin a server if it can. This means that after being serverkicked, it will try to hop back on again if it can. This can be viewed as server abuse depending on the server!
- **Idle Channels**: If the bot detects a channel limit on the server, it keeps track of how many channels it is in. If it is asked to join a new channel and it is at its channel limit, it will attempt to leave idle channels. Idle channels are defined as channels in which the bot has not responded to a dice roll or other command within one hour. The bot keeps track of how long the channels have been idle, and will always leave the most idle channel first - so if it is in two idle channels, and channel #A has been idle for 1 hour, and channel #B has been idle for two hours, it will leave channel #B.

###Options
The basic behavior and personality of the bot is controlled via "personality files" - configuration files that contain options and triggers for behavior, as well as most if not all of the bot's actual visible output to the IRC server. The default personality file is "dicesuke.properties", which provides a surly personality to the bot. An additional personality file ("chibiVernon.properties") is included for your convenience, which provides a more cheery (if unhinged) experience.

Here are the basic options available. If a default is listed, then that default is used if the option is missing or can't be parsed.

- **Network**: The actual IRC network to connect to. Should not include any URL prefixes, like "http://". Default is "irc.sandwich.net".
- **Port**: The port to connect to. Default is 6697.
- **Channels:** The channels to connect to on the server. These channels will be autojoined when the bot connects, and will be treated as "permanent" channels - the bot will never attempt to leave them if they are idle.
- **Nicks**: The nicks that the bot should use on the server, separated by commas. It will attempt to use them in order, so if given "Dicesuke,Diceroshi" it will attempt to use Dicesuke. If that nick is in use, it will attempt to use Diceroshi instead.
- TODO: ghost
- **SSL**: Whether to use SSL when connecting to the server or not. Default: True.
- **TrustAllCertificates**: When using SSL to connect to the server, this option allows all certificates to be trusted, even if the signing certificate is not recognized by any of the sources available to the bot. **Do not use this option if you don't actually need it.** Default: False.
- **NickservPassword**: If the bot is using a registered nick, you can provide the Nickserv password here so the bot will register itself. This is mandatory if the ghost option is enabled.
- **MotD**: The "message of the day" for the bot - it will send this to the channel this the first time it joins after being started up. Any new channel joined will receive the message, unless the bot has already joined that channel and left at some earlier point.
- **UseCriticalSuccessMessages**: In some RPG systems, getting the maximum value on a die roll (e.g. 20 on 1d20 or 12 on 2d6) is considered a "Critical Hit". If this option is turned on, the dicebot will print a critical success message when using the Standard roller or any other dice roller which supports them. The message will be picked randomly from those provided in the personality key "CriticalSuccesses".
- **UseCriticalFailureMessages**: In some RPG systems, getting the minimum value on a die roll (e.g. 1 on 1d20 or 2 on 2d6) is considered a "Critical Failure". If this option is turned on, the dicebot will print a critical failure message when using the Standard roller or any other dice roller which supports them. The message will be picked randomly from those provided in the personality key "CriticalFailures".

###Changing the bot's personality
The bot outputs a lot of text to the channels it's in as part of the rolling process, and also as part of several commands. Most of this text is configurable, with the exception of the text of the !help command, which is largely fixed. Changing this text is fairly easy in most cases - locate the text you want to replace in the properties file, and replace it with the text you want the bot to say. If the text includes markers like %d or %s, that means the command has values which are substituted in for those markers. Here's a list of the text keys the bot looks for, a description of when they're used, and a list of markers for that text key, if any. This list is broken up roughly by features.

- ####General Error
    - **BrokenRegexp**: Triggered when one of the regular expressions the bot uses to parse IRC lines breaks. *Markers*: None.
    - **ParseBadShort**: Triggered when the bot is asked to parse something as a number that it can't. *Markers*: %s - The string the user provided that isn't a number.
    - **BadCommand**: Triggered when something goes hideously wrong inside the dicebot. Should rarely, if ever, be seen unless you're using a development version.
    - **ReflectionError**: Triggered when an error happens with the class structure of the dicebot. Should never be seen unless you're using a development version. 
- ####Commands
    - **Cheat**: Triggered when a player tries to use the !cheat command (which doesn't do anything but output this text). *Markers*: None.
    - **Leave**: Triggered when the bot is told to leave a channel. *Markers*: None.
    - **LeaveIdleChannel**: Triggered when the bot automatically leaves an idle channel. *Markers*: None.
    - **StatusChannelCount**: Triggered when the bot is asked for its status - should tell how many channels the bot is in. *Markers*: %d. The number of channels the bot is in.
    - **StatusRolledCount**: Triggered when the bot is asked for its status - should tell how many groups and dice the bot has rolled. *Markers*: %d, %d. In order: The number of groups the bot has rolled, the number of actual dice the bot has rolled.
    - **JoinChannelDenied**: Triggered when the bot is asked to leave an idle channel and no channels are eligible. *Markers*: None.
    - **DrawNonNumberCards**: Triggered when a player tries to draw an invalid number of cards. *Markers*: None.
    - **DrawCard**: Triggered when a player draws cards without notifying anyone else. *Markers*: %d, %s. In order: The number of cards drawn, the player who drew them.
    - **DrawCardAndNotify**: Triggered when a player draws cards and notifies another user. *Markers*: %d, %s, %s. In order: The number of cards drawn, the player who drew them, the user to be notified.
- ####Basic Rolling
    - **LessThanOneGroup**: Triggered when a roll is made with 0 groups (e.g. 0 6d10). *Markers*: None.
    - **Roll0Dice**: Triggered when a roll is made with 0 dice (e.g. 0d10). *Markers*: None.
    - **CannotSatisfyRerollSingleDie**: Triggered when a roll is made where a reroll of a die is called for, and the reroll expects a higher result than the die can give (e.g. d2b3). *Markers*: %s, %s. In order: The reroll condition that can't be met, the size of the die that can't meet it.
    - **CannotSatisfyRerollMultipleDice**: Triggered when a roll is made where a reroll of multiple dice is called for, and the reroll expects a higher result than each die can give (e.g. 6d2b3). *Markers*: %s, %d, %s. In order: The reroll condition that can't be met, the number of dice being rolled, the size of the die that can't meet the reroll condition.
    - **InfiniteExplosion**: Triggered when a roll is made which would explode infinitely (e.g. d6v1). *Markers*: None.
- ####Standard Roller
    - **OneSidedDice**: Triggered when a roll is made with one or more 1-sided dice (e.g. 10d1). *Markers*: Two %d markers - the first is the number of 1-sided dice rolled, and the second is the total of the roll (after modifiers).
    - **Roll0Sides**: Triggered when a roll is made with a 0-sided dice (e.g. 10d0). *Markers*: None.
    - **Standard1Group**: Triggered when only one group of standard dice are rolled (e.g. 2d6). *Markers* %s, %s, %s, %s. In order: The dice roll made, the person who made the dice roll, the natural result of the dice, the dice result after modifiers.
    - **StandardMoreGroups**: Triggered when multiple groups of standard dice are rolled (e.g. 10 2d6). *Markers*: %s, %s, %s, %s. In order: The dice roll made, the person who made the dice roll, the natural results for all groups, the roll results after modifiers for all groups.
    - **Standard1GroupCrit**: Triggered when one group of standard dice are rolled and the result is a critical success or failure. *Markers*: %s, %s, %s, %s, %s, %s. In order: The dice roll made, the person who made the dice roll, the natural result of the dice, the dice result after modifiers, "Success" or "Failure" depending on if it's a critical success or critical failure, the critical message picked at random from the corresponding list.
- ####L5R Roller
    - **KeepingLessThan1**: Triggered when a roll is made which keeps no dice (e.g. 10k0). *Markers*: None.
    - **RollLessThanKeep**: Triggered when a roll is made which rolls fewer dice than it keeps (e.g. 8k10). *Markers*: None.
    - **L5ROneGroup**: Triggered when one group of L5R dice are rolled (e.g. 5k2). *Markers*: %s, %s, %s, %s. In order: The dice roll made, the person who made the dice roll, the list of all rolled dice, the summed value of all kept dice from the pool.
    - **L5RMoreGroups**: Triggered when multiple groups of L5R dice are rolled (e.g. 10 5k2). *Markers*: %s, %s, %s, %s. In order: The dice roll made, the person who made the dice roll, the list of all rolled dice for all groups, the summed value of all kept dice from the pool for all groups.
- ####White Wolf Roller
    - **CannotSatisfySuccesses**: Triggered when the number of needed successes is less than the number of dice (e.g. 5t8). *Markers*: Two %d markers - the first is the number of needed successes, and the second is the number of dice.
    - **DCLessThan0**: Triggered when the specified DC is less than 0. *Markers*: None.
    - **WhiteWolfSuccess**: Triggered on every successful roll. *Markers*: %s, %s, %s, %d, %d. In order: The dice roll made, the user who made the dice roll, the result of the dice roll, the number of successes, the number of 1s rolled.
    - **WhiteWolfFailure**: Triggered on every failed roll. *Markers*: %s, %s, %s. In order: The dice roll made, the user who made the dice roll, the result of the dice roll.
- ####Fudge Roller
    - **Fudge1Group**: Triggered when only one group of Fudge dice are rolled (e.g. 4dF). *Markers*: %s, %s, %s, %s, %s, %s. In order: The dice roll made, the user who made the dice roll, the numeric result of the dice roll, the actual faces of the dice result (e.g. -, , , +), the result of the dice after modifiers, the Fate Ladder description of the roll.
    - **FudgeMoreGroups**: Triggered when multiple groups of Fudge dice are rolled (e.g. 10 4dF). *Markers*: %s, %s, %s, %s. In order: The dice roll made, the user who made the dice roll, the results for all groups, the actual faces of the dice for all groups.
- ####Criticals
    - **CriticalFailures**:The list of critical failure messages, separated by commas.
    - **CriticalSuccesses**:The list of critical success messages, separated by commas.

###Building the project from source
The dicebot is configured as a Maven project, and any maven command should work on the project out of the box. To build an
artifact that mirrors the releases, run "mvn clean package". A zip named "dicebot-X-Y-Z-bin.zip" or "dicebot-X-Y-SNAPSHOT-bin.zip" will be created in the target directory. That zip file should work the same as any downloaded release.
