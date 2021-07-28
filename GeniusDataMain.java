package com.songdata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.opencsv.exceptions.CsvException;

public class GeniusDataMain {

	public static void main(String[] args) throws IOException, InterruptedException{
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		
		OpenCsvReader reader = new OpenCsvReader();
		String[] ids = chartRequest("\"id\":");
		String[] titles = chartRequest("\"title\":");
		String[] artists = chartRequest("\"name\":");
		    	
		    	try {
			    	String file1 = "c:\\Users\\Winnie\\Downloads\\Genius Count Database.csv"; 
			    	String file2 = "c:\\Users\\Winnie\\Downloads\\Genius Ranking Database.csv";
			    	    	
			    	OpenCsvReader database = new OpenCsvReader(); 
					int numSongs = database.findNumRows(file1);
					
					LocalDate today = LocalDate.now();
					LocalDate yesterday = today.minusDays(1);
			    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
			    	
			    	String[] oldSongs = database.yesterdaysSongs(file2, formatter.format(yesterday), 1); //yesterdays song IDs
			    	String[] oldTitles = database.yesterdaysSongs(file2, formatter.format(yesterday), 2);
			    	String[] oldArtists = database.yesterdaysSongs(file2, formatter.format(yesterday), 3);
			    	
			    	int numOldSongs = oldSongs.length;
			    	System.out.println("old songs:"+ numOldSongs);
			    	int numNewSongs = 0;
			    	for (int i=0; i<200; i++){
			    		int search = database.searchArray(oldSongs, oldSongs.length, ids[i]);
			    		if (search < 0) {
			    			numNewSongs++;
			    		}
			    	}
			    	System.out.println("new songs:" + numNewSongs);
			    	int numSongsToday = numOldSongs + numNewSongs;
			    	String[] ranks = new String[numSongsToday];
			    	for (int k = 0; k < numSongsToday; k++)
					{
						ranks[k] = "";
					}  	  	
			    	
			    	String[] newSongs = new String[numNewSongs];
			    	String[] newTitles = new String[numNewSongs];
			    	String[] newArtists = new String[numNewSongs];
			    	
			    	int index = 0;
			    	int len = numOldSongs;
			    	for (int i=0; i<200; i++){ //Searches for songs in daily top 200 that are not already in database
			    		System.out.println(len);
			    		int search = database.searchArray(oldSongs, numOldSongs, ids[i]);
						if (search < 0) { 
							ranks[len] = Integer.toString(i+1);
							len++;
							newSongs[index] = ids[i];
							newTitles[index] = titles[i];
							newArtists[index] = artists[i];
							index++;
							//database.addSong(file2, ids[i] + "," + songNames[i] + "," + artists[i] + "," + formatter.format(today) + "," + Integer.toString(i+1)); //rank
							}
						else {
							ranks[search] = Integer.toString(i+1);
							//database.addSong(file2, ids[i] + "," + songNames[i] + "," + artists[i] + "," + formatter.format(today) + "," + Integer.toString(i+1)); //rank
						}
			    	}
			    	for (int k = 0;k < numOldSongs; k++)
			    	{
			    		database.addSong(file1, formatter.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + synchronousRequest(oldSongs[k]));
			    	}
			    	for (int j = 0; j < numNewSongs; j++)
			    	{
			    		database.addSong(file1, formatter.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + synchronousRequest(newSongs[j]));
			    	}

			    	System.out.println("oldsongs:" + numOldSongs);
			    	System.out.println("newsongs:" + numNewSongs);
			    	for (int l = 0; l < numOldSongs; l++)
			    	{
			    		System.out.println(ranks[l]);
			    		database.addSong(file2, formatter.format(today) + "," + oldSongs[l] + "," + oldTitles[l] + "," + oldArtists[l] + "," + ranks[l]);
			    	}
			    	for (int m = 0; m < numNewSongs; m++)
			    	{
			    		System.out.println(ranks[m]);
			    		database.addSong(file2, formatter.format(today) + "," + newSongs[m] + "," + newTitles[m] + "," + newArtists[m] + "," + ranks[m]);
			    	}
			    	
		    	}	
		    	catch (FileNotFoundException e) {
		    		e.printStackTrace();
		    	}
		    	catch (IOException e){
					e.printStackTrace();
		    	}
		    	catch(CsvException e) {
		    		e.printStackTrace();
		    	}
		    	
		    	
		    }

		    private static String synchronousRequest(String id) throws IOException, InterruptedException {
		        // create a client
		        var client = HttpClient.newHttpClient();

		        // create a request
		        var request = HttpRequest.newBuilder(
		            URI.create("api.genius.com/annotations/"+ id)
		        ).build();

		        // use the client to send the request
		        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		        		//new JsonBodyHandler<>(APOD.class)); 

		        // the response:
		      //  System.out.println(response);
		 //      System.out.println(response.body());        
		    		String[] tokens = response.body().split("\"");

		    		String str = tokens[6];
		    		String shazams = str.substring(1, str.length() - 1);
//		    		System.out.println(shazams);
		    		return shazams;
		    
		    }
		    
		    private static String[] chartRequest(String find) throws IOException, InterruptedException
		    {
		    	var client = HttpClient.newHttpClient();

		        // create a request
		        var request1 = HttpRequest.newBuilder(
		            URI.create("https://genius.com/api/songs/chart?time_period=day&chart_genre=all&page=1&per_page=50&text_format=html%2Cmarkdown")
		        ).build();
		        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
		        
		        var request2 = HttpRequest.newBuilder(
			         URI.create("https://genius.com/api/songs/chart?time_period=day&chart_genre=all&page=1&per_page=50&text_format=html%2Cmarkdown")
			    ).build();
			    HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
			        
		        var request3 = HttpRequest.newBuilder(
			         URI.create("https://genius.com/api/songs/chart?time_period=day&chart_genre=all&page=1&per_page=50&text_format=html%2Cmarkdown")
			    ).build();
			    HttpResponse<String> response3 = client.send(request3, HttpResponse.BodyHandlers.ofString());
			        		        
		        var request4 = HttpRequest.newBuilder(
			        URI.create("https://genius.com/api/songs/chart?time_period=day&chart_genre=all&page=1&per_page=50&text_format=html%2Cmarkdown")
			    ).build();
			    HttpResponse<String> response4 = client.send(request4, HttpResponse.BodyHandlers.ofString());
			     	        
		        String[] substrings1 = response1.body().split(find);
		        String[] substrings2 = response2.body().split(find);
		        String[] substrings3 = response3.body().split(find);
		        String[] substrings4 = response4.body().split(find);
		        String[] songList = new String[200];
		        
		        for (int i = 0; i<50; i++)
		        {
		        	String str = substrings1[i+1];
		        	String[] tokens = str.split("\"");
		        	String str1;
		        	if (find.equals("\"id\":")){
		        		String str2 = tokens[0];
		        		str1 = str2.substring(0, str2.length() - 1);
		        	}
		        	else {
		        		str1 = tokens[1];
		        	}
		        	if (str1.contains(",")) {
		        		songList[i] = "\"" + str1 + "\"";
		        	}
		        	else {
		        		songList[i] = str1;
		        	}
		        }
		        for (int i = 0; i<50; i++)
		        {
		        	String str = substrings2[i+1];
		        	String[] tokens = str.split("\"");
		        	String str1;
		        	if (find.equals("\"id\":")){
		        		String str2 = tokens[0];
		        		str1 = str2.substring(0, str2.length() - 1);
		        	}
		        	else {
		        		str1 = tokens[1];
		        	}
		        	if (str1.contains(",")) {
		        		songList[50 + i] = "\"" + str1 + "\"";
		        	}
		        	else {
		        		songList[50 + i] = str1;
		        	}
		        }
		        for (int i = 0; i<50; i++)
		        {
		        	String str = substrings3[i+1];
		        	String[] tokens = str.split("\"");
		        	String str1;
		        	if (find.equals("\"id\":")){
		        		String str2 = tokens[0];
		        		str1 = str2.substring(0, str2.length() - 1);
		        	}
		        	else {
		        		str1 = tokens[1];
		        	}
		        	if (str1.contains(",")) {
		        		songList[100 + i] = "\"" + str1 + "\"";
		        	}
		        	else {
		        		songList[100 + i] = str1;
		        	}
		        }
		        for (int i = 0; i<50; i++)
		        {
		        	String str = substrings4[i+1];
		        	String[] tokens = str.split("\"");
		        	String str1;
		        	if (find.equals("\"id\":")){
		        		String str2 = tokens[0];
		        		str1 = str2.substring(0, str2.length() - 1);
		        	}
		        	else {
		        		str1 = tokens[1];
		        	}
		        	if (str1.contains(",")) {
		        		songList[150 + i] = "\"" + str1 + "\"";
		        	}
		        	else {
		        		songList[150 + i] = str1;
		        	}
		        }

		        return songList;
		    }
		    
		    

}
