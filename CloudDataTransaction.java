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

public class CloudDataTransaction {

	public static void main(String[] args) throws InterruptedException, IOException{
		// TODO Auto-generated methods
		String file = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Database.csv";
    	String file1 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Database1.csv";
    	String file2 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Database.csv";
    	String file3 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Database1.csv";
    	
    	String file4 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Likes Database.csv";
    	String file5 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Likes Database1.csv";
    	String file6 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Likes Database.csv";
    	String file7 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Likes Database1.csv";
    	
    	String file8 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Comments Database.csv";
    	String file9 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Comments Database1.csv";
    	String file10 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Comments Database.csv";
    	String file11 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Comments Database1.csv";
    	
    	String file12 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Rank Database.csv";
    	String file13 = "c:\\Users\\Winnie\\Downloads\\Soundcloud Top 50 Rank Database1.csv";
    	String file14 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Rank Database.csv";
    	String file15 = "c:\\Users\\Winnie\\Downloads\\Soundcloud N&H Rank Database1.csv";
    	
  //  	dailyUpdate(file, file1, true, "\"playback_count\":", true);
    	dailyUpdate(file2,file3, false, "\"playback_count\":", true);
    	
    	dailyUpdate(file4, file5, true, "\"likes_count\":", true);
   // 	dailyUpdate(file6,file7, false, "\"likes_count\":", true);
    	
    	dailyUpdate(file8, file9, true, "\"comment_count\":", true);
   // 	dailyUpdate(file10,file11, false, "\"comment_count\":", true);
    	
    	dailyUpdate(file12, file13, true, "\"comment_count\":", false);
   // 	dailyUpdate(file14,file15, false, "\"comment_count\":", false);    	
	}
	
	private static void dailyUpdate(String file1, String file2, boolean chartType, String metric, boolean notRank) throws InterruptedException, IOException{
    	OpenCsvReader reader = new OpenCsvReader();
    	String[] ids = chartRequest("\"kind\":\"track\"", chartType);
    	String[] songNames = chartRequest("\"title\":", chartType);
    	String[] artists = chartRequest("\"username\":", chartType); //"user_id"
    	try {
    		String[] shazams = new String[99];

	    /*	for (int i = 0; i< 99; i++)
	    	{
	    		String id = ids[i];
	    		System.out.println(id);
	    		shazams[i] = synchronousRequest(id, metric);
	
	    	}*/
    	
	    	OpenCsvReader database = new OpenCsvReader(); 
			int numSongs = database.findNumRows(file1);
			
			System.out.println("number of songs:" + numSongs);
			String[] songIds = new String[numSongs]; // fix this
			songIds = database.colToArr(file1, 0); 
			
			String[] ranks = new String[numSongs + 100];
			
			for (int k = 0; k <numSongs + 100; k++)
			{
				ranks[k] = "";
			}
	
	    	int numRows = database.findNumRows(file1); //-1 bc date is on top
	    	
	    	int numRows1 = database.findNumRows(file1);
	    	System.out.println("NUMBER OF ROWS: "+ numRows1);
	    	for (int i=0; i<99; i++){ //Searches for songs in top 50 that are not already in database
	    		int search = database.searchArray(songIds, numRows, ids[i]);	   		
				if (search < 0) { 
					database.addSong(file1, ids[i] + "," + songNames[i] + "," + artists[i]);
					ranks[numRows1] = Integer.toString(i+1);
					numRows1++;
					}
				else {
					String s = Integer.toString(i+1);
					ranks[search] = s;
				}
	    	}
	    	
	    	numRows = database.findNumRows(file1);
	    	String[] allSongIds = new  String[numRows];
	    	String[] allShazams = new String[numRows];
	    	System.out.println("NUMBER OF ROWS:"+ numRows);
	    	
	    	allSongIds = database.colToArr(file1, 0); //updates song ids
	    	LocalDate today = LocalDate.now();
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/YYYY");
	    	
	    	allShazams[0] = formatter.format(today);
	    	ranks[0] = formatter.format(today);
	    	for (int j = 1; j<numRows; j++) { //pulls shazam count for all songs 
	
	    		allShazams[j] = synchronousRequest(allSongIds[j], metric); 
	    	//	System.out.println(j);
	    	}
	    	
	    	if (notRank == true) {
	    		database.addColumn(file1, file2, allShazams, true);
		    	database.copyOver(file2, file1);
	    	}
	    	else {
	    		database.addColumn(file1, file2, ranks, true);
		    	database.copyOver(file2, file1);
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
            URI.create("https://api-v2.soundcloud.com/tracks?client_id=aVfBEvueQiX0L8YGQTo3mm5vQJhZfseT&app_version=1625669469&ids="+id)
        ).build();

        // use the client to send the request
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        		//new JsonBodyHandler<>(APOD.class)); 
        
      //  System.out.println(response.body());
        
        	String[] tokens1 = response.body().split(find);
        	System.out.println(id);
        	if (response.body().equals("[]")) {
        		return "";
        	}
        	String str1 = tokens1[1];
        	String[] tokens2 = str1.split(",");
        	String count = tokens2[0];
        	return count;    	 
    }
    
    private static String[] chartRequest(String find, boolean chartType) throws IOException, InterruptedException
    {
    	var client = HttpClient.newHttpClient();    	
    	// chartType: true = Top 50, false = New & Hot
    	String[] songList = new String[101];
    	
        if (chartType == true){
        	var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=top&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=p0qXnO6vGPGnUE8mStvEVVelga3zO3sy&limit=100&offset=0&linked_partitioning=1&app_version=1625061987&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens1 = response.body().split(find);
        	if (find.equals("\"kind\":\"track\"")) {
        		for (int i = 0; i<99; i++) {
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
        //        	return songList;
            	}
        	}
        	else {
        		for (int i = 0; i<99; i++) {
        			System.out.println(i);
            		String str = tokens1[i+1]; 
                	String[] tokens2 = str.split("\"");
                	String str1 = tokens2[1];
                	if (str1.contains(",")) {
                		songList[i] = "\"" + str1 + "\"";
                	}
                	else {
                		songList[i] = str1;
                	}
  //              	return songList;
            	}
            }
        
        }
        else{
        	var request = HttpRequest.newBuilder( URI.create
        			("https://api-v2.soundcloud.com/charts?kind=trending&genre=soundcloud%3Agenres%3Aall-music&region=soundcloud%3Aregions%3AUS&high_tier_only=false&client_id=ahAJuiWvqPHUWMtUhizqN5QaITxmOwTN&limit=100&offset=0&linked_partitioning=1&app_version=1624267273&app_locale=en")).build();
        	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        	String[] tokens1 = response.body().split(find);
        	if (find.equals("\"kind\":\"track\"")) {
	            for (int i = 0; i<95; i++){
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
	     //       return songList;
        	}
            else {
            	for(int i = 0; i<95; i++){
            		String str = tokens1[i+1]; 
                	String[] tokens2 = str.split("\"");
                	String str1 = tokens2[1];
                	if (str1.contains(",")) {
                		songList[i] = "\"" + str1 + "\"";
                	}
                	else {
                		songList[i] = str1;
                	}
                //	return songList;
            	}
            }
        }
        return songList;
    }
		

}
