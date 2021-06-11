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
public class SongDataMain {

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
    	

    	String file = "c:\\Users\\Winnie\\Downloads\\Shazam Top 200 United States Chart 10-06-2021 (1).csv";
    	
    	SongDataMain sdm = new SongDataMain();
    	OpenCsvReader reader = new OpenCsvReader();
    	String[] ids = new String[200];
    	try {
    	ids= reader.getIds(file);
    	String[] shazams = new String[200];
    	for (int i = 0; i< 200; i++)
    	{
    		String id = ids[i];
    		System.out.println(i);
    		shazams[i] = synchronousRequest(id);
    	}
    	reader.addColumn(file, shazams);
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
    	//<Supplier<APOD>>
/*    	HttpURLConnection connection = (HttpURLConnection) new URL("https://www.shazam.com/services/count/v2/web/track/565106878").openConnection();
		
		connection.setRequestMethod("GET"); 

		int response = connection.getResponseCode();
        
        if (response == 200){
        	
        	System.out.println(response);
        }*/
        // the response:
      //  System.out.println(response);
       System.out.println(response.body());        
    		String[] tokens = response.body().split("\"");
/*    		int i = 0;
    		while (tokens[i] != null)
    		{
    			System.out.println(tokens[i]);
    		}*/
    		String str = tokens[6];
    		String shazams = str.substring(1, str.length() - 1);
//    		System.out.println(shazams);
    		return shazams;
    
    }
       

}
