# OverwolfListener
Java application to listen for PUBG events and print them to a Discord server.

# How it Works

This application creates a web server to listen for messages sent from the accompanying Overwolf PUBG App.
When the message is received, it checks the name of the user that sent the data, and either creates a new message, or updates an already existing one within Discord with an embed message containing the PUBG data.
