package com.songdata;
import java.util.ArrayList;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.lang.Object;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.*;


public class OpenCsvReader {
	
    public static void main(String[] args) throws IOException, CsvException, URISyntaxException {

//        String fileName = "c:\\Users\\Winnie\\Downloads\\CSVDemo.csv";
    	OpenCsvReader reader = new OpenCsvReader();
    	String fileName = "c:\\Users\\Winnie\\Downloads\\Shazam Count Database.csv";
    	String fileName1 = "c:\\Users\\Winnie\\Downloads\\Shazam Song Database1.csv";
    	String fileName2 = "c:\\Users\\Winnie\\Downloads\\ShazamCount.csv";
    	String file2 = "c:\\Users\\Winnie\\Downloads\\Shazam Top 200 United States Chart 30-05-2021.csv";
    	String file = "c:\\Users\\Winnie\\Downloads\\Shazam Top 200 United States Chart 01-06-2021.csv";
    	String[] arr = {"1", "2,", "3", "5"};
    	String[] arr1 = {"1", "2", "3", "4"};
    	
    //	System.out.println( reader.countCols(file2));
    	
//    	reader.addColumn(file2, file, arr, true);
//    	reader.copyOver(file, file2);
//    	reader.addColumn(file2, file, arr1, true);
 //   	reader.addRow(fileName, "hi");
 //   	reader.addRow(fileName, "hi");
   	reader.addSong(fileName, "HI");
 //   	reader.addSong(fileName, "HI");
 //   	reader.colToArr(fileName1);
 //       List<String[]> r = readCsvFile(fileName);
 //       r.forEach(x -> System.out.println(Arrays.toString(x)));

//        readCsvWithEmbeddedSpecial();
 //       String[] arr = getIds(fileName);
    }
    
	private static void readCsvWithEmbeddedSpecial() throws IOException, CsvException, URISyntaxException {

        // loads file from resource folder
        URL resource = OpenCsvReader.class.getClassLoader().getResource("csv/monitor.csv");
        File file = Paths.get(resource.toURI()).toFile();

        List<String[]> r;
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            r = reader.readAll();
        }

        // print result
        int listIndex = 0;
        for (String[] arrays : r) {
            System.out.println("\nString[" + listIndex++ + "] : " + Arrays.toString(arrays));

            int index = 0;
            for (String array : arrays) {
                System.out.println(index++ + " : " + array);
            }

        }

    }

    private static List<DetailedData> readCsvToObject(String fileName) throws IOException, CsvException {
        List<DetailedData> beans = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(DetailedData.class)
                .build()
                .parse();
        return beans;
    }

    private static List<String[]> readCsvFileCustomSeparator(String fileName, char separator) throws IOException, CsvException {

        List<String[]> r;

        // custom separator
        CSVParser csvParser = new CSVParserBuilder().withSeparator(separator).build();
        try (CSVReader reader = new CSVReaderBuilder(
                new FileReader(fileName))
                .withCSVParser(csvParser)   // custom CSV parser
                .withSkipLines(1)           // skip the first line, header info
                .build()) {
            r = reader.readAll();
        }
        return r;

    }
    

    public List<String[]> readCsvFile(String fileName) throws IOException, CsvException {

       List<String[]> r = new ArrayList<String[]>();
/*        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            r = reader.readAll();
        }*/

//         read line by line
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] lineInArray;
            while ((lineInArray = reader.readNext()) != null) {
                r.add(lineInArray);
            }
            reader.close();
        }
        return r;

    }

    public String[] getIds(String fileName) throws IOException, CsvException{
    	String[] ids = new String[200];
    	List<String[]> lines = readCsvFile(fileName); 
    	for(int i = 0; i< lines.size()-3; i++) {
    	   String[] line = lines.get(i+3);
 //   	   System.out.println(i);
 //    	   System.out.println(line[line.length-1]);
     	   ids[i] = line[line.length-1];
    	}
    	return ids;
    }
    
    public int countCols(String fileName) throws IOException, CsvException{
//    	BufferedReader br=null;
    	try(CSVReader reader = new CSVReader(new FileReader(fileName))){
    //		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
    		String[] header = reader.readNext(); // assuming first read
    		int columnCount = 0;
    		if (header != null) {                     // and there is a (header) line
    		   columnCount = header.length;       // get the column count
    		//   System.out.println("number of columms: " + columnCount);
    		}
    		return columnCount;
    	}
 //   		String line = br.readLine();
 //   		line.split(",");
    	
    }
    
    public void addColumn(String fileName, String fileName1, String[] nums, boolean database) throws IOException{
    	CSVReader reader = new CSVReader(new FileReader(fileName));
    	CSVWriter writer = new CSVWriter(new FileWriter(fileName1));
 //       final String lineSep=System.getProperty("line.separator");

        try {
          
            String line = null;
            int i=0;  

            int numCols = countCols(fileName);
            System.out.println("number of cols: " + numCols);
            String[] header = reader.readNext();
            while (header != null) 
            { 
 //           	System.out.println(line);
                String addedColumn = String.valueOf(nums[i]);
                
                if(header.length == 3)
            	{
                	//line = "";
                	String[] toAdd = new String[numCols+1];
                	
                	for (int k = 0; k<3; k++)
                	{
                		toAdd[k] = header[k];
                	}
                	
                	for (int m = 3; m<numCols ; m++)
                	{
                		toAdd[m] = "";
                	}
                	
                	toAdd[numCols] = addedColumn;
                	
                	for(int x = 0; x < numCols+1; x ++)
                	{
                		System.out.print(toAdd[x] + ",");
                		
                	}
                	System.out.println();
                	
                	writer.writeNext(toAdd);
            	}
                else
                {
                	String[] toAdd1 = new String[numCols +1];
                	for (int j = 0; j< numCols; j++)
                	{
                		toAdd1[j] = header[j];
                	}
                	toAdd1[numCols]= addedColumn;
                	
                	for(int x = 0; x < numCols+1; x ++)
                	{
                		System.out.print(toAdd1[x] + ",");
                	
                	}
                	System.out.println();
                	
                	writer.writeNext(toAdd1);
                }
                i++;
                header = reader.readNext();
            }
            reader.close();
            writer.close();
        }catch(Exception e){
            System.out.println(e);
        }

    }	

/*    public void addColumn(String fileName, String fileName1, String[] nums, boolean database) throws IOException{
        BufferedReader br=null;
        BufferedWriter bw=null;
        final String lineSep=System.getProperty("line.separator");

        try {
            File file = new File(fileName);
            File file2 = new File(fileName1);//so the
                        //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = null;
                        int i=0;  
  //skip first 3 lines
           if (database == false) {
	            String line1 = br.readLine();
	            bw.write(line1+lineSep);
	            String line2 = br.readLine();
	            bw.write(line2+lineSep);
	            String line3 = br.readLine();
	            bw.write(line3+"Song ID,Shazams" + lineSep);
            }
            else {}
            int numCols = countCols(fileName);
            System.out.println("number of cols: " + numCols);
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            { 
 //           	System.out.println(line);
                String addedColumn = String.valueOf(nums[i]);
                
                String[] cells = line.split(",");
                
                if(cells.length == 3)
            	{
                	//line = "";
                	for(int j = 3; j < numCols; j++)
                	{
                		line = line.concat(",");
                	}
            	}
                bw.write(line + "," + addedColumn+lineSep); //add comma between?
            }

        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }

    }	*/
    
    public void copyOver(String srcFile, String fileName) throws IOException
    {
    	File file = new File(fileName);
    	FileOutputStream os = new FileOutputStream(file);
    	Files.copy(Paths.get(srcFile), os);
    }
   
    public void addCol (String fileName, String[] toAdd) throws IOException
    {
    	
      	OutputStreamWriter fw=null;
        BufferedWriter bw=null;
        InputStreamReader fr = null;
        BufferedReader br = null;
        String s;
        final String lineSep=System.getProperty("line.separator");
    	try
        {
    		File file = new File(fileName);
            fw= new OutputStreamWriter(new FileOutputStream(file));
            bw=new BufferedWriter(fw);
            try
            {
                fr=new InputStreamReader(new FileInputStream(file));
                br=new BufferedReader(fr);
                
 /*               if (database == false) {
    	            String line1 = br.readLine();
    	            bw.write(line1+lineSep);
    	            String line2 = br.readLine();
    	            bw.write(line2+lineSep);
    	            String line3 = br.readLine();
    	            bw.write(line3+"Song ID,Shazams" + lineSep);
                }else {}*/
                int i = 0;
                while((s=br.readLine())!=null)
                {
//                	String addedColumn = String.valueOf(toAdd[i]);
                	String addedColumn = toAdd[i];
                    if(s.equals(""))
                 	{
                 		s = ",";
                 	}
                    bw.write(s+ "," + addedColumn+lineSep);
                    
                    i++;
                	
                	s = br.readLine();
                }
                //bw.write(toAdd);
                br.close();
            }
            catch(FileNotFoundException e)
            {
                System.out.println("File was not found");
            }
            catch(IOException e)    
            {
                System.out.println("No file found");
            }

            bw.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error1");
        }
    }    
    
    public int findNumRows(String fileName) throws IOException
    {
		BufferedReader br = null;
    	
    		int count = 0;
	    		br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
	    		String input;
		        while((input = br.readLine()) != null)
		        {
		            count++;
		        }
		        br.close();
		        return count;
    }
    
/*    public void addRow(String fileName, String fileName1, String toAdd) throws IOException
    {
        BufferedReader br=null;
        BufferedWriter bw=null;
        final String lineSep=System.getProperty("line.separator");     

        try {
            File file = new File(fileName);
            File file2 = new File(fileName1);
           
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
          //  String line = null;
            int i = 0;*/
 /*           for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {
                bw.write(line); //add comma between?
            }*/
/*            String line = br.readLine();
            while( line != null)
            {
            	bw.write(line + "\n");
            	line = br.readLine();
            }
            bw.write(toAdd);
        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }
        
    }*/
    
    public void addSong(String fileName, String toAdd)throws IOException
    {
    	try {
	    	FileWriter writer = new FileWriter(fileName, true);
	    	StringBuilder sb = new StringBuilder();
	    	sb.append(toAdd);
	    	sb.append('\n');
	    	writer.write(sb.toString());
//	    	writer.flush();
	    	writer.close();
    	}catch(FileNotFoundException e) {
    		System.out.println(e.getMessage());
    	}
    }
    
    public void addRow(String fileName, String toAdd) throws IOException
    {
    	OutputStreamWriter fw=null;
        BufferedWriter bw=null;
        InputStreamReader fr = null;
        BufferedReader br = null;
        String s;
    	try
        {
    		File file = new File(fileName);
            fw= new OutputStreamWriter(new FileOutputStream(file));
            bw=new BufferedWriter(fw);

            try
            {
                fr=new InputStreamReader(new FileInputStream(file));
                br=new BufferedReader(fr);

                while((s=br.readLine())!=null)
                {
                	bw.write(s + "\n"); 
                	
                	s = br.readLine();
                }
                //bw.write(toAdd);
                br.close();
            }
            catch(FileNotFoundException e)
            {
                System.out.println("File was not found");
            }
            catch(IOException e)    
            {
                System.out.println("No file found");
            }

            bw.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("Error1");
        }
    }
    
    
    public String[] colToArr(String fileName, int colNum) throws IOException, CsvException {  //column of csv to array
    	String[] lineInArray = new String[findNumRows(fileName)];
         try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
        	 int i = 0;
        	 String[] fullRow = reader.readNext();     // and there is a (header) line
        	 if(fullRow == null)
        	 {
        		 return null;
        	 }
          	   int columnCount = fullRow.length;       // get the column count
        	
        	// int numCols = columnCount;
             while (fullRow != null) {
            	// System.out.println(i);
            	 
            	 if (fullRow[colNum] == null){
            		 lineInArray[i]= "0";
            	 }else {
            		 lineInArray[i] = fullRow[colNum]; //if empty, add 0
            	 }
//            	 System.out.println(i);
   //         	 System.out.println(lineInArray[i]); 
            	 i++;
            	 fullRow = reader.readNext();
             }
             reader.close();
         }
         return lineInArray;

     }	
    
    public int searchArray(String[] arr, int length, String toFind)
    {
    	int index = -1;
    	for (int i=0;i<length;i++) {
    	    if (arr[i].equals(toFind)) {
    	        index = i;
    	        break;
    	    }
    	}
    	return index;
    }
    
/*    public String[] yesterdaysSongs(String file, String date, int column) throws IOException, CsvException
    {
    	String[] lineInArray = new String[findNumRows(file)];
    	 
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
       	 int i = 0;
       	 String[] fullRow = reader.readNext();
       	 while(fullRow != null)
       	 {
	       	 if(fullRow[0].equals(date))
	       	 {
	       		 i++;
	       	 }
	       	fullRow = reader.readNext();
       	 }
       	String[] songs = new String[i];
       	String[] fullRow1 = reader.readNext();
       	 for (int j = 0; j<i; j++)
       	 {
	       	if(fullRow1[0].equals(date))
	      	 {
	      		 songs[i] = fullRow[column];
	      		 i++;
	      		fullRow = reader.readNext();
	      	 }
	       	else 
	       	{
	       		fullRow = reader.readNext();
	       	}
       	 }
       	 reader.close();
       	 return songs;
        }
    }*/
    public String[] yesterdaysSongs(String file, String date, int column) throws IOException, CsvException
    {
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
       	 int i = 0;
         String[] fullRow = reader.readNext();
       	 while(fullRow != null)
       	 {
	       	 if(fullRow[0].equals(date))
	       	 {
	       		 i++;
	       	 }
	       	fullRow = reader.readNext();
       	 }
       	 String[] songs = new String[i];
       	 CSVReader reader1 = new CSVReader(new FileReader(file));
       	 String[] fullRow1 = reader1.readNext();
       	 int j = 0;
       	 while(fullRow1 != null)
       	 {
	       	 if(fullRow1[0].equals(date))
	       	 {       		 
	       		 songs[j] = fullRow1[column];
	       		 j++;
	       	 }
	       	 
	       	fullRow1 = reader1.readNext();
       	 }
       	 reader.close();
       	 return songs;
        }
    }
	
}
