package utilityfunction.population;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.core.population.PlanImpl;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.objectattributes.ObjectAttributes;

public class MATSimPopulation {
	
	final Scenario scenario;
	final Population population;
	List<String[]> matchingList = new ArrayList<String[]>();
	
	public MATSimPopulation(Config config){
		//get population from configFile
		this.scenario = ScenarioUtils.loadScenario(config);
		this.population = scenario.getPopulation();
	}
	
	public Population getPopulation(){
		return this.population;
	}
		
	public List<String[]> getMATSimPopulationMatchingList(){
		//go through all Agents (person)
		for (Person person : scenario.getPopulation().getPersons().values()) {
			List <PlanElement> actsLegs = ((PlanImpl)(person.getSelectedPlan())).getPlanElements();
			double distanceToWork = 0;
			double longestDistance = 0;
			String mode = "";
			String lastMode ="";
			String currentMode ="";
			boolean hasHomeAct = false;
			boolean hasWorkAct = false;
			int numberOfLegs = 0;
			boolean modeIsCombination = false;
			//go through all Persons Plan, sum distanceToWork
			for (PlanElement actLeg : actsLegs){
				if (actLeg instanceof Activity){ //Plan Element is an Activity
					if (((Activity) actLeg).getType().contains("home")){ //check if activity is "home"-Activity
						distanceToWork = 0;
						hasHomeAct = true; //start adding 
					}
					else if(((Activity) actLeg).getType().contains("work")){
						hasWorkAct = true; //stop adding
						break; //stops going through Plan
					}
				}else if (actLeg instanceof Leg){
					if (hasHomeAct){
						numberOfLegs++; //count number of Legs to work
						double distance = ((Leg) actLeg).getRoute().getDistance();
						distanceToWork = distanceToWork + distance;
						currentMode = ((Leg) actLeg).getMode();
						if (distance >= longestDistance){
							longestDistance = distance;
							mode = currentMode;
						}
						//if more then one mode (excluding walking) is used to get to work, set mode to "combination"
						if (numberOfLegs > 1 && !currentMode.equals(lastMode) && !currentMode.contains("walk") && !lastMode.contains("walk")){
							modeIsCombination = true;
						}
						lastMode = currentMode;
					}
					
				}
				
			}
			if (hasHomeAct && hasWorkAct){
				//TODO save distancetoWork with Person-ID (List)
				//Characteristics (person ID, distance to work, main mode) saved in String-Array
				String[] characteristics = new String[3];
				matchingList.add(characteristics);
				characteristics[0] = person.getId().toString();
				characteristics[1] = String.valueOf(distanceToWork);
				if (modeIsCombination){ //set mode to "combination" if more than one mode (excluding walking) is used
					characteristics[2] = "combination";
				}
				else{
					characteristics[2] = mode;
				}
				
			}
		}
		return matchingList;
	}
	
	
	public ObjectAttributes addAttributes(List <String[]> surveyPopulationList){
		
		// surveyPopulationList needs to be {"UserID", "DTWmax","MOS3","MOS4", "SOC6", "MPT"}
		// MOS3: Distance to Work A through F
		// MOS4: Main Mode to Work A though H
		// SOC6: monthly income A through F
		// MPT: ModePreferenceType (based on usage): {"Pragmatic Long-Distance Driver", "Auto Addicted", "Active Public Transportation Lover", "Lazy Public Transportation Lover", "Active Auto Lover"}
		// TODO add attributes population input: pop-data + survey data
		
//		public static void addAttributesToMATSimPop (Population population, List<String[]> surveyPopulationList){
			
//		}
		matchingList = this.getMATSimPopulationMatchingList(); //MATSimPopulation List needs to be: {"ID", "DistanceToWork", "ModeToWork"}
		final ObjectAttributes personAttributes = population.getPersonAttributes();
		final String DAILY_INCOME = "DAILY_INCOME";
		final String MPT = "MPT";
		
		int noMatches = 0;
		int oneMatch = 0;
		int moreThanOneMatch = 0;
		
		for (String[] agent : matchingList){
			//go through all survey Pop and find all matching -> number of matches
			int modeNumberMATSim = this.setModeNumberMATSim(agent[2]); //mode information
			int numberOfMatches = 0;
			
			List<Double> dailyIncome = new ArrayList<Double>();
			
			List<String> modePreferenceType = new ArrayList<String>();
			
			for (String[] person: surveyPopulationList){
				int modeNumberSurvey = this.setModeNumberSurvey(person[3]); //mode: "MOS4"
				if (modeNumberSurvey == modeNumberMATSim){
					//cast String to double and compare distance "MOS3"
					if(this.compareDistance(agent[1], person[2])){
						numberOfMatches++;
						//save Attributes to List : income, mode-Preference-Type
						
						dailyIncome.add(this.getDailyIncome(person[4],1)); //set number of persons in HH to 1
						
						modePreferenceType.add(person[5]); //set up survey with column MPT
						
					}
					
				}
				
			}
			if (numberOfMatches == 0){
				//set attributes random over all survey pop
				Random randomGenerator = new Random(); 
				int index = randomGenerator.nextInt(surveyPopulationList.size());
				personAttributes.putAttribute(agent[0], DAILY_INCOME, this.getDailyIncome(surveyPopulationList.get(index)[4],1)); //one person in houshold
				personAttributes.putAttribute(agent[0], MPT, this.getMPT(surveyPopulationList.get(index)[5]));
				noMatches++;
			}
			else if (numberOfMatches == 1){
				//set Attributes from specific surveyPerson
				personAttributes.putAttribute(agent[0], DAILY_INCOME, dailyIncome.get(0));
				personAttributes.putAttribute(agent[0], MPT, this.getMPT(modePreferenceType.get(0)));
				oneMatch++;
			}
			else if (numberOfMatches > 1){
				//set Attributes random over matching survey Pop
//				dailyIncome.removeIf(Objects::isNull); //remove "null" elements (not necessary)
				Random randomGenerator = new Random(); 
				int index = randomGenerator.nextInt(dailyIncome.size()); //generates random Integer between 0 (included) and size of List dailyIncome
				personAttributes.putAttribute(agent[0], DAILY_INCOME, dailyIncome.get(index));
				personAttributes.putAttribute(agent[0], MPT, this.getMPT(modePreferenceType.get(index)));
				moreThanOneMatch++;
			}
			//reset (empty) Attribute-List
//			System.out.println(numberOfMatches);
		}
	
		System.out.println("Number of Agents w/o matches in Survey: " + noMatches);
		System.out.println("Number of Agents w/ one match in survey: " + oneMatch);
		System.out.println("Number of Agents w/ more than one match in survey: " + moreThanOneMatch);
		
		
		int allreadyhasIncomeAttribute = 0;
		//add Attributes random to other population
		for (Person person : population.getPersons().values()) {
			Random randomGenerator = new Random(); 
			int index = randomGenerator.nextInt(surveyPopulationList.size());
			if (personAttributes.getAttribute(person.getId().toString(), DAILY_INCOME) == null) { //person has no income attribute
				
				personAttributes.putAttribute(person.getId().toString(), DAILY_INCOME, this.getDailyIncome(surveyPopulationList.get(index)[4],1));
			}
			else{
				allreadyhasIncomeAttribute++;
			}
			if (personAttributes.getAttribute(person.getId().toString(), MPT) == null){ //person has MPT attribute
				personAttributes.putAttribute(person.getId().toString(), MPT, this.getMPT(surveyPopulationList.get(index)[5]));
			}
		}
		System.out.println("agents already having an income Attribute (must be equal to number of agents having home-work info): " + allreadyhasIncomeAttribute);
		
		return personAttributes; //maybe void (just generating/adding to the attribute xml-file
	}
	
	//monthlyIncome times 12 divided by 240 (working days) 
	//(can also be divided by number of household members if information is available)
	private Double getDailyIncome(String income, int personsInHoushold) {
		double dailyIncome = -1;
		if (income.equals("A")){
			dailyIncome = (double) 500 * 12 / (personsInHoushold * 240);
			}
		else if (income.equals("B")){
			dailyIncome = (double) 1500 * 12 / (personsInHoushold * 240);
		}
		else if (income.equals("C")){
			dailyIncome = (double) 2500 * 12 / (personsInHoushold * 240);
		}
		else if (income.equals("D")){
			dailyIncome = (double) 3500 * 12 / (personsInHoushold * 240);
		}
		else if (income.equals("E")){
			dailyIncome = (double) 4500 * 12 / (personsInHoushold * 240);
		}
		else if (income.equals("F")){
			dailyIncome = (double) 5500 * 12 / (personsInHoushold * 240);
		}
		else{
			dailyIncome = -1000;
		}
		return dailyIncome;
}
	//{"Pragmatic Long-Distance Driver", "Auto Addicted", "Active Public Transportation Lover", "Lazy Public Transportation Lover", "Active Auto Lover"}
	private String	getMPT(String mptCode){
		String mpt = "";
		if (mptCode.equals("A")){
			mpt = "PragmaticLongDistanceDriver";
			}
		else if (mptCode.equals("B")){
			mpt = "AutoAddicted";
		}
		else if (mptCode.equals("C")){
			mpt = "ActivePublicTransportationLover";
		}
		else if (mptCode.equals("D")){
			mpt = "LazyPublicTransportationLover";
		}
		else if (mptCode.equals("E")){
			mpt = "ActiveAutoLover";
		}
		return mpt;
	}

	private int setModeNumberMATSim (String mode){
		//declare mode-compare Integer 1 - "walk", 2 - "bike", 3 - "pt", 4 - "car", 5 - "ride", 6 - "train", 7- "combination", 8 - "other"};
		int modeNumber = 0;
		if (mode.equals("walk") || mode.equals("transit_walk")){
			modeNumber = 1;
		}
		else if (mode.equals("bike")){
			modeNumber = 2;
		}
		else if (mode.equals("colectivo")||mode.equals("pt")){
			modeNumber = 3;
		}
		else if (mode.equals("car")){
			modeNumber = 4;
		}
		else if (mode.equals("ride")||mode.equals("taxi")){
			modeNumber = 5;
		}
		else if (mode.equals("train")){
			modeNumber = 6;
		}
		else if (mode.equals("combination")){
			modeNumber = 7;
		}
		else if (mode.equals("other")){
			modeNumber = 8;
	}
		return modeNumber;
}
	private int setModeNumberSurvey (String mode){
		//declare mode-compare Integer 1 - "walk", 2 - "bike", 3 - "pt", 4 - "car", 5 - "ride", 6 - "train", 7- "combination", 8 - "other"}
		
		int modeNumber = 0;
		if (mode.equals("A")){
			modeNumber = 1;
		}
		else if (mode.equals("B")){
			modeNumber = 2;
		}
		else if (mode.equals("C")){
			modeNumber = 3;
		}
		else if (mode.equals("D") || mode.equals("F")){ //motorbike counts as car
			modeNumber = 4;
		}
		else if (mode.equals("E")){
			modeNumber = 5;
		}
		else if (mode.equals("G")){
			modeNumber = 6;
		}
		else if (mode.equals("H")){
			modeNumber = 7;
		}
		
		//no mode "other" in survey
//		else if (mode.equals("other")){
//			modeNumber = 8;

		return modeNumber;
	}
	
	private boolean compareDistance (String distanceMATSim, String distanceRangeSurvey){
		boolean sameDistance = false;
		double distanceMATSimValue = Double.parseDouble(distanceMATSim);
		double lowerBound;
		double upperBound;
		
		//set bounds for survey-answers and compare to distanceMATSimValue
		if (distanceRangeSurvey.equals("A")){
			upperBound = 1;
			if (distanceMATSimValue <= upperBound){
				sameDistance = true;
			}
		}
		else if (distanceRangeSurvey.equals("B")){
			lowerBound = 1;
			upperBound = 5;
			if (distanceMATSimValue > lowerBound || distanceMATSimValue <= upperBound){
				sameDistance = true;
			}
		}
		else if (distanceRangeSurvey.equals("C")){
			lowerBound = 5;
			upperBound = 10;
			if (distanceMATSimValue > lowerBound || distanceMATSimValue <= upperBound){
				sameDistance = true;
			}
		}
		else if (distanceRangeSurvey.equals("D")){
			lowerBound = 10;
			upperBound = 30;
			if (distanceMATSimValue > lowerBound || distanceMATSimValue <= upperBound){
				sameDistance = true;
			}
		}
		else if (distanceRangeSurvey.equals("E")){
			lowerBound = 30;
			upperBound = 50;
			if (distanceMATSimValue > lowerBound || distanceMATSimValue <= upperBound){
				sameDistance = true;
			}
		}
		else if (distanceRangeSurvey.equals("F")){
			lowerBound = 50;
			if (distanceMATSimValue > lowerBound){
				sameDistance = true;
			}
		}
		
		return sameDistance;
		
		
	}

	public Scenario getScenario() {
		return this.scenario;
	}
	
	public int getAttributeStats (String attribute, String value){
		int valueCount = 0;
		final ObjectAttributes personAttributes = population.getPersonAttributes();
		final String DAILY_INCOME = "DAILY_INCOME";
		final String MPT = "MPT";
		
		if (attribute.equals(DAILY_INCOME)){
			double askedValue = Double.parseDouble(value);
			for (Person person : population.getPersons().values()) {
				double dailyIncome = (double) personAttributes.getAttribute(person.getId().toString(), DAILY_INCOME);
				if(dailyIncome == askedValue){
					valueCount++;
				}
			}
			
		}
		else if (attribute.equals(MPT)){
			String askedValue = value;
			for (Person person : population.getPersons().values()) {
				String mpt = (String) personAttributes.getAttribute(person.getId().toString(), MPT);
				if(mpt.equals(askedValue)){
					valueCount++;
				}
				}
			//System.out.println("MPT-Stats is not yet implemented, Sorry!");
		}
		else{
			System.out.println("Stats only available for Attribute DAILY_INCOME and MPT");
			valueCount = 0;
		}
		return valueCount;
	}
}
