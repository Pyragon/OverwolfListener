package com.cryo.pubg;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

@Data
public class PUBGStats {
	
	public PUBGStats() {
		kills = -1;
		rank = -1;
		totalPlayers = -1;
		totalTeams = -1;
		gameStart = -1;
		gameEnd = -1;
	}
	
	private String phase;
	
	private boolean running;
	
	private int kills;
	
	private String map;
	
	private String[] teamMembers;
	
	private String mode;
	
	private int totalPlayers;
	
	private int totalTeams;
	
	private int rank;
	
	private String killedBy;
	
	private long gameEnd;
	
	private long gameStart;
	
	public MessageEmbed getEmbedMessage(User user) {
		EmbedBuilder builder = new EmbedBuilder();
		
		builder.setTitle("PUBG Stats", "http://github.com/Pyragon/pubg-over");
		builder.setDescription("Contains information about "+user.getName()+"'s current or last PUBG game.");
		builder.setAuthor(user.getName(), null, user.getEffectiveAvatarUrl());
		builder.setTimestamp(Instant.now());
		builder.setColor(new Color(255, 255, 255));
		builder.setFooter("Stats Last Updated", user.getEffectiveAvatarUrl());
		
		SimpleDateFormat format = new SimpleDateFormat("EEEE, mmmm dS, yyyy - hh:mm:ss a");

		String startTime = gameStart == -1 ? "" : format.format(new Date(gameStart));
		String endTime = gameEnd == -1 ? "" : format.format(new Date(gameEnd));
		
		builder.addField("Game Status:", running ? "Running" : "Not Running", true);
		
		if((gameStart != -1 && gameEnd == -1) && running)
			builder.addField("Match Status:", "Possibly In-Game. Started: "+startTime, true);
		else if((gameStart == -1 && gameEnd == -1) && running)
			builder.addField("Match Status:", "Not in a game", true);
		else if(running) {
			if(gameStart > gameEnd)
				builder.addField("Match Status:", "Possibly In-Game. Started: "+startTime, true);
			else
				builder.addField("Match Status:", "Not In Game. Started: "+startTime+" Ended: "+endTime, true);
		}
		
		if(phase != null)
			builder.addField("Phase:", phase, true);
		
		if(kills != -1)
			builder.addField("Kills:", Integer.toString(kills), true);
		
		if(map != null)
			builder.addField("Map:", map, true);
		
		if(teamMembers != null)
			builder.addField("Team Members:", Arrays.toString(teamMembers), true);
		
		if(mode != null)
			builder.addField("Mode:", mode, true);
		
		if(totalPlayers != -1)
			builder.addField("Total Players:", Integer.toString(totalPlayers), true);
		
		if(totalTeams != -1)
			builder.addField("Total Teams:", Integer.toString(totalTeams), true);
		
		if(rank != -1)
			builder.addField("Rank:", Integer.toString(rank), true);
		
		if(killedBy != null)
			builder.addField("Killed By:", killedBy, true);
		
		return builder.build();
	}

}
