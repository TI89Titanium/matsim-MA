package utilityfunction.config;

import org.matsim.core.config.experimental.ReflectiveConfigGroup;

public class UtilityFunctionParametersConfigGroup extends ReflectiveConfigGroup {
	public static final String GROUP_NAME = "UtilityFunctionParameters";
	
	private Double MPT_Parmeter = null;
	private Double monetaryDistanceRate_car_km = null;
	private Double monetaryDistanceRate_twowaycarsharing_km = null;
	
	

	
	public UtilityFunctionParametersConfigGroup() {
		super(GROUP_NAME);
	}
	
	@StringGetter( "MPT_Parmeter" )
	public Double getMPT_Parmeter() {
		return this.MPT_Parmeter;
	}

	@StringSetter( "MPT_Parmeter" )
	public void setMPT_Parmeter(final Double MPT_Parmeter) {
		this.MPT_Parmeter = MPT_Parmeter;
	}
	
	@StringGetter( "monetaryDistanceRate_car_km" )
	public Double getmonetaryDistanceRate_car_km() {
		return this.monetaryDistanceRate_car_km;
	}

	@StringSetter( "monetaryDistanceRate_car_km" )
	public void setmonetaryDistanceRate_car_km(final Double monetaryDistanceRate_car_km) {
		this.monetaryDistanceRate_car_km = monetaryDistanceRate_car_km;
	}
	
	@StringGetter( "monetaryDistanceRate_twowaycarsharing_km" )
	public Double getmonetaryDistanceRate_twowaycarsharing_km() {
		return this.monetaryDistanceRate_twowaycarsharing_km;
	}

	@StringSetter( "monetaryDistanceRate_twowaycarsharing_km" )
	public void setmonetaryDistanceRate_twowaycarsharing_km(final Double monetaryDistanceRate_twowaycarsharing_km) {
		this.monetaryDistanceRate_twowaycarsharing_km = monetaryDistanceRate_twowaycarsharing_km;
	}
	
}
