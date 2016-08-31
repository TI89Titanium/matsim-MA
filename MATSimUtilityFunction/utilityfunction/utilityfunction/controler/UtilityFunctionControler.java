package utilityfunction.controler;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.StrategyConfigGroup.StrategySettings;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.MatsimServices;
import org.matsim.core.replanning.strategies.DefaultPlanStrategiesModule;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.CharyparNagelScoringParameters;
import org.matsim.utils.objectattributes.ObjectAttributes;

import utilityfunction.constants.UtilityFunctionConstants;
import utilityfunction.population.PopulationMatching;
import utilityfunction.scoring.function.WolfLegScoring;


public class UtilityFunctionControler {
	
	private static String inputPath = "../MATSimUtilityFunction/input/";
	public static final String csvFileMatching = "C:/Users/Maximilian/Dropbox/01_KIT/Abschlussarbeit/UtilityMobility/Files/MIWDataRaw.csv";
	public static final String configFileMatching = "../MATSimUtilityFunction/input/config_popmatching.xml";

//	private static boolean doModeChoice = true;
	
	private static String configFile;
	private static Boolean doMatching;
	
	public static void main(String[] args) {


		if(args.length==0){
			// use default config file
			configFile = inputPath + "config_3agents.xml";
			doMatching = false;

		} else if (args.length == 1){
			configFile = args[0];
			doMatching = false;
		} else if (args.length == 2){
			configFile = args[0];
			if (args[1].equals("true")){
			doMatching = true;
			} else{
				doMatching = false;
			}
		}
		
		Config config = ConfigUtils.loadConfig(configFile);
		
		final Scenario scenario;
		
		if(doMatching){
		//add agent characteristics/attributes via PopulationMatching input:configFile + csvFile; return scenario file with changed attribute-Infos
		PopulationMatching populationMatching = new PopulationMatching();
		scenario = populationMatching.matchPopulation(config, csvFileMatching);
		}
		else {
			scenario = ScenarioUtils.loadScenario(config);
		}
		
		final ObjectAttributes personAttributes = scenario.getPopulation().getPersonAttributes();
		
		Controler controler = new Controler(scenario);
	
//TODO: define routing for other modes beside car or define other modes
		// first try agents with "normal" matsim-modes
		setNetworkModeRouting(controler);
		
//TODO: adding pt fare
		
		// adding basic strategies for car and non-car users
		setBasicStrategiesForSubpopulations(controler);
		
//TODO: mapping agents' activities to links on the road network to avoid being stuck on the transit network
		


		// add new scoring function via ScoringFunctionFactory Interface.
		// Do this as an anonymous class, so it has access to all variables and methods in this class.
		controler.setScoringFunctionFactory(new ScoringFunctionFactory() {
			
			@Override
			public ScoringFunction createNewScoringFunction(Person person) {
				SumScoringFunction sumScoringFunction = new SumScoringFunction();

				// Score activities, legs, payments and being stuck
				// with the default MATSim scoring (formulated by Charypar and Nagel) based on utility parameters in the config file.
				final CharyparNagelScoringParameters params =
						new CharyparNagelScoringParameters.Builder(scenario, person.getId()).build(); //build parameters from scenario (see if new parameter-class is necessary
				sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params));
				sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, scenario.getNetwork()));
				sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params));
				sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));
				
				//TODO: define own scoring functions (leg- and maybe money-scoring)
				sumScoringFunction.addScoringFunction(new WolfLegScoring(params,person, personAttributes, scenario.getNetwork(), config));
			
			return sumScoringFunction;
			}
		});
		
		controler.run();
	}
	

	// methods for routing-settings for all modes
	//public only for test
	private static void setNetworkModeRouting(Controler controler) {
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				addTravelTimeBinding(TransportMode.ride).to(networkTravelTime());
				addTravelDisutilityFactoryBinding(TransportMode.ride).to(carTravelDisutilityFactoryKey());
				addTravelTimeBinding(UtilityFunctionConstants.Modes.taxi.toString()).to(networkTravelTime());
				addTravelDisutilityFactoryBinding(UtilityFunctionConstants.Modes.taxi.toString()).to(carTravelDisutilityFactoryKey());
				addTravelTimeBinding(UtilityFunctionConstants.Modes.colectivo.toString()).to(networkTravelTime());
				addTravelDisutilityFactoryBinding(UtilityFunctionConstants.Modes.colectivo.toString()).to(carTravelDisutilityFactoryKey());
				addTravelTimeBinding(UtilityFunctionConstants.Modes.other.toString()).to(networkTravelTime());
				addTravelDisutilityFactoryBinding(UtilityFunctionConstants.Modes.other.toString()).to(carTravelDisutilityFactoryKey());
			}
		});
	}
	
	
	
	// mehtods for strategy setting
	//public only for test
	private static void setBasicStrategiesForSubpopulations(MatsimServices controler) {
		setReroute("carAvail", controler);
		setChangeExp("carAvail", controler);
		setReroute(null, controler);
		setChangeExp(null, controler);
	}

	private static void setChangeExp(String subpopName, MatsimServices controler) {
		StrategySettings changeExpSettings = new StrategySettings();
		changeExpSettings.setStrategyName(DefaultPlanStrategiesModule.DefaultSelector.ChangeExpBeta.toString());
		changeExpSettings.setSubpopulation(subpopName);
		changeExpSettings.setWeight(0.7);
		controler.getConfig().strategy().addStrategySettings(changeExpSettings);
	}
	
	private static void setReroute(String subpopName, MatsimServices controler) {
		StrategySettings reRouteSettings = new StrategySettings();
		reRouteSettings.setStrategyName(DefaultPlanStrategiesModule.DefaultStrategy.ReRoute.toString());
		reRouteSettings.setSubpopulation(subpopName);
		reRouteSettings.setWeight(0.15);
		controler.getConfig().strategy().addStrategySettings(reRouteSettings);
	}
}
