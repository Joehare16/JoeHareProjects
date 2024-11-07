import java.util.HashMap;

public class Run {

	public static void main(String[] args) {
		// This is the test input to the program 
		HashMap<String, Integer> leagueInfo = new HashMap<String, Integer>(); 
		leagueInfo.put("Scotland", 15);
		leagueInfo.put("Bulgaria", 2);
		leagueInfo.put("England", 16);
		leagueInfo.put("Andorra", 2);
		leagueInfo.put("Wales", 10);
		leagueInfo.put("Malta", 0);
		leagueInfo.put("N.Ireland", 6);
		leagueInfo.put("Finland", 12);
		// You can edit the above code to test your program with various input
		// Below code is how we will run your program and assess its correctness
		LeagueTable league = new LeagueTable(leagueInfo);
		// Creates the League from the input data

		// Prints the Teams with more points than the total points of at least 3 teams
		System.out.println("===================");
		System.out.println("ANALYSING THE TEAMS");
		System.out.println("===================");		
		//league.analyseTeams(league.copyLeague());
				
		// Prints the League Table
		System.out.println("===================");
		System.out.println("PRINTING THE LEAGUE");
		System.out.println("===================");
		league.printTable();
	}

}