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
//		sendRequest("3774339", "\"full_title\":\"");
		
		//SCRAPE ARTIST AND TITLE!
		
		final GeniusDataMain scraper = new GeniusDataMain();
		
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY-MM-dd");  
        
    	//scraper:
        List<String> allText = new ArrayList<String>(); //list of all songs on Today's directory
        List<String> newTitles = new ArrayList<String>();
    	List<String> newArtists = new ArrayList<String>();
        for(int j=0; j<24; j++)
        {
        	String nextUrl = "https://genius.com/songs?last_greatest_datetime="+formatter.format(today)+"+" + Integer.toString(j); 
        	
        	String htmlContent1 = scraper.getContent(nextUrl);
        	
        	List<String> newTitlesAndArtists = scraper.extractText(htmlContent1, false);
        	
        	int len2 = newTitlesAndArtists.size();
        	boolean[] invalidSongs = new boolean [len2];
 //       	System.out.println("number of all songs" + len2);
        	for (int k = 0; k< len2; k++)
        	{
        		invalidSongs[k] = false;
        	}        		
        	for (int m = 0; m<len2 ; m++)
        	{
        		String fullLine = newTitlesAndArtists.get(m);
        		
        		
        		if (fullLine.contains("Translation")|fullLine.contains("Romanized")|fullLine.contains("Traduzione")|fullLine.contains("Tradution")|fullLine.contains("Traducciones"))
        		{
        			invalidSongs[m] = true;
        			System.out.println(fullLine);
        			continue;//gets rid of translated songs
        		}
        		if (fullLine.contains("&nbsp;"))
        		{
        			fullLine = fullLine.replace("&nbsp;", " ");
        		}
        		if (fullLine.contains("&amp;"))
        		{
        			fullLine = fullLine.replace("&amp;", "&");
        		}
        		if (fullLine.contains("&quot;"))
        		{
        			fullLine = fullLine.replace("&quot;", "\"");
        		}

        		String fullLineMinusLyrics = fullLine.split(" Lyrics")[0];
        		String[] names = fullLineMinusLyrics.split(" – ");
        		if (names[0].contains(","))
        		{
        			newArtists.add("\"" + names[0] + "\"");
        		}
        		else {       		
        			newArtists.add(names[0]);
        		}
        		if (names[1].contains(","))
        		{
        			newTitles.add("\"" + names[1] + "\"");
        		}
        		else
        		{
        			newTitles.add(names[1]);
        		}
        	}
        	
        	List<String> extractedText1 = scraper.extractText(htmlContent1, true);
        	int len3 = extractedText1.size();
        	for (int x = 0; x<len3; x++)
        	{
        		if (invalidSongs[x] == true)
        		{
        			extractedText1.remove(x);
        			len3--;
        			continue;
        		}
        	}
        	int len1 = extractedText1.size();
        	System.out.println(len3 + ":" + len1);
        	for (int l = 0; l < len1; l++)
            {
            	String str1 = extractedText1.get(l);
            	int strLen1 = str1.length();
            	String newStr1 = str1.substring(0,strLen1-2); //takes off extra "
            	extractedText1.set(l, newStr1);
            }    
        	allText.addAll(extractedText1);
   //     	System.out.println("size:" + allText.size());
   //     	System.out.println("number of invalid songs(titles):" + newTitles.size());
        }
        
        System.out.println("size:" + allText.size());
    	System.out.println("number of valid songs(titles):" + newTitles.size());
        
        //record which songs make it on the top 200 chart
        
		String[] topIds = chartRequest(",\"instrumental\":"); //add it to one big string
		String topSongsStrings = "";
		for (int y = 0; y<200; y++)
		{
			topSongsStrings += y + ")" + topIds[y] + ",";
		}
		
		String[] topTitles = chartRequest("\"title\":");		
		String[] topArtists = chartRequest("\"name\":");
		
		    	
		    	try {
			    	String file1 = "c:\\Users\\Winnie\\Downloads\\Genius Count Database.csv"; 
			    	String file2 = "c:\\Users\\Winnie\\Downloads\\Genius Ranking Database.csv";
			    	    	
			    	OpenCsvReader database = new OpenCsvReader();     	
			    
			    	DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd/MM/YYYY");
			    	
			    	
			    	String oldSongsString = database.yesterdaysSongsString(file1, formatter1.format(yesterday), 1);
			    	List<String> oldSongs = database.yesterdaysSongsList(file2, formatter1.format(yesterday), 1); //yesterdays song IDs
			    	List<String> oldTitles = database.yesterdaysSongsList(file2, formatter1.format(yesterday), 2);
			    	List<String> oldArtists = database.yesterdaysSongsList(file2, formatter1.format(yesterday), 3);
			    	
			    	int numOldSongs = oldSongs.size();
			    	System.out.println("old songs:"+ numOldSongs);			    	
			    	
			    	ArrayList<String> allTitles = new ArrayList<String>();
			    	allTitles.addAll(oldTitles);
			    	ArrayList<String> allArtists = new ArrayList<String>();
			    	allArtists.addAll(oldArtists);
			    	
			    	allTitles.addAll(newTitles);
			    	allArtists.addAll(newArtists);
			    	
/*			    	for (int i = 0; i< numNewSongs; i++)
			    	{
			    		String id1 = allText.get(i);
			    		String title1 = sendRequest(id1, "\"title\":\"");
			    		System.out.println(id1);
			    		String artist1 = sendRequest(id1, "\"full_title\":\"");
			    		allTitles.add(title1);
			    		allArtists.add(artist1);
			    	}*/			    	
			    	
			    	ArrayList<String> allSongs = new ArrayList<String>(); //List of ids
			    	allSongs.addAll(oldSongs);//adds old and new songs
			    	allSongs.addAll(allText);
			    	int totalSongCount = allSongs.size();
			    	
			    	System.out.println("number of song IDs: " + totalSongCount);
			    	System.out.println("number of Titles: " + allTitles.size());
			    	System.out.println("number of Artists: " + allArtists.size());
			    	
			    	
			    	int numNewSongs = 0;
			    	for (int i=0; i< 200; i++){
			    		int z = oldSongsString.indexOf(topIds[i]);
			    		if (z < 0) {
			    			numNewSongs++;
			    		}
			    	}
			    	
			    	String[] ranks = new String[totalSongCount + numNewSongs];
			    	for (int k = 0; k < totalSongCount + numNewSongs; k++) 
					{
						ranks[k] = "";
					} 
			    	
			    	for (int p = 0; p<200; p++) {
			    		int z = oldSongsString.indexOf(topIds[p]);
			    		if (z < 0) {
			    			allSongs.add(topIds[p]);
			    			allTitles.add(topTitles[p]);
			    			allArtists.add(topArtists[p]);
			    			ranks[totalSongCount] = Integer.toString(p+1);
			    			totalSongCount++;
			    		}else {
			    			int position = z/8; //
			    			ranks[position] = Integer.toString(p+1);
			    		}
			    	}

			    	System.out.println(allSongs.size() + " : " + allTitles.size() + " : " + allArtists.size() + " : " + totalSongCount);
			    	int songCount = allTitles.size();
			    	
			    	for (int k = 0;k < songCount; k++)
			    	{
			    		System.out.println(allSongs.get(k) + "***"+ allTitles.get(k) + "***" + allArtists.get(k) + "***rank: " + ranks[k]);
			    		System.out.println(allSongs.get(k) + " pageviews: " + sendRequest(allSongs.get(k), "\"pageviews\":") + " rank: " + ranks[k]);
			    		database.addSong(file1, formatter1.format(today) + "," + allSongs.get(k) + "," + allTitles.get(k) + "," + allArtists.get(k) + "," + sendRequest(allSongs.get(k), "\"pageviews\":"));
//			    		database.addSong(file2, formatter1.format(today) + "," + allSongs.get(k) + "," + allTitles.get(k) + "," + allArtists.get(k) + "," + ranks[k]);
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
	//		        System.out.println("Redirect to URL : " + newUrl);
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
		       // System.out.println(fullResponse);
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
	//					System.out.println(pageviews);
						return pageviews;
		        	}
		        }
		        else if (toFind.equals("\"full_title\":\""))
		        {
		        	String[] strings = onlySongResponse.split(toFind);
		        	String str1 = strings[1];
		        	System.out.println(str1);
		        	String[] strings1 = str1.split("\","); //sometimes titles include quotes ???
		        	String fullTitle = strings1[0];
		        	System.out.println(fullTitle);
		        	String[] strings2 = fullTitle.split("by");
		        	String pageviews = strings2[1];
		        	pageviews.trim();
		        	System.out.println(pageviews);
		        	return pageviews;
		        }
		        else
		        {
		        	System.out.println(toFind);
		        	String[] strings = onlySongResponse.split(toFind);
		        	System.out.println("******"+strings[0]);
			        String str1 = strings[1];
			        String[] strings1 = str1.split("\"");
					String pageviews = strings1[0];
	//				System.out.println(pageviews);
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
		        	String str1;
		        	if (find.equals(",\"instrumental\":")){
		        		String string1 = substrings1[i];
		        		String[] tokens1 = string1.split(":");		        		
		        		str1 = tokens1[tokens1.length-1];
		        	}
		        	else {
		        		String str = substrings1[i+1];
			        	String[] tokens = str.split("\"");
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
		        	String str1;
		        	if (find.equals(",\"instrumental\":")){
		        		String string1 = substrings1[i];
		        		String[] tokens1 = string1.split(":");		        		
		        		str1 = tokens1[tokens1.length-1];
		        	}
		        	else {
		        		String str = substrings1[i+1];
			        	String[] tokens = str.split("\"");
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
		        	String str1;
		        	if (find.equals(",\"instrumental\":")){
		        		String string1 = substrings1[i];
		        		String[] tokens1 = string1.split(":");		        		
		        		str1 = tokens1[tokens1.length-1];
		        	}
		        	else {
		        		String str = substrings1[i+1];
			        	String[] tokens = str.split("\"");
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
		        	String str1;
		        	if (find.equals(",\"instrumental\":")){
		        		String string1 = substrings1[i];
		        		String[] tokens1 = string1.split(":");		        		
		        		str1 = tokens1[tokens1.length-1];
		        	}
		        	else {
		        		String str = substrings1[i+1];
			        	String[] tokens = str.split("\"");
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
		    
		    private List<String> extractText(String content, boolean id) {
		    	
		    	final List<String> tagValues = new ArrayList<String>();
		        final Pattern titleRegExp = Pattern.compile("<script>(.*?)</script>", Pattern.DOTALL);
		        Pattern regex; 
		        if (id == true)
		        {
		        	Pattern regex2 = Pattern.compile("<li data-id=\"(.*?)class=\"\">", Pattern.DOTALL);
		        	regex = regex2;
		        }
		        else
		        {
		        	Pattern regex4 = Pattern.compile("song_link\" title=\"(.*?)\">", Pattern.DOTALL);
		        	regex = regex4;
		        }

		        final Matcher matcher = regex.matcher(content);
		        while (matcher.find()) {
		            tagValues.add(matcher.group(1));		            
		        }
		       
		        return tagValues;
		       
		    }	    


}
