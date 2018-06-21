package com.cryo.pubg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.Listener;
import com.google.gson.Gson;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

public class PUBG {
	
	private HashMap<Long, PUBGStats> stats;
	private HashMap<String, Long> names;
	private HashMap<Long, Long> messages;
	
	private Listener listener;
	
	private Gson gson;
	private JDA client;
	
	public PUBG(Listener listener) {
		this.listener = listener;
		load();
	}
	
	private void load() {
		gson = Listener.getGson();
		client = listener.getBot().getClient();
		loadMembers();
		loadMessages();
	}
	
	public void decodeData(String name, String key, String value) {
		if(!names.containsKey(name)) return;
		Long id = names.get(name);
		PUBGStats stats = this.stats.get(id);
		if(stats == null) return;
		switch(key) {
		case "phase":
			stats.setPhase(value);
			break;
		case "running":
			stats.setRunning(Boolean.parseBoolean(value));
			break;
		case "kills":
			stats.setKills(Integer.parseInt(value));
			break;
		case "map":
			stats.setMap(value);
			break;
		case "rank":
			stats.setRank(Integer.parseInt(value));
			break;
		case "team_members":
			break;
		case "mode":
			stats.setMode(value);
			break;
		case "total_players":
			stats.setTotalPlayers(Integer.parseInt(value));
			break;
		case "total_teams":
			stats.setTotalTeams(Integer.parseInt(value));
			break;
		case "killed_by":
			stats.setKilledBy(value);
			break;
		case "game_start":
			stats.setGameStart(Long.parseLong(value));
			break;
		case "game_end":
			stats.setGameEnd(Long.parseLong(value));
			break;
		}
		System.out.println("test");
		this.stats.put(id, stats);
		long messageId = -1;
		if(messages.containsKey(id))
			messageId = messages.get(id);
		User user = client.getUserById(id);
		MessageEmbed message = stats.getEmbedMessage(user);
		System.out.println("test");
		listener.getBot().sendMessage(user, messageId, message);
	}
	
	private Properties getProperties() {
		return listener.getProperties();
	}
	
	private void loadMembers() {
		stats = new HashMap<>();
		names = new HashMap<>();
		String json = getProperties().getProperty("users");
		HashMap<String, String> users = gson.fromJson(json, HashMap.class);
		for(String userId : users.keySet()) {
			long id = Long.parseLong(userId);
			String name = users.get(userId);
			User user = client.getUserById(id);
			if(user == null)
				continue;
			names.put(name, id);
			stats.put(id, new PUBGStats());
		}
		listener.getBot().setStatus("Watching "+users.size());
	}
	
	private void loadMessages() {
		String json = getProperties().getProperty("messages");
		HashMap<String, String> messages = gson.fromJson(json, HashMap.class);
		if(messages == null) {
			messages = new HashMap<>();
			return;
		}
		this.messages = new HashMap<>();
		for(String userId : messages.keySet())
			this.messages.put(Long.parseLong(userId), Long.parseLong(userId));
	}
	
	private void saveMessages() {
		String json = gson.toJson(messages);
		getProperties().put("messages", json);
		listener.saveProperties();
	}
	
	public void removeMessageId(long userId, long messageId) {
		if(messages.containsKey(userId)) {
			messages.remove(userId);
			saveMessages();
		}
	}
	
	public void addMessageId(long userId, long messageId) {
		messages.put(userId, messageId);
		saveMessages();
	}

}
