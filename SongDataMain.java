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

public class SongDataMain {
	
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    	//chartRequest();

//    	String file = "c:\\Users\\Winnie\\Downloads\\Shazam Top 200 United States Chart 17-06-2021.csv";
//    	String retFile = "c:\\Users\\Winnie\\Downloads\\ShazamCount.csv";
    	
//    	String[] ids = new String[200];
    	OpenCsvReader reader = new OpenCsvReader();
    	String[] ids = chartRequest("\"key\":");
    	String[] songNames = chartRequest("\"title\":");
    	String[] artists = chartRequest("\"subtitle\":");
    	try {
//    	ids= reader.getIds(file);
    	String[] shazams = new String[200];

    	for (int i = 0; i< 200; i++) //change back to 200
    	{
    		String id = ids[i];
    //		System.out.println(i);
    		shazams[i] = synchronousRequest(id);

    	}
 //   	reader.addColumn(file, retFile, shazams, false);
    	////DATABASE:
    	String file1 = "c:\\Users\\Winnie\\Downloads\\Shazam Song Database.csv"; //
    	String file2 = "c:\\Users\\Winnie\\Downloads\\Shazam Song Database1.csv";
 //   	    	
    	OpenCsvReader database = new OpenCsvReader(); 
		int numSongs = database.findNumRows(file1);
		
		System.out.println("number of songs:" + numSongs);
		String[] songIds = new String[numSongs]; // fix this
		songIds = database.colToArr(file1, 0); 
		
		String[] ranks = new String[numSongs + 50];
		ranks[0] = "Rank";
		for (int k = 0; k <numSongs + 50; k++)
		{
			ranks[k] = "";
		}

    	int numRows = database.findNumRows(file1); //-1 bc date is on top
    	
    	int numRows1 = database.findNumRows(file1);
    	System.out.println("NUMBER OF ROWS: "+ numRows);
    	for (int i=0; i<200; i++){ //Searches for songs in daily top 200 that are not already in database
    		int search = database.searchArray(songIds, numRows, ids[i]);
    		//int search = Arrays.binarySearch(songIds, ids[i]); 
    		//System.out.println(i + ":" + search);
			if (search < 0) { 
//				database.addRow(file1, file2, shazams[i]);
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
    	for (int j = 1; j<numRows; j++) { //pulls shazam count for all songs 

    		allShazams[j] = synchronousRequest(allSongIds[j]); 
    		System.out.println(j);
    	}
    	
    	database.addColumn(file1, file2, allShazams, true);
    	database.copyOver(file2, file1);
    	database.addColumn(file1, file2, ranks, true);
    	database.copyOver(file2, file1);
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


	private static void asynchronousRequest() throws InterruptedException, ExecutionException {

        // create a client
        var client = HttpClient.newHttpClient();

        // create a request
        var request = HttpRequest.newBuilder(
        	//HttpURLConnection connection = (HttpURLConnection) new URL("https://www.shazam.com/services/count/v2/web/track/" + shazamid).openConnection();
            URI.create("https://www.shazam.com/services/count/v2/web/track/565106878"))
            .header("accept", "application/json")
            .build();

        // use the client to send the request
        var responseFuture = client.sendAsync(request, new JsonBodyHandler<>(APOD.class));

        // We can do other things here while the request is in-flight

        // This block s until the request is complete
        var response = responseFuture.get();

        // the response:
        System.out.println(response);
       // System.out.println(response.body().get().title);
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
