package utilityfunction.scoring;

import org.matsim.api.core.v01.events.Event;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.population.Leg;
import org.matsim.api.core.v01.population.Person;
import org.matsim.core.config.Config;
import org.matsim.core.scoring.functions.CharyparNagelScoringParameters;
import org.matsim.utils.objectattributes.ObjectAttributes;

/**
 * This is a implementation of the utlity function with additional parameters based on the CharyparNagel function
 * from the MATSim Score
 * 
 * @author Maximilian Wolf (GitHub: <a href="https://github.com/TI89Titanium">TI89Titanium</a>)
 */

public class MPTLegScoring implements org.matsim.core.scoring.SumScoringFunction.LegScoring, org.matsim.core.scoring.SumScoringFunction.ArbitraryEventScoring {
	//declare parameters
	protected double score;
	/** The parameters used for scoring */
	protected final CharyparNagelScoringParameters params;
	/** Additional Parameters */
	protected final double mptParam;
	protected final Person person;
	protected final ObjectAttributes personAttributes;
	protected Network network; //nerver used?
	protected Config config;
	
	//TODO add parameter (instance of parameter-class "WolfScoringParameters"
	//public final double monetaryDistanceRate; etc.
	public MPTLegScoring (final CharyparNagelScoringParameters params,Person person,ObjectAttributes personAttributes, Network network, Config config){
		this.params = params;
		this.person = person;
		this.personAttributes = personAttributes;
		this.network = network;
		this.config = config;
		this.mptParam = Double.parseDouble(this.config.getModule("UtilityFunctionParameters").getParams().get("MPT_Parmeter"));
		//needs to get person information too?!
	}
		
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
	protected double calcMPTScore(Leg leg) {
		
		String mpt = (String) personAttributes.getAttribute(person.getId().toString(), "MPT");
		
		String moduleName = "MPT_" + mpt;
		double tmpScore = 0.0;
		
		// switch on mode type
		if (leg.getMode().equals("walk") || leg.getMode().equals("transit_walk") || leg.getMode().equals("walk_rb") || leg.getMode().equals("bike")) {
			tmpScore += Double.parseDouble(this.config.getModule(moduleName).getParams().get("MPTValue_Walk")) * this.mptParam;
		}
		else if (leg.getMode().equals("colectivo") || leg.getMode().equals("pt")){
			tmpScore += Double.parseDouble(this.config.getModule(moduleName).getParams().get("MPTValue_PT")) * this.mptParam;
		}
		else if (leg.getMode().equals("car")){
			tmpScore += Double.parseDouble(this.config.getModule(moduleName).getParams().get("MPTValue_Car")) * this.mptParam;
		}
		// different types of carsharing from carsharing-module
		else if (leg.getMode().equals("ride") || leg.getMode().equals("taxi") ||leg.getMode().equals("freefloating") || leg.getMode().equals("onewaycarsharing") || leg.getMode().equals("twowaycarsharing")){
			tmpScore += Double.parseDouble(this.config.getModule(moduleName).getParams().get("MPTValue_CS_Ride")) * this.mptParam;
		}
		else if (leg.getMode().equals("train")){
		tmpScore += Double.parseDouble(this.config.getModule(moduleName).getParams().get("MPTValue_Train")) * this.mptParam;
		}
		else if (leg.getMode().equals("other")){
			//no adding to score
		}
		return tmpScore;
	}

	@Override
	public double getScore() {
		return this.score;
	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLeg(Leg leg) {
		// TODO Auto-generated method stub
		double legScore = calcMPTScore(leg);
		this.score += legScore;
		
	}



}