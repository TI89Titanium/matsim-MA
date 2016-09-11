package utilityfunction.test;
import utilityfunction.controler.*;

public class SimulationTest {

	public static String inputPath = "../MATSimUtilityFunction/input/";
	public static final String csvFileMatching = "C:/Users/Maximilian/Dropbox/01_KIT/Abschlussarbeit/UtilityMobility/Files/MIWDataRaw.csv";
	public static final String configFileMatching = "../MATSimUtilityFunction/input/config_popmatching.xml";
	public static final String configFile_3agents = inputPath + "config_3agents.xml";
	
	public static void main(String[] args) {
	
	String doMatching = "true";
	
	//String[] argsSimulationMatching = {configFileMatching, doMatching};
	String[] argsSimulation = {configFile_3agents};
		
	UtilityFunctionControler.main(argsSimulation);
	}

}
