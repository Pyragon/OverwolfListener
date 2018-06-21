package com.cryo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import com.cryo.discord.DiscordBot;
import com.cryo.pubg.PUBG;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.Getter;
import spark.Spark;

public class Listener {
	
	private @Getter Properties properties;
	
	private @Getter DiscordBot bot;
	
	private @Getter PUBG pubg;
	
	private static @Getter Gson gson;
	
	public static void main(String[] args) {
		new Listener().run();
	}
	
	private void run() {
		gson = buildGson();
		loadProperties();
		bot = new DiscordBot(this);
		pubg = new PUBG(this);
		Spark.port(8090);
		CorsFilter.apply();
		Spark.get("/pubg/:name/:action/:value", (req, res) -> {
			String name = req.params(":name");
			String action = req.params(":action");
			String value = req.params(":value");
			pubg.decodeData(name, action, value);
			return success();
		});
	}
	
	private String success() {
		Properties prop = new Properties();
		prop.put("success", true);
		return gson.toJson(prop);
	}
	
	private void loadProperties() {
		File file = new File("props.json");
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String json = reader.readLine();
			properties = gson.fromJson(json, Properties.class);
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveProperties() {
		File file = new File("props.json");
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(gson.toJson(properties));
			writer.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Gson buildGson() {
		Gson gson = new GsonBuilder()
				.serializeNulls()
				.setVersion(1.0)
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.create();
		return gson;
	}

}
