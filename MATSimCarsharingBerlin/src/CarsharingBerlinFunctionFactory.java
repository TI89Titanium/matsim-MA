import javax.inject.Inject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.contrib.carsharing.scoring.CarsharingLegScoringFunction;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.CharyparNagelScoringParameters;


import utilityfunction.scoring.WolfLegScoring;

public class CarsharingBerlinFunctionFactory implements ScoringFunctionFactory{
	
	private final Scenario scenario;
	private final CharyparNagelScoringParameters params;
	
	@Inject
	public CarsharingBerlinFunctionFactory (final Scenario sc){
		this.scenario = sc;
		this.params = CharyparNagelScoringParameters.getBuilder(sc.getConfig().planCalcScore()).create();
	}
	
	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		
		SumScoringFunction sumScoringFunction = new SumScoringFunction();

		// Score activities, legs, payments and being stuck
		// with the default MATSim scoring (formulated by Charypar and Nagel) based on utility parameters in the config file.
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params));
		sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params, scenario.getNetwork()));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params));
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params));
		
		//add carsharing scoring
		sumScoringFunction.addScoringFunction(new CarsharingLegScoringFunction(params, scenario.getConfig(), scenario.getNetwork()));
		
		//TODO: define own scoring functions (leg- and maybe money-scoring)
		sumScoringFunction.addScoringFunction(new WolfLegScoring(params,person, scenario.getPopulation().getPersonAttributes(), scenario.getNetwork(), scenario.getConfig() ));
	
	return sumScoringFunction;
	}
}
