package utilityfunction.population;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


public class SurveyData {

	//declaration
	String csvFile;
	BufferedReader br = null;
    String line = "";
    String cvsSplitBy = ";"; //use semicolon as separator
    //survey-population with id, distance to work, main-mode
    String [] columnName;
    String[][] surveyPopulation;
    List <String[]> surveyPopulationList;
    
    //private final ObjectAttributes personAttributes = new ObjectAttributes(); //Attributes (distance to work, main-mode)
    //Map<Id<Person>, ObjectAttributes> surveyPopulation = new Map<Id<Person>, ObjectAttributes>();


	//constructor (input file (String))
	public SurveyData(String csvFile){
		this.csvFile = csvFile;
		this.surveyPopulation = readFile();
		this.surveyPopulationList = Arrays.asList(surveyPopulation);

        
	}
	//read file 
	//read file save in array or hasmap
	private String[][] readFile(){
		
		int numberOfLines = 0;
		//get number of lines (persons)
		try {

	        br = new BufferedReader(new FileReader(csvFile));
	                  	        	        
	        while (((line = br.readLine()) != null)) {   
	        	numberOfLines++;
	    	 }
	        

	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }	
		
		surveyPopulation = new String[numberOfLines-1][];//initialise without columnName-Line
		
	try {

        br = new BufferedReader(new FileReader(csvFile));
        
        
        int i = 0;
        
        	        
        while (((line = br.readLine()) != null)) {

        	if (i==0){
        		columnName = line.split(cvsSplitBy); //use semicolon as separator
        	}
        	else {
        		surveyPopulation[i-1] = line.split(cvsSplitBy);
        	}
        
        	
        	i++;
    	 }
        

    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
	
	return surveyPopulation;
	
	}
	
	//getter (return String Array)
	public String[][] getSurveyPopulationArray(){
		//surveyPopulation = translatePopulationData(surveyPopulation);
		return this.surveyPopulation;
	}
	
	public List<String[]> getSurveyPopulationList(String[] columnSelection){
		int numberOfColumns = columnSelection.length;
		int numberOfRows = surveyPopulation.length;
		Integer [] columnIdx = new Integer[numberOfColumns];
	//	String [][] surveyPopulationColumnSelection = new String[numberOfRows][numberOfColumns]; //obsolete due to conversion to List
		List <String[]> surveyPopulationColumnSelectionList = new ArrayList<String[]>();
		//Search for String (columnName)
		for (int i=0; i<numberOfColumns; i++){
			columnIdx[i] = Arrays.asList(columnName).indexOf(columnSelection[i]);
		}
		
		
		//fill Selection List:
		int k = 0; //counter for selectionList
		
		for (int i=0; i<numberOfRows;i++){
			String[] RowArray = new String[numberOfColumns]; //initialize new array for each row/List-Element
			surveyPopulationColumnSelectionList.add(k,RowArray);
			for (int j=0; j<numberOfColumns; j++){
				
				//don't add List-Element when Column is not filled with the information in this row
				//TODO: make robust version, so it works and maybe throws exception
				if(surveyPopulationList.get(i).length<columnIdx[j]){
					surveyPopulationColumnSelectionList.remove(k);
					k--;
					break; //go to next row
				}
				//fill List-Element with Data from surveyPopulation Array
				else{
					RowArray[j] = surveyPopulation[i][columnIdx[j]];
				}
			}
			k++;
		}
		
	
		if(columnSelection[0].equals("UserID")){
			surveyPopulationColumnSelectionList = translatePopulationData(surveyPopulationColumnSelectionList);
		}
		
		//delete rows with missing elements

		//		surveyPopulationColumnSelection = ArrayUtils.remove(surveyPopulationColumnSelection,i);
		
		return surveyPopulationColumnSelectionList;
	}
	
	//special methods when matching with MATSim-Population with only one mode - to add more realistic attributes
	//(assumption: mode selection depends on income and mobility type
	//String selectedMode is the mode-id as used in the survey: e.g. "D" for car (self-driver)
	public List<String[]> getSurveyPopulationListSpecificModes(List <String[]> surveyPopulationList, int modeColumn, String[] selectedModes){
		//int listIndex = 0;
		
		Iterator<String[]> i = surveyPopulationList.iterator();
		boolean hasSelectedMode = false;
		while (i.hasNext()) {
		   String[] parameters = i.next();
		   for (String mode : selectedModes){
		   if (parameters[modeColumn].equals(mode)){
			   hasSelectedMode = true;
		   }
		   }
		   if (!hasSelectedMode){
			   i.remove();
		   }
		   hasSelectedMode = false;
		}
		/*
		for (String[] parameters : surveyPopulationList){ //parameters are matching parameters and attribute parameters
			if (!parameters[modeColumn].equals(selectedMode)){
				surveyPopulationList.remove(listIndex);
				listIndex--;
			}
			listIndex++;
		}
		*/
		
		
		return surveyPopulationList;
	}
	
	public String[] getSurveyPopulationColumnNameArray(){
		return this.columnName;
	}
	
	private List<String[]> translatePopulationData(List<String[]> surveyPopulation){
		
		//output: MatSim Population File
		
		//add sufix to ID (first column)
		String surveyPopulationPrefix = "SP";
		
		for(String[] row : surveyPopulation){
			row[0] = surveyPopulationPrefix + row[0];
		}
		
		
		return surveyPopulation;
	}

		
	
	//setter
	
}
