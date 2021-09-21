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

public class SoundCloudDataMain {

	public static void main(String[] args) throws IOException, InterruptedException{
		String file1 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Playcount Database.csv";
		String file3 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Like Database.csv";
    	String file5 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Comment Database.csv";
    	String file7 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Ranking Database.csv";
		
    	String file2 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Playcount Database.csv";
    	String file4 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Like Database.csv";
    	String file6 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Comment Database.csv";
    	String file8 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Ranking Database.csv";
    	
    	String file = "c:\\Users\\Winnie\\Downloads\\Testing.csv";
    	    	
 //   	dailyUpdate(file1, file3, file5, file7, true);
    	
    	dailyUpdate(file2, file4, file6, file8, false);	
	}
	private static void dailyUpdate(String file1, String file2, String file3, String file4, boolean chartType) throws InterruptedException, IOException{
				
		int count = chartCount(chartType);
		System.out.println("chart count:" + count);
		
		String[] ids = chartRequest("\"kind\":\"track\"", chartType);
    	String[] songNames = chartRequest("\"title\":", chartType);
    	String[] artists = chartRequest("\"username\":", chartType); //"user_id"
    	
    	
    	String[] isrcCodes = chartRequest("\"isrc\":", chartType);
    	
    	try {
    	    	
	    	OpenCsvReader database = new OpenCsvReader(); 
			int numSongs = database.findNumRows(file1);
			
			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.minusDays(1);
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	    	
	    	System.out.println(formatter.format(yesterday));
	    	
	    	String[] oldSongs = database.yesterdaysSongs(file1, formatter.format(yesterday), 1); //yesterdays song IDs
	    	String[] oldTitles = database.yesterdaysSongs(file1, formatter.format(yesterday), 2);
	    	String[] oldArtists = database.yesterdaysSongs(file1, formatter.format(yesterday), 3);
	    	String[] oldIsrcs = database.yesterdaysSongs(file1, formatter.format(yesterday), 4);
	    	
	    	int numOldSongs = oldSongs.length;
	    	System.out.println("old songs:"+ numOldSongs);
	    	int numNewSongs = 0;
	    	for (int i=0; i< count; i++){
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
	    	String[] newIsrcs = new String[numNewSongs];
	    	
	    	int index = 0;
	    	int len = numOldSongs;
	    	for (int i=0; i<count; i++){ //Searches for songs in daily top 100 that are not already in database
	    		int search = database.searchArray(oldSongs, numOldSongs, ids[i]);
				if (search < 0) { 
					ranks[len] = Integer.toString(i+1);
					len++;
					newSongs[index] = ids[i];
					newTitles[index] = songNames[i];
					newArtists[index] = artists[i];
					newIsrcs[index] = isrcCodes[i];
					index++;
					}
				else {
					ranks[search] = Integer.toString(i+1);
				}
	    	}
	    	System.out.println("oldsongs:" + numOldSongs);
	    	System.out.println("newsongs:" + numNewSongs);
		    	for (int k = 0;k < numOldSongs; k++)
		    	{
		    		System.out.println(oldIsrcs[k]);
		    		database.addSong(file1, formatter.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + oldIsrcs[k] + "," + synchronousRequest(oldSongs[k], "\"playback_count\":"));// + oldIsrcs[k]));
		    		database.addSong(file2, formatter.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + oldIsrcs[k] + "," + synchronousRequest(oldSongs[k], "\"likes_count\":")); // + oldIsrcs[k]));
		    		database.addSong(file3, formatter.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + oldIsrcs[k] + "," + synchronousRequest(oldSongs[k], "\"comment_count\":")); //+ oldIsrcs[k]));
		    		database.addSong(file4, formatter.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + oldIsrcs[k] + "," + ranks[k]); // + oldIsrcs[k])
		    	}
		    	for (int j = 0; j < numNewSongs; j++)
		    	{
		    		System.out.println(newIsrcs[j]);
		    		database.addSong(file1, formatter.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + newIsrcs[j] + "," + synchronousRequest(newSongs[j], "\"playback_count\":")); // + newIsrcs[j]));
		    		database.addSong(file2, formatter.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + newIsrcs[j] + "," + synchronousRequest(newSongs[j], "\"likes_count\":")); // + newIsrcs[j]));
		    		database.addSong(file3, formatter.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + newIsrcs[j] + "," + synchronousRequest(newSongs[j], "\"comment_count\":")); // + newIsrcs[j]));
		    		database.addSong(file4, formatter.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + newIsrcs[j] + "," + ranks[j]); // + newIsrcs[j]);
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

    private static String synchronousRequest(String id, String find) throws IOException, InterruptedException {
        // create a client
        var client = HttpClient.newHttpClient();

        // create a request
        
         //https://api-v2.soundcloud.com/tracks?ids=1080488065&client_id=aVfBEvueQiX0L8YGQTo3mm5vQJhZfseT&%5Bobject%20Object%5D=&app_version=1625669469&app_locale=en
        var request = HttpRequest.newBuilder(
            URI.create("https://api-v2.soundcloud.com/tracks?ids=" + id + "&client_id=B31E7OJEB3BxbSbJBHarCQOhvKZUY09J&%5Bobject%20Object%5D=&app_version=1631196797&app_locale=en")
        ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        		//new JsonBodyHandler<>(APOD.class)); 
        
      //  System.out.println(response.body());
        
        	String[] tokens1 = response.body().split(find);
        	System.out.println(response.body());
        	System.out.println(id);
        	if (response.body().equals("[]")) {
        		return "";
        	}
        	if (response.body().equals("{}")) {
        		return "";
        	}
        	String str1 = tokens1[1];
        	String[] tokens2 = str1.split(",");
        	String count = tokens2[0];
        	return count;    	 
    }
    
    private static String[] chartRequest(String find, boolean chartType) throws IOException, InterruptedException
    {
    	int count = chartCount(chartType);
    	var client = HttpClient.newHttpClient();    	
    	// chartType: true = Top 50, false = New & Hot
    	String[] songList = new String[count];
    	
        if (chartType == true){
        	var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=top&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=B31E7OJEB3BxbSbJBHarCQOhvKZUY09J&limit=100&offset=0&linked_partitioning=1&app_version=1631196797&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens1 = response.body().split(find);
        	if (find.equals("\"kind\":\"track\"")) {
        		for (int i = 0; i<count; i++) {
            		String str = tokens1[i];
            		String[] tokens2 = str.split(",");
            		int size = tokens2.length;
	           		String str1 = tokens2[size-1];
	           		String str2 = str1.substring(5);
                	if (str2.contains(",")) {
                		songList[i] = "\"" + str2 + "\"";
                	}
                	else {
                		songList[i] = str2;
                	}
            	}
        	}
        	else if (find.equals("\"isrc\":")){
        		String[] tokens3 = response.body().split("\"kind\":\"track\"");
        		for (int i = 0; i<count; i++) {        			
            		String str = tokens3[i+1];
            		if (str.contains("isrc"))
            		{
            			String[] tokens2 = str.split("\"isrc\":\"");
            			String str1 = tokens2[1].split("\"")[0];
                    	if (str1.contains(",")) {
                    		songList[i] = "\"" + str1 + "\"";
                    	}
                    	else {                 		
                    		songList[i] = str1;
                    	}
            		}
            		else
            		{
       //     			System.out.println(i);
            			songList[i] = "";
            		}
            	}
        	}
        	else {
        		for (int i = 0; i<count; i++) {
            		String str = tokens1[i+1]; 
                	String[] tokens2 = str.split("\"");
                	String str1 = tokens2[1];
                	if (str1.contains(",")) {
                		songList[i] = "\"" + str1 + "\"";
                	}
                	else {
                		songList[i] = str1;
                	}
            	}
            }
        
        }
        else{
        	var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=trending&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=B31E7OJEB3BxbSbJBHarCQOhvKZUY09J&limit=100&offset=0&linked_partitioning=1&app_version=1631196797&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens1 = response.body().split(find);
        	if (find.equals("\"kind\":\"track\"")) {
	            for (int i = 0; i<count; i++){
	            	String str = tokens1[i];
	            	String[] tokens2 = str.split(",");
	           		int size = tokens2.length;
	           		String str1 = tokens2[size-1];
	           		String str2 = str1.substring(5);
	            	if (str1.contains(",")) {
	                	songList[i] = "\"" + str2 + "\"";
	                }
	                else {
	                		songList[i] = str2;
	                }
	            }
        	}
        	else if (find.equals("\"isrc\":")){
        		String[] tokens3 = response.body().split("artist");
        		for (int p = 0; p<count; p++) {        			
            		String str = tokens3[p+1];
            		if (str.contains("\"isrc\":"))
            		{
            			String[] tokens2 = str.split("\"isrc\":\"");
            			String str1 = tokens2[1].split("\"")[0];           			
            			System.out.println(str1);
                    	if (str1.contains(",")) {
                    		songList[p] = "\"" + str1 + "\"";
                    	}
                    	else {
                    		songList[p] = str1;
                    	}
            		}
            		else
            		{
            			songList[p] = "";
            		}
            	}
        	}
            else {
            	for(int i = 0; i<count; i++){
            		String str = tokens1[i+1]; 
                	String[] tokens2 = str.split("\"");
                	String str1 = tokens2[1];
                	if (str1.contains(",")) {
                		songList[i] = "\"" + str1 + "\"";
                	}
                	else {
                		songList[i] = str1;
                	}
            	}
            }
        }
        return songList;
    }
    private static int chartCount(boolean chartType) throws InterruptedException, IOException
    {
    	var client = HttpClient.newHttpClient();  
    	int count = 0;
    	if (chartType == true) {
    		var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=top&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=B31E7OJEB3BxbSbJBHarCQOhvKZUY09J&limit=100&offset=0&linked_partitioning=1&app_version=1631196797&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens = response.body().split(",");
        	int len = tokens.length;
        	for (int i = 0 ; i < len; i++)
        	{
        		if (tokens[i].equals("\"kind\":\"track\"")) //different with "artists"??
        		{
        			count++;
        		}
        	}
    	}
    	else {
    		var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=trending&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=B31E7OJEB3BxbSbJBHarCQOhvKZUY09J&limit=100&offset=0&linked_partitioning=1&app_version=1631196797&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens = response.body().split(",");
        	int len = tokens.length;
        	for (int i = 0 ; i < len; i++)
        	{
        		if (tokens[i].equals("\"kind\":\"track\""))
        		{
        			count++;
        		}
        	}
    	}
    	return count;
    }

}
