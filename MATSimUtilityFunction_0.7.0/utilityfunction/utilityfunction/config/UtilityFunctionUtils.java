package utilityfunction.config;

import org.matsim.core.config.Config;

public class UtilityFunctionUtils {
public static Config addConfigModules(Config config) {
		
		MPT_ActiveAutoLoverConfigGroup configGroupAAL = new MPT_ActiveAutoLoverConfigGroup();
    	config.addModule(configGroupAAL);
    	
    	MPT_ActivePublicTransportationLoverConfigGroup configGroupAPTL = new MPT_ActivePublicTransportationLoverConfigGroup();
    	config.addModule(configGroupAPTL);
    	
    	MPT_AutoAddictedConfigGroup configGroupAA = new MPT_AutoAddictedConfigGroup();
    	config.addModule(configGroupAA);
    	
    	MPT_LazyPublicTransportationLoverConfigGroup configGroupLPTL = new MPT_LazyPublicTransportationLoverConfigGroup();
    	config.addModule(configGroupLPTL);
    	
    	MPT_PragmaticLongDistanceDriverConfigGroup configGroupPLDD = new MPT_PragmaticLongDistanceDriverConfigGroup();
    	config.addModule(configGroupPLDD);
    	
    	UtilityFunctionParametersConfigGroup configGroupUFP = new UtilityFunctionParametersConfigGroup();
    	config.addModule(configGroupUFP);
    	
    	return config;
		
	}
}
