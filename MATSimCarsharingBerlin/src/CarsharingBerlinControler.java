import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;

import utilityfunction.config.UtilityFunctionUtils;
import utilityfunction.population.PopulationMatching;

public class CarsharingBerlinControler {

	public static void main(String[] args) {
		
		final String configFile = "input/config.xml";
		final String csvFile = "input/MIWDataRaw.csv";
		
		Config config = ConfigUtils.loadConfig(configFile);
		
		//add new Modules
		//Utilityfunction-Modules
		UtilityFunctionUtils.addConfigModules(config);
		
		//Prepare PopulationMatching
		PopulationMatching populationMatching = new PopulationMatching();
		String selectedModes[] = {"D", "E"};
		//Match Population with Survey (specific modes car and ride)
		Scenario scenario = populationMatching.matchPopulationSpecificModes(config, csvFile, selectedModes);
		
		
		
		Controler controler = new Controler(scenario);
		controler.run();
		

	}

}
