import java.util.Set;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.contrib.carsharing.config.CarsharingConfigGroup;
import org.matsim.contrib.carsharing.runExample.CarsharingUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;

import utilityfunction.config.UtilityFunctionUtils;
import utilityfunction.population.PopulationMatching;
import utilityfunction.scoring.WolfLegScoringFunctionFactory;

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
		Scenario scenario = populationMatching.matchPopulationSpecificModes(config, csvFile, selectedModes);
		
		
		//controler for MATSim
		Controler controler = new Controler(scenario);
		
		//set up new scoring function WolfLegScoring with function factory;
		controler.setScoringFunctionFactory(new WolfLegScoringFunctionFactory(scenario));
		
	//	installCarSharing(controler);
		
		//run MATSim
		controler.run();
		
		//TODO: run stats module
	}
	
	/*
public static void installCarSharing(final Controler controler) {		
		
		final Scenario scenario = controler.getScenario();
		CarsharingXmlReaderNew reader = new CarsharingXmlReaderNew(scenario.getNetwork());
		
		final CarsharingConfigGroup configGroup = (CarsharingConfigGroup)
				scenario.getConfig().getModule( CarsharingConfigGroup.GROUP_NAME );

		reader.readFile(configGroup.getvehiclelocations());
		
		Set<String> carsharingCompanies = reader.getCompanies().keySet();
		
		MembershipReader membershipReader = new MembershipReader();
		
		membershipReader.readFile(configGroup.getmembership());

		final MembershipContainer memberships = membershipReader.getMembershipContainer();
		
		final CostsCalculatorContainer costsCalculatorContainer = CarsharingUtils.createCompanyCostsStructure(carsharingCompanies);
		
		final CarsharingListener carsharingListener = new CarsharingListener();
		final CarsharingSupplyInterface carsharingSupplyContainer = new CarsharingSupplyContainer(controler.getScenario());
		carsharingSupplyContainer.populateSupply();
		final KeepingTheCarModel keepingCarModel = new KeepingTheCarModelExample();
		final ChooseTheCompany chooseCompany = new ChooseTheCompanyExample();
		final ChooseVehicleType chooseCehicleType = new ChooseVehicleTypeExample();
		final RouterProvider routerProvider = new RouterProviderImpl();
		final CurrentTotalDemand currentTotalDemand = new CurrentTotalDemand(controler.getScenario().getNetwork());
		final CarsharingManagerInterface carsharingManager = new CarsharingManagerNew();
		final RouteCarsharingTrip routeCarsharingTrip = new RouteCarsharingTripImpl();
		
		//===adding carsharing objects on supply and demand infrastructure ===
		
		controler.addOverridingModule(new AbstractModule() {

			@Override
			public void install() {
				bind(KeepingTheCarModel.class)
				.toInstance(keepingCarModel);
				bind(ChooseTheCompany.class).toInstance(chooseCompany);
				bind(ChooseVehicleType.class).toInstance(chooseCehicleType);
				bind(RouterProvider.class).toInstance(routerProvider);
				bind(CurrentTotalDemand.class).toInstance(currentTotalDemand);
				bind(RouteCarsharingTrip.class).toInstance(routeCarsharingTrip);
				bind(CostsCalculatorContainer.class).toInstance(costsCalculatorContainer);
				bind(MembershipContainer.class).toInstance(memberships);
			    bind(CarsharingSupplyInterface.class).toInstance(carsharingSupplyContainer);
			    bind(CarsharingManagerInterface.class).toInstance(carsharingManager);
			    bind(DemandHandler.class).asEagerSingleton();
			}			
		});		
		
		//=== carsharing specific replanning strategies ===
		
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				this.addPlanStrategyBinding("RandomTripToCarsharingStrategy").to( RandomTripToCarsharingStrategy.class ) ;
				this.addPlanStrategyBinding("CarsharingSubtourModeChoiceStrategy").to( CarsharingSubtourModeChoiceStrategy.class ) ;
			}
		});
		
		//=== adding qsimfactory, controller listeners and event handlers
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bindMobsim().toProvider(CarsharingQsimFactoryNew.class);
		        addControlerListenerBinding().toInstance(carsharingListener);
		        addControlerListenerBinding().to(CarsharingManagerNew.class);		        
				bindScoringFunctionFactory().to(CarsharingScoringFunctionFactory.class);		      
		        addEventHandlerBinding().to(PersonArrivalDepartureHandler.class);
		        addEventHandlerBinding().to(DemandHandler.class);
			}
		});
		//=== adding carsharing specific scoring factory ===
		controler.addOverridingModule(new AbstractModule() {
			
			@Override
			public void install() {
				        
				bindScoringFunctionFactory().to(CarsharingScoringFunctionFactory.class);	
			}
		});

		//=== routing moduels for carsharing trips ===

		controler.addOverridingModule(CarsharingUtils.createRoutingModule());			
	}
*/
}
