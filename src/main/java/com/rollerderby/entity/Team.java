package com.rollerderby.entity;

import java.util.ArrayList;
import java.util.List;

public class Team {
	
	private String name; // nome do time 
	private int victories; //numero de vitorias
	private int defeats; //numero de derrotas 
	private int totalPoints; //numero de pontos marcados 
	private int totalPointsAgainst; //numero de pontos sofridos 
    private List<String> matchResults; // Histórico de jogos
    
	public Team(String team1) {
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVictories() {
		return victories;
	}
	public void setVictories(int victories) {
		this.victories = 0;
	}
	public int getDefeats() {
		return defeats;
	}
	public void setDefeats(int defeats) {
		this.defeats = 0;
	}
	public int getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = 0;
	}
	public int getTotalPointsAgainst() {
		return totalPointsAgainst;
	}
	public void setTotalPointsAgainst(int totalPointsAgainst) {
		this.totalPointsAgainst = 0;
	}
	public List<String> getMatchResults() {
		return matchResults;
	}
	public void setMatchResults(List<String> matchResults) {
		this.matchResults = new ArrayList<>();
	}





	public void addVictory(int pointsFor, int pointsAgainst) {
		this.victories++;
		this.totalPoints += pointsFor;
		this.totalPointsAgainst += pointsAgainst;
		this.matchResults.add("V " + pointsFor + "-" + pointsAgainst);
		
	}
	
	public void addDefeat(int pointsFor, int pointsAgainst) {
        this.defeats++;
        this.totalPoints += pointsFor;
        this.totalPointsAgainst += pointsAgainst;
        this.matchResults.add("D " + pointsFor + "-" + pointsAgainst);
    }





	

}
