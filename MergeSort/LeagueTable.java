
import java.util.ArrayList;
import java.util.HashMap;

public class LeagueTable {
    private ArrayList<Team> teams;

    public LeagueTable(HashMap<String, Integer> league) {
        this.teams = new ArrayList<>(); // Creates a new arraylist for this league
        for (String name : league.keySet()) { // gets the keys of the hashmap
            Team team = new Team(); // creates a new team object to store the name(key) and the points (value) of
                                    // the team
            team.setName(name);
            team.setPoints(league.get(name));
            teams.add(team); // adds team to the arraylist
        }

    }

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

    public ArrayList<Team> copyLeague() {
        return copyPartOfLeague(teams, 0, teams.size() - 1);
    }

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

    // Takes as input a list and returns the sorted list
    private ArrayList<Team> mergeSort(ArrayList<Team> league) {
        for (Team team : league) {
            System.out.print(team.getName()); // prints out the recursion tree, shows the league recrusively getting
                                              // split.
        }
        System.out.println();
        if (league.size() <= 1) { // returns the league when there is only 1 item left in the league.stopping
                                  // condition
            return league;
        }
        int mid = league.size() / 2; // finds the mid point of the league in order to split the league in half
        // uses the copypart of league method to split in half
        ArrayList<Team> firstHalf = new ArrayList<>(copyPartOfLeague(league, 0, mid - 1));
        ArrayList<Team> secondHalf = new ArrayList<>(copyPartOfLeague(league, mid, league.size() - 1));
        // calls mergesort on each half of the league
        mergeSort(firstHalf);
        mergeSort(secondHalf);
        // calls the merge method to merge together the two sorted lists
        return merge(firstHalf, secondHalf, league);
    }

    // Part 1 of A2: Implement the Merge algorithm as part of Mergesort
    // Takes as input two sorted lists and returns the merged sorted list
    private ArrayList<Team> merge(ArrayList<Team> firstHalf, ArrayList<Team> secondHalf, ArrayList<Team> Sorted) {
        // sets the indexs, i and j represent index of fristhalf and secondhalf
        // respectively. k represents the index of the new ArrayList sorted where the
        // Lists will be combined
        int i = 0;
        int j = 0;
        int k = 0;
        // sets the size of the new array sorted to be the size of firsthalf and second
        // half combined so that all items can be stored in new array

        // compares both the sorted lists in order to make a fully complete sorted list
        // does this by comparing the elements at index i or j and then incrementing the
        // index by 1 but only for the one that was smaller and put into the new list
        // this means that if the first two i elements are smaller then the first second
        // half item they will be stored in sorted list first for example.
        while (i < firstHalf.size() && j < secondHalf.size()) {
            if (firstHalf.get(i).getPoints() >= secondHalf.get(j).getPoints()) {
                Sorted.set(k, firstHalf.get(i)); // sets the sorted list at index k to i
                i++;// i is incremented as an item from the first list has been added
                k++;// k is always incremented
            } else {
                Sorted.set(k, secondHalf.get(j));
                j++;
                k++;
            }
        }

        while (i < firstHalf.size()) {
            Sorted.set(k, firstHalf.get(i));
            i++;
            k++;
        }

        while (j < secondHalf.size()) {
            Sorted.set(k, secondHalf.get(j));
            j++;
            k++;
        }
        return Sorted;
    }

    private void print(ArrayList<Team> league) {
        printHelp(league, 0); // passes in the list that needs to be printed sets index to 0
    }

    private void printHelp(ArrayList<Team> league, int i) {
        if (i < league.size()) { // while the index is smaller then the size of the list
            String name = league.get(i).getName();
            int points = league.get(i).getPoints(); // gets details of the league
            System.out.println(name + "-" + points);
            printHelp(league, i + 1); // calls the printHelp method again and prints the next item
        }
    }

    public void analyseTeams(ArrayList<Team> league) {
        for (Team team : league) { // creates a subset for each team where the teams that sum up to make their
                                   // scores or less will be stored
            ArrayList<Team> Subset = new ArrayList<>();
            int Points = team.getPoints();
            subsetSum(league, team, 0, Points, Subset); // passes in the league, the team currently being anaylsed, 0 as
                                                        // the index, Points the amount of point sthat team has which is
                                                        // used as the target, the Susbet array
        }
    }

    private void subsetSum(ArrayList<Team> league, Team Team, int index, int Points, ArrayList<Team> Subset) {

        if (Points > 0 && Subset.size() >= 3) { // checks if the points are bigger then 0 as we want it to include
                                                // points sums that are smaller then the target as well, the subet also
                                                // nees to be at least 3 in length
            // Print the subset
            System.out.print(Team.getName() + "(" + Team.getPoints() + ")" + ">"); // prints the target team
            for (Team team : Subset) {
                System.out.print(team.getName() + "(" + team.getPoints() + ")   "); // prints the teams in the subset
            }
            System.out.println();
            System.out.println();
        }
        if (league.isEmpty()) {
            return;
        }

        for (int i = index; i < league.size(); i++) {
            Subset.add(league.get(i)); // considers the next team in the league as a possible combination for sum
            subsetSum(league, Team, i + 1, Points - league.get(i).getPoints(), Subset);
            Subset.remove(Subset.size() - 1); // allows backtracking to take place
        }
    }
}


