# Chat Bot
An external chat bot for Minecraft that runs from console made in Java.

## Features
```
Auto-Reconnect: Automatically reconnect to the server if you get disconnected
Auto-Register/Login: Automatically use /register and /login with a password set in the config
Greentext: Turn Greentext chat on/off for the bot
Customizable prefix: Customize the prefix in the config.cfg file
```

## How to use
```
1. Put your login info file in the same directory as the jar (default login.txt)
2. Make sure your login info is set to email on the first line, password on the second
3. Set the account type in config.cfg. (MSA for Microsoft and MOJ for Mojang)
3. Open terminal and type 'java -jar ChatBot.jar <ip>/<ip:port>'
```

## Issues
Due to my parsing of Minecraft's chat messages being bad, the bot may not be able to read messages on some servers, and some messages on working servers might not come through, depending on formatting.
I am working to create a better chat parser to address this

## Commands
Below are a list of commands currently in ChatBot (Assuming default prefix of "!")

###### 8ball -question-
Ask a question to the magic 8 ball
###### bible / pray
Print a random bible verse in chat
###### isEven -number-
Check if a number is even using the isEven API
###### report
Report a player for bad behavior
###### tps
Get the current server TPS
###### cum
Make the bot cum
###### help
Links to here
