package com.cryo.discord;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.security.auth.login.LoginException;

import com.cryo.Listener;
import com.cryo.pubg.PUBGStats;
import com.google.gson.Gson;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.requests.RestAction;

public class DiscordBot {
	
	private @Getter JDA client;
	
	private Listener listener;
	
	private boolean ready;
	
	private String status;
	
	private Properties properties;
	
	private static @Getter Gson gson;
	
	private String channelId;
	
	public DiscordBot(Listener listener) {
		this.listener = listener;
		load();
	}
	
	private void load() {
		try {
			gson = Listener.getGson();
			properties = listener.getProperties();
			client = new JDABuilder(AccountType.BOT).setToken(properties.getProperty("token")).buildBlocking();
			status = "Loading bot...";
			channelId = properties.getProperty("channel_id");
			ready();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendMessage(User user, long messageId, MessageEmbed embed) {
		if(messageId != -1) {
			RestAction<Message> action = client.getTextChannelById(channelId).getMessageById(messageId);
			Message message = action.complete();
			if(message == null) {
				listener.getPubg().removeMessageId(user.getIdLong(), messageId);
				return;
			}
			message.editMessage(embed).complete();
			return;
		}
		Message message = client.getTextChannelById(channelId).sendMessage(embed).complete();
		listener.getPubg().addMessageId(user.getIdLong(), message.getIdLong());
	}
	
	private void ready() {
		ready = true;
		changeStatus();
	}
	
	public void setStatus(String status) {
		this.status = status;
		changeStatus();
	}
	
	private void changeStatus() {
		if(!ready) return;
		String status = this.status;
		if(!status.contains("Loading"))
			status += " | .stats";
		client.getPresence().setGame(Game.playing(status));
	}

}
