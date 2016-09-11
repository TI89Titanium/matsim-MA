package utilityfunction.constants;


public class UtilityFunctionConstants {

	// additional modes from santiago scenario
		public enum Modes{
								bus,
								metro,
								train,
								colectivo,
								taxi,
								other,
//								school_bus,
//								motorcycle,
								truck
							};
						
		//Santiago greater area population according to "Informe de Difusión", page 9, tabla 1			
		public final static int N = 6651700;
		
		public final static String toCRS = "EPSG:32719";
		
		public final class SubpopulationName {
			public final static String carUsers = "carUsers";
		}
		
		public final class SubpopulationValues {
			public final static String carAvail = "carAvail";
		}
	
}
