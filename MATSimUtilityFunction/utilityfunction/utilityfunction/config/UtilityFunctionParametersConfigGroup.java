package utilityfunction.config;

import org.matsim.core.config.ReflectiveConfigGroup;

public class UtilityFunctionParametersConfigGroup extends ReflectiveConfigGroup {
	public static final String GROUP_NAME = "UtilityFunctionParameters";
	
	private Double MPT_Parmeter = null;
	
	

	
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
	
}
