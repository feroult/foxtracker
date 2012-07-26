package br.com.foxtracker;


import com.google.gson.Gson;


public class JsonProvider {
	
	public static String toJson(TrackData o){
		Gson gson = new Gson();
		return gson.toJson(o);
	}
	
	public static TrackData fromJson(String json){
		Gson gson = new Gson();
		TrackData d = gson.fromJson(json, TrackData.class);
		return d;
	}

}
