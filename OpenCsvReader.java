package com.songdata;
import java.util.ArrayList;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.FileInputStream;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.*;


public class OpenCsvReader {
	
    public static void main(String[] args) throws IOException, CsvException, URISyntaxException {

        String fileName = "c:\\Users\\Winnie\\Downloads\\CSVDemo.csv";
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
 /*   
    public static List<String> getIds(String fileName) throws IOException, CsvException{
    	List<String> ids = new ArrayList<String>(200);
    	List<String[]> lines = readCsvFile(fileName); 
    	for(int i = 0; i< lines.size()-3; i++) {
    	   String[] line = lines.get(i+3);
 //   	   System.out.println(i);
 //    	   System.out.println(line[line.length-1]);
     	   ids.add(line[line.length-1]);
    	}
    	return ids;
    }
*/
    public void addColumn(String fileName, String[] nums) throws IOException{
        BufferedReader br=null;
        BufferedWriter bw=null;
        final String lineSep=System.getProperty("line.separator");

        try {
            File file = new File(fileName);
            File file2 = new File("c:\\Users\\Winnie\\Downloads\\ShazamChart6-10.csv");//so the
                        //names don't conflict or just use different folders

            br = new BufferedReader(new InputStreamReader(new FileInputStream(file))) ;
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file2)));
            String line = null;
                        int i=0;
          
           
  //skip first 3 lines
            String line1 = br.readLine();
            bw.write(line1+lineSep);
            String line2 = br.readLine();
            bw.write(line2+lineSep);
            String line3 = br.readLine();
            bw.write(line3+"Song ID,Shazams" + lineSep);
            for ( line = br.readLine(); line != null; line = br.readLine(),i++)
            {               

                String addedColumn = String.valueOf(nums[i]);
                System.out.println(line+ "," + addedColumn + lineSep);
                bw.write(line+ "," + addedColumn+lineSep); //add comma between?
        }

        }catch(Exception e){
            System.out.println(e);
        }finally  {
            if(br!=null)
                br.close();
            if(bw!=null)
                bw.close();
        }

    }	
	
	
	
	
}
