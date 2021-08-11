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
//import com.gargoylesoftware.htmlunit.WebClient;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import com.opencsv.exceptions.CsvException;

public class GeniusDataMain {

	public static void main(String[] args) throws IOException, InterruptedException{
	
		final GeniusDataMain scraper = new GeniusDataMain();
		
//		scraper.scrape();
		
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");
    	
    	String yesterdaysUrl = "https://genius.com/songs?last_greatest_datetime="+formatter.format(yesterday)+"+23";
    	String yesterdaysDirectory = scraper.getContent(yesterdaysUrl);
    	final List<String> yesterdaysSongs = scraper.extractText(yesterdaysDirectory);
    	String firstSong1 = yesterdaysSongs.get(0);
    	String firstSong = firstSong1.substring(0,firstSong1.length()-2);
    	System.out.println("first song of yesterday: " + firstSong);
    	
/*		String todaysUrl = "https://genius.com/songs?last_greatest_datetime="+formatter.format(today)+"+00";
		final String htmlContent = scraper.getContent(todaysUrl);
		//https://genius.com/songs?last_greatest_datetime=2021-07-18+12%3A27%3A10+UTC&last_id=7018963
        final List<String> extractedText = scraper.extractText(htmlContent);
        int len = extractedText.size();
        
        for (int k = 0; k < len; k++)
        {
        	String str = extractedText.get(k);
        	int strLen = str.length();
        	String newStr = str.substring(0,strLen-2); //takes off extra "
        	System.out.println(newStr);
        	extractedText.set(k, newStr);
        }    */         
        
        List<String> allText = new ArrayList<String>();
 //       allText.addAll(extractedText);
        for(int j=0; j<24; j++)
        {
 //       	int length = allText.size();
 //       	String lastSong = allText.get(length-1);
 //       	System.out.println("last song = " + lastSong);
        	String nextUrl = "https://genius.com/songs?last_greatest_datetime="+formatter.format(today)+"+" + Integer.toString(j); //todaysUrl + "&last_id=" + lastSong;
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
		

		String[] ids = chartRequest("\"id\":");
		String[] titles = chartRequest("\"title\":");
		String[] artists = chartRequest("\"name\":");
		
		//record which songs make it on the top 100 chart
		    	
		    	try {
			    	String file1 = "c:\\Users\\Winnie\\Downloads\\Genius Count Database.csv"; 
			    	String file2 = "c:\\Users\\Winnie\\Downloads\\Genius Ranking Database.csv";
			    	    	
			    	OpenCsvReader database = new OpenCsvReader(); 
					int numSongs = database.findNumRows(file1);
					
			    	DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/YYYY");
			    	
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
			    		database.addSong(file1, formatter1.format(today) + "," + oldSongs[k] + "," + oldTitles[k] + "," + oldArtists[k] + "," + synchronousRequest(oldSongs[k]));
			    	}
			    	for (int j = 0; j < numNewSongs; j++)
			    	{
			    		database.addSong(file1, formatter1.format(today) + "," + newSongs[j] + "," + newTitles[j] + "," + newArtists[j] + "," + synchronousRequest(newSongs[j]));
			    	}

			    	System.out.println("oldsongs:" + numOldSongs);
			    	System.out.println("newsongs:" + numNewSongs);
			    	for (int l = 0; l < numOldSongs; l++)
			    	{
			    		System.out.println(ranks[l]);
			    		database.addSong(file2, formatter1.format(today) + "," + oldSongs[l] + "," + oldTitles[l] + "," + oldArtists[l] + "," + ranks[l]);
			    	}
			    	for (int m = 0; m < numNewSongs; m++)
			    	{
			    		System.out.println(ranks[m]);
			    		database.addSong(file2, formatter1.format(today) + "," + newSongs[m] + "," + newTitles[m] + "," + newArtists[m] + "," + ranks[m]);
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
	/*	        var request = HttpRequest.newBuilder(
		            URI.create("api.genius.com/annotations/"+ id)
		        ).build();*/
		        
		        String authToken = "1OvoUEZlQmNoJS2NP_iTJtDyiGZaYvn-RGeBENvgywee0_WnqUn8_3H_P_3IM9SH";
		        
		        String authorizationHeader = "Basic " + authToken;
		        
		        var request = HttpRequest.newBuilder()
		                .uri(URI.create("api.genius.com/annotations/"+ id))
		                .GET()
		                .header("Authorization", authorizationHeader)
		                .header("Content-Type", "application/json")
		                .build();

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
		        final Pattern titleRegExp = Pattern.compile("<script>(.*?)</script>", Pattern.DOTALL);//("<head>(.*?)</head>", Pattern.DOTALL);
		//        final Pattern titleRegExp = Pattern.compile("<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
		        					//"<head>.*?<title>(.*?)</title>.*?</head>", Pattern.DOTALL);
		        
	//	        Pattern regex2 = Pattern.compile("<div id=\"main\">.*?<ul class = \"song_list primary list \">(.*?)</ul>.*?</div>", Pattern.DOTALL);
		        
//		        Pattern regex2 = Pattern.compile("<div id=\"main\">(.*?)</div>", Pattern.DOTALL);
		        
//		        Pattern regex2 = Pattern.compile("<ul class=\"song_list primary_list \">(.*?)</div>", Pattern.DOTALL);
		        
		        Pattern regex2 = Pattern.compile("<li data-id=\"(.*?)class=\"\">", Pattern.DOTALL);
		        
		        
//		        Pattern regex1 = Pattern.compile("<li data-id=\"(.*?)class=\">", Pattern.DOTALL);
		        
//		        Pattern regex1 = Pattern.compile("<div class=\"main\">(.*)<div class=\"pagination\">", Pattern.DOTALL);
		        
//		        Pattern regex1 = Pattern.compile("<body class(.*)</body>", Pattern.DOTALL);
		        
		        //Pattern regex1 = Pattern.compile("</ul>(.*)</ul>.*", Pattern.DOTALL);
		        
//		        Pattern regex1 = Pattern.compile("<div id=\"main\">.*?<ul class= \"song_list primary list \">(.*?)</ul>.*?</div>", Pattern.DOTALL);
		         									//<div id="main">
		        
		        //<div class="pagination">

	//	        final Matcher matcher = titleRegExp.matcher(content);
		        
		        final Matcher matcher = regex2.matcher(content);
		        while (matcher.find()) {
		        	//System.out.println(matcher.group(1));
		            tagValues.add(matcher.group(1));		            
		        }
		       
		        return tagValues;
		       
		    }
		    
		    private void scrape() throws MalformedURLException{
		        final String httpsUrl = "https://genius.com/songs?last_greatest_datetime=2021-07-18+12%3A47%3A34+UTC";
		        try {
		           final URL url = new URL(httpsUrl);
		           final HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
		              
		           System.out.println("****** Content of the URL ********");          
		           final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		                  
		           String input;
		                  
		           while ((input = br.readLine()) != null){
		             System.out.println(input);
		           }
		           br.close();
		        } catch (MalformedURLException e) {
		           e.printStackTrace();
		        } catch (IOException e) {
		           e.printStackTrace();
		        }
		     }
		    


}
