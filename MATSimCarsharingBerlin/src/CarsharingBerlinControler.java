import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Activity;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.carsharing.config.CarsharingConfigGroup;
import org.matsim.contrib.carsharing.control.listeners.CarsharingListener;
import org.matsim.contrib.carsharing.qsim.CarsharingQsimFactory;
import org.matsim.contrib.carsharing.replanning.CarsharingSubtourModeChoiceStrategy;
import org.matsim.contrib.carsharing.replanning.RandomTripToCarsharingStrategy;
import org.matsim.contrib.carsharing.runExample.CarsharingUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.population.PlanImpl;

import utilityfunction.config.UtilityFunctionUtils;
import utilityfunction.population.PopulationMatching;

public class CarsharingBerlinControler {

	public static void main(String[] args) {
		
		final String configFile = "input/config.xml";
		final String csvFile = "input/MIWDataRaw_MTP_new.csv";
		
		Config config = ConfigUtils.loadConfig(configFile);
		
		//add new Modules
		//Utilityfunction-Modules
		UtilityFunctionUtils.addConfigModules(config);
		
		//Carsharing-Modules
		if(Integer.parseInt(config.getModule("qsim").getValue("numberOfThreads")) > 1)
			Logger.getLogger( "org.matsim.core.controler" ).warn("Carsharing contrib is not stable for parallel qsim!! If the error occures please use 1 as the number of threads.");
		
		
		
		CarsharingUtils.addConfigModules(config);
		
		//Prepare PopulationMatching
		PopulationMatching populationMatching = new PopulationMatching();
		String selectedModes[] = {"D", "E"};
		//Match Population with Survey (specific modes car and ride)
		final Scenario scenario = populationMatching.matchPopulationSpecificModes(config, csvFile, selectedModes);
		
		//Create Carsharing Stations
		int numberOfStations = 3000;
		createCarsharingStations(scenario, numberOfStations);
				
		//controler for MATSim
		Controler controler = new Controler(scenario);
		
		//set up new scoring function WolfLegScoring and carsharingLegScoring with function factory;
		controler.setScoringFunctionFactory(new CarsharingBerlinFunctionFactory(scenario));
		
		installCarSharing(controler);
		
		//run MATSim
		controler.run();
		
		//TODO: run stats module

	}
	
	public static void installCarSharing(final Controler controler) {
		
		Scenario sc = controler.getScenario() ;
		
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				this.addPlanStrategyBinding("RandomTripToCarsharingStrategy").to( RandomTripToCarsharingStrategy.class ) ;
				this.addPlanStrategyBinding("CarsharingSubtourModeChoiceStrategy").to( CarsharingSubtourModeChoiceStrategy.class ) ;
			}
		});
		
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bindMobsim().toProvider( CarsharingQsimFactory.class );
			}
		});

		controler.setTripRouterFactory(CarsharingUtils.createTripRouterFactory(sc));
		
		final CarsharingConfigGroup csConfig = (CarsharingConfigGroup) controler.getConfig().getModule(CarsharingConfigGroup.GROUP_NAME);
		controler.addControlerListener(new CarsharingListener(controler,
				csConfig.getStatsWriterFrequency() ) ) ;
	}
	
	public static void createCarsharingStations(Scenario sc, int numberOfStations){
		
		if (numberOfStations > sc.getPopulation().getPersons().size()){
			System.out.println("Please enter number of Carsharing Stations smaler then number of agents. Number of Stations is set to number of agents.");
			numberOfStations = sc.getPopulation().getPersons().size();
		}
		
		try{
		File file = new File("input/Stations.txt");
		
				
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		String[] columns = {"Location[0]","Station-No.[1]","GeoX[2]","GeoY[3]","North[4]","East[5]","VehicleCount[6]","OpenParkingSpots (used only for onewaycarsharing)[7]"};
		for (String c : columns){
			bw.write(c);
			bw.write("\t");
		}
		bw.newLine();
		int stationCount = 1;
		List<String[]> stations = new ArrayList<String[]>();
		
		for (Person person : sc.getPopulation().getPersons().values()){
			String[] stationInformation = new String[7];
			stationInformation[0] = Integer.toString(stationCount);
			stationInformation[1] = stationInformation[0];
			Activity firstActivity = ((PlanImpl)(person.getSelectedPlan())).getFirstActivity();
			stationInformation[2] = Double.toString(firstActivity.getCoord().getX());
			stationInformation[3] = Double.toString(firstActivity.getCoord().getY());
			stationInformation[4] = "-";
			stationInformation[5] = "-";
			stationInformation[6] = "1";
			
			stations.add(stationInformation);
			stationCount++;
		}
		
		//randomly distribute the stations, so if the wanted number of stations
		//is lower then number of agents, the carsharing stations are distributed
		//randomly
		Collections.shuffle(stations);
		
		int i = 0;
		while (i < numberOfStations) {//end when wanted number of stations is reached
			for (String stI : stations.get(i)){
			bw.write(stI);
			bw.write("\t");
			}
		bw.newLine();
		i++;
		}
		
		bw.close();
				
		}	catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
