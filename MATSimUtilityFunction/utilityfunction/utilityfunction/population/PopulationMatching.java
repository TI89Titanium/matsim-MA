package utilityfunction.population;

import java.util.List;

import org.matsim.api.core.v01.Scenario;


public class PopulationMatching {
	
	public static final String[] columnSelection = {"UserID", "DTWmax","MOS3","MOS4", "SOC6", "MPT"};
	MATSimPopulation matSimPopulation;
	
	public Scenario matchPopulation(String configFile, String csvFile) {

		//prepare Data - input: raw survey data
		SurveyData surveyData = new SurveyData(csvFile);
		
		//get Array with Population only with data in specific columns
		List <String[]> surveyPopulationList = surveyData.getSurveyPopulationList(columnSelection);
				
		//get population + get Data from Population (act + modes)
		this.matSimPopulation = new MATSimPopulation(configFile);
		
		matSimPopulation.addAttributes(surveyPopulationList);
		
		Scenario scenario = matSimPopulation.getScenario();
		
		System.out.println("----------------------------------------------");
		System.out.println("----------------------------------------------");
		System.out.println("Added additional Attributes to Attributes-File");
		System.out.println("----------------------------------------------");
		System.out.println("----------------------------------------------");
		
		return scenario;
		
	}
	
	//String selectedMode is the mode-id as used in the survey: e.g. "D" for car (self-driver)
	public Scenario matchPopulationSpecificModes(String configFile, String csvFile, String[] selectedModes) {

		//prepare Data - input: raw survey data
		SurveyData surveyData = new SurveyData(csvFile);
		
		//get Array with Population only with data in specific columns
		List <String[]> surveyPopulationList = surveyData.getSurveyPopulationList(columnSelection);
		//delete all entries with other modes
		surveyPopulationList = surveyData.getSurveyPopulationListSpecificModes(surveyPopulationList, 3, selectedModes);
		//get population + get Data from Population (act + modes)
		this.matSimPopulation = new MATSimPopulation(configFile);
		
		matSimPopulation.addAttributes(surveyPopulationList);
		
		Scenario scenario = matSimPopulation.getScenario();
		
		System.out.println("----------------------------------------------");
		System.out.println("----------------------------------------------");
		System.out.println("Added additional Attributes to Attributes-File");
		System.out.println("-------------Only selected Mode---------------");
		System.out.println("----------------------------------------------");
		
		return scenario;
		
	}
	
	public MATSimPopulation getMATSimPopulation(){
		return this.matSimPopulation;
	}
	
	
	
	
	

	
}
// TODO Constructor: get all variables and files necessary (maybe: get config file)

