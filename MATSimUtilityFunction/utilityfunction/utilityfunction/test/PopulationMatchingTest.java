package utilityfunction.test;

import java.util.List;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;

import utilityfunction.population.MATSimPopulation;
import utilityfunction.population.PopulationMatching;

public class PopulationMatchingTest {

	public static void main(String[] args) {
		String csvFileMatching = "C:/Users/Maximilian/Dropbox/01_KIT/Abschlussarbeit/UtilityMobility/Files/MIWDataRaw.csv";
		String csvFileMatchingNew = "C:/Users/Maximilian/Dropbox/01_KIT/Abschlussarbeit/UtilityMobility/Files/MIWDataRaw_MTP_new.csv";
		String configFileMatching = "../MATSimUtilityFunction/input/config_popmatching.xml";
		String configFileMatchingLegScoring = "../MATSimUtilityFunction/input/config_popmatching_legscoring.xml";
		//PopulationMatching populationMatching = new PopulationMatching.matchPopulation(configFileMatching, csvFileMatching);
		
		Config config = ConfigUtils.loadConfig(configFileMatchingLegScoring);
		
		MATSimPopulation matSimPopulation = new MATSimPopulation(config);
		
		List <String[]> matSimPopulationMatchingList = matSimPopulation.getMATSimPopulationMatchingList();
		
		
		printMATSimPopInfo(matSimPopulationMatchingList);
		//create SurveyList and print info
		PopulationMatching populationMatching = new PopulationMatching();
		Scenario scenario = populationMatching.matchPopulation(config, csvFileMatchingNew);
		
		String selectedModes[] = {"D", "E"};
		//Scenario scenario = populationMatching.matchPopulationSpecificModes(config, csvFileMatchingNew, selectedModes);
		
		//Print Number of Agents
		//Print Distribution of DAILY_INCOME
		//System.out.println(scenario.getPopulation().getPersonAttributes().toString());
		printOutIncomeStats(populationMatching.getMATSimPopulation());
		printOutMTPStats(populationMatching.getMATSimPopulation());
		
	}

	//Test: print out matSimList
		public static void testPrintMATSimPopList (List <String[]> matSimPopulationList){
			for(String[] ausgabe : matSimPopulationList){
				System.out.println(ausgabe[0] + " " + ausgabe[1] + " " + ausgabe[2]);
			}
			System.out.println(matSimPopulationList.size());
		}
		
		//Test: print out array
		public static void testPrintSurveyPop (List <String[]> surveyPopulationList){
			int length = surveyPopulationList.size();		
//			System.out.println(columnName[column]);
			//print out Array:
			for (int i=0; i<length; i++){
				System.out.print(i+1 + " ");
				for (int j=0; j<surveyPopulationList.get(i).length; j++){
				
				System.out.print(surveyPopulationList.get(i)[j] + " ");
				}
				System.out.println("");
			}
		}
		
		//Test: print out matSimList
		public static void printMATSimPopInfo (List <String[]> matSimPopulationList){
			//Occ of modes walk (walk, transit_walk); bike; pt (colectivo, pt); car; ride(ride, taxi); train; combination; other

			Integer[] modeOcc = {0,0,0,0,0,0,0,0};
			int sum = 0;
			String [] modes = {"walk", "bike", "pt", "car", "ride", "train", "combination", "other"};
			
			for(String[] print : matSimPopulationList){
				if (print[2].equals("walk") || print[2].equals("transit_walk")){
					modeOcc[0]++;
					sum++;
				}
				else if (print[2].equals("bike")){
					modeOcc[1]++;
					sum++;
				}
				else if (print[2].equals("colectivo")||print[2].equals("pt")){
					modeOcc[2]++;
					sum++;
				}
				else if (print[2].equals("car")){
					modeOcc[3]++;
					sum++;
				}
				else if (print[2].equals("ride")||print[2].equals("taxi")){
					modeOcc[4]++;
					sum++;
				}
				else if (print[2].equals("train")){
					modeOcc[5]++;
					sum++;
				}
				else if (print[2].equals("combination")){
					modeOcc[6]++;
					sum++;
				}
				else if (print[2].equals("other")){
					modeOcc[7]++;
					sum++;
				}
					
			}
			System.out.println("Number of Agents: " + matSimPopulationList.size());
			System.out.println("Number of Agents with recorded Mode: " + sum);
			for (int i = 0; i<modeOcc.length; i++){
				System.out.println("Mode " + modes[i] + ": " + modeOcc[i]);
			}
		}
		public static void printOutIncomeStats(MATSimPopulation matSimPopulation){
			String attribute = "DAILY_INCOME";
			Integer[] countOfIncomeGroup = {0,0,0,0,0,0};
			String [] dailyIncomeToIncomeGroupTranslater = {"A", "B", "C", "D", "E", "F"};
			countOfIncomeGroup[0] = matSimPopulation.getAttributeStats(attribute, "25.0");
			countOfIncomeGroup[1] = matSimPopulation.getAttributeStats(attribute, "75.0");
			countOfIncomeGroup[2] = matSimPopulation.getAttributeStats(attribute, "125.0");
			countOfIncomeGroup[3] = matSimPopulation.getAttributeStats(attribute, "175.0");
			countOfIncomeGroup[4] = matSimPopulation.getAttributeStats(attribute, "225.0");
			countOfIncomeGroup[5] = matSimPopulation.getAttributeStats(attribute, "275.0");
			System.out.println("Distribution of Income-Groups of MATSim Population:");
			int i = 0;
			for (int count : countOfIncomeGroup){
				System.out.println(dailyIncomeToIncomeGroupTranslater[i] + ": " + count);
				i++;
			}
		}
		
		//{"Pragmatic Long-Distance Driver", "Auto Addicted", "Active Public Transportation Lover", "Lazy Public Transportation Lover", "Active Auto Lover"}
		public static void printOutMTPStats(MATSimPopulation matSimPopulation){
			String attribute = "MPT";
			Integer[] countOfModalPreferenceType = {0,0,0,0,0};
			String [] mptNames = {"Pragmatic Long-Distance Driver", "Auto Addicted", "Active Public Transportation Lover", "Lazy Public Transportation Lover", "Active Auto Lover"};
			countOfModalPreferenceType[0] = matSimPopulation.getAttributeStats(attribute, "PragmaticLongDistanceDriver");
			countOfModalPreferenceType[1] = matSimPopulation.getAttributeStats(attribute, "AutoAddicted");
			countOfModalPreferenceType[2] = matSimPopulation.getAttributeStats(attribute, "ActivePublicTransportationLover");
			countOfModalPreferenceType[3] = matSimPopulation.getAttributeStats(attribute, "LazyPublicTransportationLover");
			countOfModalPreferenceType[4] = matSimPopulation.getAttributeStats(attribute, "ActiveAutoLover");
			
			System.out.println("Distribution of ModalPreferenceType (MPT) of MATSim Population:");
			int i = 0;
			for (int count : countOfModalPreferenceType){
				System.out.println(mptNames[i] + ": " + count);
				i++;
			}
		}
}

