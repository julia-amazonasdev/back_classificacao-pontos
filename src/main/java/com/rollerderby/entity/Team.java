package com.rollerderby.entity;

import java.util.List;

public class Team {
	
	private String name; // nome do time 
	private int victories; //numero de vitorias
	private int defeats; //numero de derrotas 
	private int totalPoints; //numero de pontos marcados 
	private int totalPointsAgainst; //numero de pontos sofridos 
    private List<String> matchResults; // Histórico de jogos
    
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
		this.victories = victories;
	}
	public int getDefeats() {
		return defeats;
	}
	public void setDefeats(int defeats) {
		this.defeats = defeats;
	}
	public int getTotalPoints() {
		return totalPoints;
	}
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}
	public int getTotalPointsAgainst() {
		return totalPointsAgainst;
	}
	public void setTotalPointsAgainst(int totalPointsAgainst) {
		this.totalPointsAgainst = totalPointsAgainst;
	}
	public List<String> getMatchResults() {
		return matchResults;
	}
	public void setMatchResults(List<String> matchResults) {
		this.matchResults = matchResults;
	}

}
