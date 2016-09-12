import java.util.ArrayList;
import java.util.List;

public class StatsTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String configFile = "input/stats_config.xml";
		
		CarsharingBerlinStats stats = new CarsharingBerlinStats(configFile);
		List<String[]> statsList= new ArrayList<String[]>();
				
		statsList = stats.getStatsList();
		
		stats.writeStatsToFile("output", statsList);
	}
}

