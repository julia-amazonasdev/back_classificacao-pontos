package com.rollerderby.services;

import com.rollerderby.entity.*;
import java.util.*;

public class MatchService {
	
	private Map<String, Team> teams = new HashMap<>();
	private List<Match> matches = new ArrayList<>();
	
	public void addMatch(String team1, String team2, int score1, int score2, String date) {
		Match match = new Match(team1, team2, score1, score2, date);
		matches.add(match);
		
		teams.putIfAbsent(team1, new Team(team1));
		teams.putIfAbsent(team2, new Team(team2));
		
		Team t1 = teams.get(team1);
		Team t2 = teams.get(team2);
		
		if(score1 > score2) {
			t1.addVictory(score1, score2);
			t2.addDefeat(score2, score1);
		} else {
			t1.addDefeat(score1, score2);
			t2.addDefeat(score2, score1);
		}
	}
	
}
