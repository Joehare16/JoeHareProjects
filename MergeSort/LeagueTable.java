// This is the only Class you need to edit
// DO NOT EDIT SOME METHODS FROM THE CLASS - THESE ARE CLEARLY STATED
// DO NOT USE ADDITIONAL LIBRARIES TO IMPLEMENT YOUR SOLUTION

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeagueTable {
	private ArrayList<Team> teams;

	// Part 1 of A2: Pre-processing of input from HashMap to ArrayList
	public LeagueTable(HashMap<String, Integer> league) {
		for (Map.Entry<String, Integer> entry : league.entrySet()) {
			Team team = new Team();
			team.setName(entry.getKey());
			team.setPoints(entry.getValue());
			teams.add(team);
			System.out.println(teams);
		}
	}

	// DO NOT EDIT THIS METHOD
	// You can use this method to create copies of an ArrayList, e.g. for dividing the list into two for Mergesort
	private ArrayList<Team> copyPartOfLeague(ArrayList<Team> league, int startIndex, int endIndex) {
		ArrayList<Team> copy = new ArrayList<Team>();
		for (int i = startIndex; i <= endIndex; i++) {
			Team team = new Team();
			team.setName(league.get(i).getName());
			team.setPoints(league.get(i).getPoints());
			copy.add(team);
		}

		return copy;
	}

	// DO NOT EDIT THIS METHOD
	// You do not need to use this method
	public ArrayList<Team> copyLeague() {
		return copyPartOfLeague(teams, 0, teams.size() - 1);
	}

	// DO NOT EDIT THIS METHOD
	public void printTable() {
		// Print the initial (unsorted) list of teams first
		print(copyLeague());

		// Sort the list of teams - it should also print the recursion tree
		System.out.println("===========================");
		System.out.println("PRINTING THE RECURSION TREE");
		System.out.println("===========================");
		teams = mergeSort(copyLeague());

		// Print the sorted list of teams
		System.out.println("=========================");
		System.out.println("PRINTING THE LEAGUE TABLE");
		System.out.println("=========================");
		print(copyLeague());
	}

	// Part 1 of A2: Implement the Mergesort algorithm to sort the league
	// Part 2 of A2: It should also print the recursion tree, somewhere in mergeSort(...) or merge(...)
	// Takes as input a list and returns the sorted list
	private ArrayList<Team> mergeSort(ArrayList<Team> league) {
		int n = league.size();
		if (n == 1) return league;
		int mid = n / 2;
		ArrayList<Team> firstHalf = mergeSort(copyPartOfLeague(league, 0, mid));
		ArrayList<Team> secondHalf = mergeSort(copyPartOfLeague(league, mid, league.size()));

		return merge(firstHalf, secondHalf);
	}

	// Part 1 of A2: Implement the Merge algorithm as part of Mergesort
	// Takes as input two sorted lists and returns the merged sorted list
	private ArrayList<Team> merge(ArrayList<Team> firstHalf, ArrayList<Team> secondHalf) {
		ArrayList<Team> finalArr = new ArrayList<Team>(firstHalf.size() + secondHalf.size());

		int i = 0;
		int j = 0;
		int k = 0;

		while (i < firstHalf.size() || j < secondHalf.size()) {

			if (firstHalf.get(i).getPoints() < secondHalf.get(j).getPoints()) {
				finalArr.set(k, firstHalf.get(i));
				i++;
				k++;
			} else {
				finalArr.set(k, secondHalf.get(j));
				j++;
				k++;
			}
			if (i > firstHalf.size()) {
				while (j < secondHalf.size()) {
					finalArr.set(k, secondHalf.get(j));
					k++;
					j++;
				}
			}
			if (j > secondHalf.size()) {
				while (i < firstHalf.size()) {
					finalArr.set(k, firstHalf.get(j));
					k++;
					i++;
				}
			}
		}
		return finalArr;
		//return print(finalArr);
	}

		// Part 1 of A2: Post-processing of output to print the league table
		// ONLY RECURSIVE SOLUTIONS WILL BE ACCEPTED
		private void print(ArrayList<Team> league) {
			for(Team team:teams){
				System.out.println(team.getName() + " - " + team.getPoints());
			}
		}

		// Part 3 of A2: Find teams with more points than the sum of points of at least 3 teams
		// YOU SHOULD NOT SORT THE TEAMS FIRST
		// HINT: Try reducing this to a similar problem of Subset Sum
		// You need to write one additional recursive method
		// (because the parameter for total points is not known in advance, but it depends on the selected team)
		// ONLY RECURSIVE SOLUTIONS WILL BE ACCEPTED FOR THE ADDITIONAL METHOD
		//public void analyseTeams(ArrayList<Team> league) {

		//}

}

