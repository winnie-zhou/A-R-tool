package com.songdata;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import java.net.URL;
//import com.gargoylesoftware.htmlunit.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import com.opencsv.exceptions.CsvException;

public class GeniusDataMain {

	public static void main(String[] args) throws IOException, InterruptedException{
		
		final GeniusDataMain scraper = new GeniusDataMain();
		
		sendRequest("7110416", "\"pageviews\":");
		
/*		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");  
        
    	//scraper:
        List<String> allText = new ArrayList<String>(); //list of all songs on Today's directory
        for(int j=0; j<24; j++)
        {
        	String nextUrl = "https://genius.com/songs?last_greatest_datetime="+formatter.format(today)+"+" + Integer.toString(j); 
        	String htmlContent1 = scraper.getContent(nextUrl);
        	List<String> extractedText1 = scraper.extractText(htmlContent1);
        	int len1 = extractedText1.size();
        	for (int l = 0; l < len1; l++)
            {
            	String str1 = extractedText1.get(l);
            	int strLen1 = str1.length();
            	String newStr1 = str1.substring(0,strLen1-2); //takes off extra "
            	System.out.println(newStr1);
            	extractedText1.set(l, newStr1);
            }    
        	allText.addAll(extractedText1);
        	System.out.println("size:" + allText.size());
        }
		
        //record which songs make it on the top 200 chart
        
		String[] topIds = chartRequest("\"id\":");
//		String[] topTitles = chartRequest("\"title\":");		
//		String[] topArtists = chartRequest("\"name\":");
		
		    	
		    	try {
			    	String file1 = "c:\\Users\\Winnie\\Downloads\\Genius Count Database.csv"; 
			    	String file2 = "c:\\Users\\Winnie\\Downloads\\Genius Ranking Database.csv";
			    	    	
			    	OpenCsvReader database = new OpenCsvReader(); 
					
			    	database.addSong(file1, "test");
			    	
			    
			    	DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/YYYY");
			    	
			    	List<String> oldSongs = database.yesterdaysSongsList(file2, formatter.format(yesterday), 1); //yesterdays song IDs
			    	List<String> oldTitles = database.yesterdaysSongsList(file2, formatter.format(yesterday), 2);
			    	List<String> oldArtists = database.yesterdaysSongsList(file2, formatter.format(yesterday), 3);
			    	
			    	int numOldSongs = oldSongs.size();
			    	System.out.println("old songs:"+ numOldSongs);
			    	int numNewSongs = allText.size();
			    	
			    	
			    	//NEED TO GET SONG TITLES + ARTISTS OF NEW SONGS
			    	//should i use API calls (already have ID) or data scraping?
			    	// "title": "Chandelier",
			    	// "context": "Sia",
			    	//create ArrayLists of new titles and new artists, add to the old
			    	// find number of new songs?
			    	
			    	
			    	ArrayList<String> allTitles = new ArrayList<String>();
			    	allTitles.addAll(oldTitles);
			    	ArrayList<String> allArtists = new ArrayList<String>();
			    	allArtists.addAll(oldArtists);
			    	
			    	for (int i = 0; i< numNewSongs; i++)
			    	{
			    		String id1 = allText.get(i);
			    		String title1 = sendRequest(id1, "\"title\":\"");
			    		String artist1 = sendRequest(id1, "\"context\":\"");
			    		allTitles.add(title1);
			    		allArtists.add(artist1);
			    	}
			    	
			    	
			    	ArrayList<String> allSongs = new ArrayList<String>(); //List of ids
			    	allSongs.addAll(oldSongs);//adds old and new songs
			    	allSongs.addAll(allText);
			    	int totalSongCount = allSongs.size();
			    	
			    	String[] ranks = new String[totalSongCount];
			    	for (int k = 0; k < totalSongCount; k++) 
					{
						ranks[k] = "";
					} 

			    	for (int i=0; i<200; i++){ //Searches for songs that are in top 200 chart
			    		int search = allSongs.indexOf(topIds[i]);
						if (search < 0) { 
						}else {
							ranks[search] = Integer.toString(i+1);
						}
			    	}
			    	for (int k = 0;k < totalSongCount; k++)
			    	{
			    		System.out.println(allSongs.get(k));
			    		database.addSong(file1, formatter1.format(today) + "," + allSongs.get(k) + "," + allTitles.get(k) + "," + allArtists.get(k) + "," + sendRequest(allSongs.get(k), "\"pageviews\":"));
			    	}
			    	for (int k = 0;k < totalSongCount; k++)
			    	{
			    		database.addSong(file2, formatter1.format(today) + "," + allSongs.get(k) + "," + allTitles.get(k) + "," + allArtists.get(k) + "," + ranks[k]);
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
		    	}		    			*/    	
		    }
	
			private static String sendRequest(String id, String toFind)throws IOException, InterruptedException, MalformedURLException{
		        URL url = new URL("http://api.genius.com/songs/"+ id + "?text_format=html");
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		        conn.setRequestProperty("Accept", "application/json");
		        conn.setRequestProperty("Authorization","Bearer "+ "u4L8mMtVIQqKfGQjcuwkOQbo4vj4ENgQvmeG4ZbD36PE9ribU1xE48SQW3U-DlZv");		     
		        conn.setRequestProperty("Content-Type","application/json");
		        conn.setRequestMethod("GET");
		        
		        boolean redirect = false;
		        
		        int status = conn.getResponseCode();
		        if (status == 301)
		        {
		        	redirect = true;
		        }

		        if (redirect) {
		        	String newUrl = conn.getHeaderField("Location");
		        	conn = (HttpURLConnection) new URL(newUrl).openConnection();
		        	conn.setRequestProperty("Accept", "application/json");
			        conn.setRequestProperty("Authorization","Bearer "+ "u4L8mMtVIQqKfGQjcuwkOQbo4vj4ENgQvmeG4ZbD36PE9ribU1xE48SQW3U-DlZv");			        
			        conn.setRequestProperty("Content-Type","application/json");
			        conn.setRequestMethod("GET");
			        System.out.println("Redirect to URL : " + newUrl);
		        }
		        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String output;
		        StringBuffer response = new StringBuffer();
		        while ((output = in.readLine()) != null) {
		            response.append(output);
		        }

		        in.close();
		        // printing result from response
		        String fullResponse = response.toString();
		        String onlySongResponse = fullResponse.split("\"current_user_metadata\":")[0]; //ignores data from related songs (ex. samples)
		        System.out.println(fullResponse);
//		        System.out.println("Response:-" + response.toString());
		        if (toFind.equals("\"pageviews\":"))
		        {
		        	if (onlySongResponse.indexOf("\"pageviews\":") < 0)
		        	{
		        		return "";
		        	}
		        	else 
		        	{
				        String[] strings = onlySongResponse.split("\"pageviews\":");
				        String str1 = strings[1];
				        String[] strings1 = str1.split("}");
						String pageviews = strings1[0];
						System.out.println(pageviews);
						return pageviews;
		        	}
		        }
		        else
		        {
		        	String[] strings = onlySongResponse.split(toFind);
			        String str1 = strings[1];
			        String[] strings1 = str1.split("\"");
					String pageviews = strings1[0];
					System.out.println(pageviews);
					return pageviews;
		        }
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
		        for(int k = 0; k<200; k++)
		        {
		        	System.out.println(k);
		        	System.out.println(songList[k]);
		        }
		        return songList;
		    }

		    private String getContent(String url) throws IOException, IOException, InterruptedException{
		    	var client = HttpClient.newHttpClient();
		    	var request = HttpRequest.newBuilder(
				        URI.create(url)
				    ).build();
		    	HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		    //	System.out.println(response.body());
		        return response.body();
		    }
		    
		    private List<String> extractText(String content) {
		    	
		    	final List<String> tagValues = new ArrayList<String>();
		        final Pattern titleRegExp = Pattern.compile("<script>(.*?)</script>", Pattern.DOTALL);
		        
		        Pattern regex2 = Pattern.compile("<li data-id=\"(.*?)class=\"\">", Pattern.DOTALL);
		        
		        final Matcher matcher = regex2.matcher(content);
		        while (matcher.find()) {
		            tagValues.add(matcher.group(1));		            
		        }
		       
		        return tagValues;
		       
		    }	    


}
