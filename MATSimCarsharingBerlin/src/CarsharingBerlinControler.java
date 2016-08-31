import org.matsim.api.core.v01.Scenario;
import org.matsim.core.controler.Controler;

import utilityfunction.population.PopulationMatching;

public class CarsharingBerlinControler {

	public static void main(String[] args) {
		
		final String configFile = "input/config.xml";
		final String csvFile = "input/MIWDataRaw.csv";
		
		//Prepare PopulationMatching
		PopulationMatching populationMatching = new PopulationMatching();
		String selectedModes[] = {"D", "E"};
		//Match Population with Survey (specific modes car and ride)
		Scenario scenario = populationMatching.matchPopulationSpecificModes(configFile, csvFile, selectedModes);
		
		//Config config = ConfigUtils.loadConfig(configFile);
		
		Controler controler = new Controler(scenario);
		controler.run();
		

	}

}
