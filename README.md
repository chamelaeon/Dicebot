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
The basic behavior of the bot is controlled via a configuration file - this file contains options and triggers for behavior. The default configuration file is "config.properties".

Here are the basic options available. If a default is listed, then that default is used if the option is missing or can't be parsed.

- **Network**: The actual IRC network to connect to. Should not include any URL prefixes, like "http://". Default is "irc.sandwich.net".
- **Port**: The port to connect to. Default is 6697.
- **Channels:** The channels to connect to on the server. These channels will be autojoined when the bot connects, and will be treated as "permanent" channels - the bot will never attempt to leave them if they are idle.
- **Nicks**: The nicks that the bot should use on the server, separated by commas. It will attempt to use them in order, so if given "Dicesuke,Diceroshi" it will attempt to use Dicesuke. If that nick is in use, it will attempt to use Diceroshi instead.
- **NickservPassword**: The password, if any, for the bot's nickname on the server's NickServ service. No default provided.
- **UseGhostIfNickExists**: If true and if a NickservPassword has been provided, the bot will not just pick a different nick when its original nick is in use. It will instead attempt to use the GHOST command to kill the existing connection and retake its own name. This can be useful if the bot is having connection issues and is reconnecting often. Default is false.
- **SSL**: Whether to use SSL when connecting to the server or not. Default: True.
- **TrustAllCertificates**: When using SSL to connect to the server, this option allows all certificates to be trusted, even if the signing certificate is not recognized by any of the sources available to the bot. **Do not use this option if you don't actually need it.** Default: False.
- **NickservPassword**: If the bot is using a registered nick, you can provide the Nickserv password here so the bot will register itself. This is mandatory if the ghost option is enabled.
- **MotD**: The "message of the day" for the bot - it will send this to the channel this the first time it joins after being started up. Any new channel joined will receive the message, unless the bot has already joined that channel and left at some earlier point.
- **UseCriticalSuccessMessages**: In some RPG systems, getting the maximum value on a die roll (e.g. 20 on 1d20 or 12 on 2d6) is considered a "Critical Hit". If this option is turned on, the dicebot will print a critical success message when using the Standard roller or any other dice roller which supports them. The message will be picked randomly from those provided in the personality key "CriticalSuccesses".
- **UseCriticalFailureMessages**: In some RPG systems, getting the minimum value on a die roll (e.g. 1 on 1d20 or 2 on 2d6) is considered a "Critical Failure". If this option is turned on, the dicebot will print a critical failure message when using the Standard roller or any other dice roller which supports them. The message will be picked randomly from those provided in the personality key "CriticalFailures".

###Changing the bot's personality
The bot outputs a lot of text to the channels it's in as part of the rolling process, and also as part of several commands. This text is configurable via providing a "personality file", a configuration file that provides an easy way to provide custom bot texts, with the exception of the text of the !help command, which is largely fixed. The default personality file is "dicesuke.properties", which provides a surly personality to the bot. An additional personality file ("chibiVernon.properties") is included for your convenience, which provides a more cheery (if unhinged) experience. Creating or personalizing your bot's personality file is fairly easy in most cases - locate the text you want to replace in the properties file, and replace it with the text you want the bot to say. The text can include custom markers (e.g. %BADSHORT%) - that means the command will look for those markers and substitute in values (e.g. the value of the roll). If it doesn't find those markers, it will ignore them. It will not substitute values for markers that it isn't initially looking for.

Here's a list of the text keys the bot looks for, a description of when they're used, and a list of markers for that text key, if any. This list is broken up roughly by features.

- ####General Error
    - **BrokenRegexp**: Triggered when one of the regular expressions the bot uses to parse IRC lines breaks.  
    *Markers*
        - None.
    - **ParseBadShort**: Triggered when the bot is asked to parse something as a number that it can't.  
    *Markers*
        - %BADSHORT% - The string the user provided that isn't a number.
    - **BadCommand**: Triggered when something goes hideously wrong inside the dicebot. Should rarely, if ever, be seen unless you're using a development version.  
    *Markers*
        - None.
    - **ReflectionError**: Triggered when an error happens with the class structure of the dicebot. Should never be seen unless you're using a development version.  
    *Markers*
        - None.
- ####Commands
    - **Cheat**: Triggered when a player tries to use the !cheat command (which doesn't do anything but output this text).  
    *Markers*
        - None.
    - **Leave**: Triggered when the bot is told to leave a channel.  
    *Markers*
        - None.
    - **LeaveIdleChannel**: Triggered when the bot automatically leaves an idle channel.  
    *Markers*
        - None.
    - **StatusChannelCount**: Triggered when the bot is asked for its status - should tell how many channels the bot is in.  
    *Markers*
        - %CHANNELCOUNT% - The number of channels the bot is in.
    - **StatusRolledCount**: Triggered when the bot is asked for its status. It should tell how many groups and dice the bot has rolled.  
    *Markers*
        - %GROUPSROLLED% - The number of groups the bot has rolled. 
        - %DICEROLLED% - The number of actual dice the bot has rolled.
    - **JoinChannelDenied**: Triggered when the bot is asked to leave an idle channel and no channels are eligible.  
    *Markers*
        - None.
    - **DrawNonNumberCards**: Triggered when a player tries to draw an invalid number of cards.  
    *Markers*
        - None.
    - **DrawCard**: Triggered when a player draws cards without notifying anyone else.  
    *Markers*
        - %CARDCOUNT% - The number of cards drawn.
        - %NICK% - The player who drew them.
    - **DrawCardAndNotify**: Triggered when a player draws cards and notifies another user.  
    *Markers*
        - %CARDCOUNT% - The number of cards drawn.
        - %NICK% - The player who drew them.
        - %NOTIFYNICK - The user to be notified.
- ####Basic Rolling
    - **LessThanOneGroup**: Triggered when a roll is made with 0 groups (e.g. 0 6d10).  
    *Markers*
        - None.
    - **Roll0Dice**: Triggered when a roll is made with 0 dice (e.g. 0d10).  
    *Markers*
        - None.
    - **CannotSatisfyRerollSingleDie**: Triggered when a roll is made where a reroll of a die is called for, and the reroll expects a higher result than the die can give (e.g. d2b3).  
    *Markers*
        - %REROLL% - The reroll condition that can't be met. 
        - %SIDES% - The size of the die that can't meet it.
    - **CannotSatisfyRerollMultipleDice**: Triggered when a roll is made where a reroll of multiple dice is called for, and the reroll expects a higher result than each die can give (e.g. 6d2b3).  
    *Markers*
        - %REROLL% - The reroll condition that can't be met. 
        - %DICEROLLED% - The number of dice being rolled.
        - %SIDES% - The size of the die that can't meet the reroll condition.
    - **InfiniteExplosion**: Triggered when a roll is made which would explode infinitely (e.g. d6v1).  
    *Markers*
        - None.
    - **CannotSatisfySuccesses**: Triggered when the number of needed successes is less than the number of dice (e.g. 5t8).  
    *Markers*
        - %SUCCESSESNEEDED% - The number of needed successes.
        - %DICEROLLED% - The number of dice rolled.
- ####Standard Roller
    - **OneSidedDice**: Triggered when a roll is made with one or more 1-sided dice (e.g. 10d1).  
    *Markers*
        - %DICECOUNT% - The first is the number of 1-sided dice rolled. 
        - %MODIFIEDVALUE% - The second is the total of the roll (after modifiers).
    - **Roll0Sides**: Triggered when a roll is made with a 0-sided dice (e.g. 10d0).  
    *Markers*
        - None.
    - **Standard1Group**: Triggered when only one group of standard dice are rolled (e.g. 2d6).  
    *Markers*
        - %DICECOUNT% - The number of dice rolled. 
        - %DICETYPE% - The type of dice rolled. 
        - %MODIFIER% - The modifier for the roll. 
        - %BEHAVIORS% - The behaviors for the roll. 
        - %USER% - The person who made the dice roll. 
        - %NATURALVALUE% - The natural result of the dice. 
        - %MODIFIEDVALUE% - The dice result after modifiers. 
        - %ANNOTATION% - The annotation for the roll, if any.
    - **StandardMoreGroups**: Triggered when multiple groups of standard dice are rolled (e.g. 10 2d6).  
    *Markers*
        - %GROUPCOUNT% - The number of groups rolled.
        - %DICECOUNT% - The number of dice rolled.
        - %DICETYPE% - The type of dice rolled.
        - %MODIFIER% - The modifier for the roll.
        - %BEHAVIORS% - The behaviors for the roll.
        - %USER% - The person who made the dice roll.
        - %NATURALVALUE% - The natural result of the dice.
        - %MODIFIEDVALUE% - The dice result after modifiers.
        - %ANNOTATION% - The annotation for the roll, if any.
    - **Standard1GroupCrit**: Triggered when one group of standard dice are rolled and the result is a critical success or failure.  
    *Markers*
        - %GROUPCOUNT% - The number of groups rolled.
        - %DICECOUNT% - The number of dice rolled.
        - %DICETYPE% - The type of dice rolled.
        - %MODIFIER% - The modifier for the roll.
        - %BEHAVIORS% - The behaviors for the roll.
        - %USER% - The person who made the dice roll.
        - %NATURALVALUE% - The natural result of the dice.
        - %MODIFIEDVALUE% - The dice result after modifiers.
        - %ANNOTATION% - The annotation for the roll, if any.
        - %CRITICALTYPE% - "Success" or "Failure" depending on if it's a critical success or critical failure.
        - %CRITICALCOMMENT% - The critical message picked at random from the corresponding list.
- ####L5R Roller
    - **KeepingLessThan1**: Triggered when a roll is made which keeps no dice (e.g. 10k0).  
    *Markers*
        - None.
    - **RollLessThanKeep**: Triggered when a roll is made which rolls fewer dice than it keeps (e.g. 8k10).  
    *Markers*
        - None.
    - **L5ROneGroup**: Triggered when one group of L5R dice are rolled (e.g. 5k2).  
    *Markers*
        - %ROLLEDDICE% - The number of dice rolled.
        - %KEPTDICE% - The number of dice kept.
        - %MODIFIER% - The modifier for the roll.
        - %BEHAVIORS% - The behaviors for the roll.
        - %USER% - The person who made the dice roll.
        - %NATURALVALUE% - The list of all rolled dice.
        - %MODIFIEDVALUE% - The summed value of all kept dice from the pool.
        - %ANNOTATION% - The annotation for the roll, if any.
    - **L5RMoreGroups**: Triggered when multiple groups of L5R dice are rolled (e.g. 10 5k2).  
    *Markers*
        - %GROUPCOUNT% - The number of groups rolled.
        - %ROLLEDDICE% - The number of dice rolled.
        - %KEPTDICE% - The number of dice kept.
        - %MODIFIER% - The modifier for the roll.
        - %BEHAVIORS% - The behaviors for the roll.
        - %USER% - The person who made the dice roll.
        - %NATURALVALUE% - The list of all rolled dice for all groups.
        - %MODIFIEDVALUE% - The summed value of all kept dice from the pool for all groups.
        - %ANNOTATION% - The annotation for the roll, if any.
- ####White Wolf Roller
    - **DCLessThan0**: Triggered when the specified DC is less than 0.  
    *Markers*
        - None.
    - **WhiteWolfSuccess**: Triggered on every successful roll.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice. 
        - %SUCCESSESNEEDED% - The number of successes needed. 
        - %MODIFIER% - The modifier for the roll. 
        - %SPECIALIZATION% - If the roll was made with specialization. 
        - %DCSTRING% - The DC for the roll, if custom. 
        - %USER% - The user who made the dice roll. 
        - %DICEVALUE% - The result of the dice roll.
        - %SUCCESSESOVERMINIMUM% - The number of successes over the required number of successes.
        - %ONESROLLED% - The number of 1s rolled.
    - **WhiteWolfFailure**: Triggered on every failed roll.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice. 
        - %SUCCESSESNEEDED% - The number of successes needed. 
        - %MODIFIER% - The modifier for the roll. 
        - %SPECIALIZATION% - If the roll was made with specialization.
        - %DCSTRING% - The DC for the roll, if custom.
        - %USER% - The user who made the dice roll.
        - %DICEVALUE% - The result of the dice roll.
- ####Fudge Roller
    - **Fudge1Group**: Triggered when only one group of Fudge dice are rolled (e.g. 4dF).  
    *Markers*
        - %DICECOUNT% - The number of rolled dice. 
        - %MODIFIER% - The modifier for the roll. 
        - %USER% - The user who made the dice roll. 
        - %FUDGEVALUE% - The actual faces of the dice result (e.g. -, , , +). 
        - %NATURALVALUE% - The numeric result of the dice roll. 
        - %MODIFIEDVALUE% - The result of the dice after modifiers. 
        - %DESCRIPTOR% - The Fate Ladder description of the roll. 
        - %ANNOTATION% - The annotation for the roll, if any.
    - **FudgeMoreGroups**: Triggered when multiple groups of Fudge dice are rolled (e.g. 10 4dF).  
    *Markers*
        - %DICECOUNT% - The number of rolled dice.
        - %MODIFIER% - The modifier for the roll
        - %USER% - The user who made the dice roll.
        - %NATURALVALUE% - The numeric results for all groups.
        - %MODIFIEDVALUE% - The result of the all groups after modifiers. 
        - %ANNOTATION% - The annotation for the roll, if any.
- ####Shadowrun 4 Roller
    - **ShadowrunSuccess**: Triggered on a successful roll.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice. 
        - %SUCCESSESNEEDED% - The number of successes needed.
        - %MODIFIER% - The modifier for the roll.
        - %EDGE% - Whether the user used edge on the roll or not.
        - %USER% - The user who made the dice roll. 
        - %DICEVALUE% - The result of the dice roll. 
        - %SUCCESSESOVERMINIMUM% - The number of successes over the required number of successes.
    - **ShadowrunSuccessGlitch**: Triggered on a successful roll with a glitch.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice. 
        - %SUCCESSESNEEDED% - The number of successes needed. 
        - %MODIFIER% - The modifier for the roll. 
        - %EDGE% - Whether the user used edge on the roll or not. 
        - %USER% - The user who made the dice roll. 
        - %DICEVALUE% - The result of the dice roll. 
        - %SUCCESSESOVERMINIMUM% - The number of successes over the required number of successes.
    - **ShadowrunFailure**: Trigged on a failed roll.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice. 
        - %SUCCESSESNEEDED% - The number of successes needed. 
        - %MODIFIER% - The modifier for the roll. 
        - %EDGE% - Whether the user used edge on the roll or not. 
        - %USER% - The user who made the dice roll. 
        - %DICEVALUE% - The result of the dice roll.
    - **ShadowrunFailureGlitch** Triggered on a failed roll with a glitch.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice.
        - %SUCCESSESNEEDED% - The number of successes needed.
        - %MODIFIER% - The modifier for the roll.
        - %EDGE% - Whether the user used edge on the roll or not.
        - %USER% - The user who made the dice roll.
        - %DICEVALUE% - The result of the dice roll.
    - **ShadowrunFailureCriticalGlitch**: Triggered on a failed roll with a critical glitch.  
    *Markers*
        - %ROLLEDDICE% - The number of rolled dice.
        - %SUCCESSESNEEDED% - The number of successes needed.
        - %MODIFIER% - The modifier for the roll.
        - %EDGE% - Whether the user used edge on the roll or not.
        - %USER% - The user who made the dice roll.
        - %DICEVALUE% - The result of the dice roll.
- ####Criticals
    - **CriticalFailures**:The list of critical failure messages, separated by '#' symbols.
    - **CriticalSuccesses**:The list of critical success messages, separated by '#' symbols.

###Building the project from source
The dicebot is configured as a Maven project, and any maven command should work on the project out of the box. To build an
artifact that mirrors the releases, run "mvn clean package". A zip named "dicebot-X-Y-Z-bin.zip" or "dicebot-X-Y-SNAPSHOT-bin.zip" will be created in the target directory. That zip file should work the same as any downloaded release.
