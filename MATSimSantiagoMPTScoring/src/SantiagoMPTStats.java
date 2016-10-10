import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.PlanElement;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.population.PlanImpl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.utils.objectattributes.ObjectAttributes;

public class SantiagoMPTStats {
	
	
	final Config config;
	final Scenario scenario;
	final Population population;
	final ObjectAttributes personAttributes;
	
	SantiagoMPTStats(String configFile) {			
	this.config = ConfigUtils.loadConfig(configFile);
	this.scenario = ScenarioUtils.loadScenario(config);
	this.population = scenario.getPopulation();
	this.personAttributes = population.getPersonAttributes();
	}

public List<String[]> getStatsList(){
	
	List<String[]> populationStats= new ArrayList<String[]>();
	
	for (Person person : population.getPersons().values()) {
		String[] personStats = new String[4];
		personStats[0] = person.getId().toString();
		personStats[1] = personAttributes.getAttribute(person.getId().toString(), "MPT").toString();
		personStats[2] = getMode(person);
		personStats[3] = person.getSelectedPlan().getScore().toString();
		
		populationStats.add(personStats);
		
	}
	
	return populationStats;
}

private String getMode(Person person) {
	List <PlanElement> actsLegs = ((PlanImpl)(person.getSelectedPlan())).getPlanElements();
	String mode = "";
	String currentMode ="";
	String lastMode ="";
	int numberOfLegs = 0;
	double longestDistance = 0.0;
	boolean modeIsCombination = false;
	
	for (PlanElement actLeg : actsLegs){
		if (actLeg instanceof Leg){
			numberOfLegs++; //count number of Legs
			double distance = ((Leg) actLeg).getRoute().getDistance();
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
	if (modeIsCombination){
		mode = "combination";
	}
	return mode;
}

public double getOverallScore(Population population){
	double overallScore = 0.0;
	
	for (Person person : population.getPersons().values()) {
		overallScore += (person.getSelectedPlan()).getScore();
	}
		
	
	return overallScore;
	
}
public double getPlanScore(String personID){
	Person person = population.getPersons().get(personID);
	double score;
	score = person.getSelectedPlan().getScore();
	return score;
}

public void writeStatsToFile (String folder, List<String[]> populationStats){
	try{
		
		File file = new File(folder + "/Stats.csv");
		
		String csvSplitBy = ";";
				
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		String[] columns = {"id","mobility type","selected mode","score"};
		for (String c : columns){
			bw.write(c);
			bw.write(csvSplitBy);
		}
		bw.newLine();
		
		for (String[] personStats : populationStats){
			for (String attribute : personStats){
				bw.write(attribute);
				bw.write(csvSplitBy);

			}
			bw.newLine();
		}
						
		bw.close();
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	System.out.println("---------------------------------------------");
	System.out.println("---------------------------------------------");
	System.out.println("Stats have been written to the Stats.csv-File");
	System.out.println("---------------------------------------------");
	System.out.println("---------------------------------------------");
}
}
