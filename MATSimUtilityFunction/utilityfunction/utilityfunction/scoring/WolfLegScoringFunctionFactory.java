package utilityfunction.scoring;

import javax.inject.Inject;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.scoring.ScoringFunction;
import org.matsim.core.scoring.ScoringFunctionFactory;
import org.matsim.core.scoring.SumScoringFunction;
import org.matsim.core.scoring.functions.CharyparNagelActivityScoring;
import org.matsim.core.scoring.functions.CharyparNagelAgentStuckScoring;
import org.matsim.core.scoring.functions.CharyparNagelLegScoring;
import org.matsim.core.scoring.functions.CharyparNagelMoneyScoring;
import org.matsim.core.scoring.functions.CharyparNagelScoringParametersForPerson;
import org.matsim.core.scoring.functions.SubpopulationCharyparNagelScoringParameters;

public class WolfLegScoringFunctionFactory implements ScoringFunctionFactory{
	
	private final Scenario scenario;
	private final CharyparNagelScoringParametersForPerson params;
	
	@Inject
	public WolfLegScoringFunctionFactory (final Scenario sc){
		this.scenario = sc;
		this.params = new SubpopulationCharyparNagelScoringParameters (sc);
	}
	
	@Override
	public ScoringFunction createNewScoringFunction(Person person) {
		
		SumScoringFunction sumScoringFunction = new SumScoringFunction();

		// Score activities, legs, payments and being stuck
		// with the default MATSim scoring (formulated by Charypar and Nagel) based on utility parameters in the config file.
		sumScoringFunction.addScoringFunction(new CharyparNagelActivityScoring(params.getScoringParameters(
				person)));
		sumScoringFunction.addScoringFunction(new CharyparNagelLegScoring(params.getScoringParameters(
				person), scenario.getNetwork()));
		sumScoringFunction.addScoringFunction(new CharyparNagelMoneyScoring(params.getScoringParameters(
				person)));
		sumScoringFunction.addScoringFunction(new CharyparNagelAgentStuckScoring(params.getScoringParameters(
				person)));
		
		//TODO: define own scoring functions (leg- and maybe money-scoring)
		sumScoringFunction.addScoringFunction(new WolfLegScoring(params.getScoringParameters(
				person),person, scenario.getPopulation().getPersonAttributes(), scenario.getNetwork(), scenario.getConfig() ));
	
	return sumScoringFunction;
	}
}
