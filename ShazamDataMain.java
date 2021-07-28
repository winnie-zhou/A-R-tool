package com.songdata;
import java.io.FileReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.io.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.util.ArrayList;
import java.lang.AutoCloseable;
import java.util.Map;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ShazamDataMain {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		OpenCsvReader reader = new OpenCsvReader();
    	String[] ids = chartRequest("\"key\":");
    	String[] titles = chartRequest("\"title\":");
    	String[] artists = chartRequest("\"subtitle\":");
    	try {
	
	    	String file1 = "c:\\Users\\Winnie\\Downloads\\Shazam Count Database.csv"; 
	    	String file2 = "c:\\Users\\Winnie\\Downloads\\Shazam Ranking Database.csv";
	    	    	
	    	OpenCsvReader database = new OpenCsvReader(); 
			int numSongs = database.findNumRows(file1);
			
			LocalDate today = LocalDate.now();
			LocalDate yesterday = today.minusDays(1);
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	    	
	    	System.out.println("hi");
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
	    	 
	    	
/*	    	for (int l = 0; l < numOldSongs; l++)
	    	{
	    		System.out.println(ranks[l]);
	    		database.addSong(file2, formatter.format(today) + "," + oldSongs[l] + "," + oldTitles[l] + "," + oldArtists[l] + "," + ranks[l]);
	    	}
	    	for (int m = 0; m < numNewSongs; m++)
	    	{
	    		System.out.println(ranks[m]);
	    		database.addSong(file2, formatter.format(today) + "," + newSongs[m] + "," + newTitles[m] + "," + newArtists[m] + "," + ranks[m]);
	    	}*/
	    	
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
            URI.create("https://www.shazam.com/services/count/v2/web/track/"+ id)
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
//    		System.out.println(shazams);
    		return shazams;
    
    }
    
    private static String[] chartRequest(String find) throws IOException, InterruptedException
    {
    	var client = HttpClient.newHttpClient();

        // create a request
        var request = HttpRequest.newBuilder(
            URI.create("https://www.shazam.com/shazam/v3/en-US/US/web/-/tracks/ip-country-chart-US?pageSize=200&startFrom=0")
        ).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        String[] tokens1 = response.body().split(find);
        String[] songList = new String[200];
        
        for (int i = 0; i<200; i++)
        {
        	String str = tokens1[i+1];
        	String[] tokens2 = str.split("\"");
        	String str1 = tokens2[1];
        	if (str1.contains(",")) {
        		songList[i] = "\"" + str1 + "\"";
        	}
        	else {
        		songList[i] = str1;
        	}
  //      	System.out.println(songList[i]);
        }
        return songList;
    }

}
