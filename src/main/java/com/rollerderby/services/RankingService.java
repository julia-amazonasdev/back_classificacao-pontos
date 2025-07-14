package com.rollerderby.services;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.rollerderby.entity.*;


public class RankingService {
	
	private final List<Match> allMatches; // define uma lista de partidas chamadas allMatches

	private final Map<String, Team> teams = new HashMap<>(); // faz um mapa com dados dos times e strings
	
	//construtor
	public RankingService(List<Match> allMatches) {
		this.allMatches = allMatches; 
	}
	
	
	//processar as partidas
	public void processMatches(boolean onlyGroupPhases) {
		teams.clear(); //limpar os dados do map
		
		for (Match match : allMatches) {
			
			if (match.getTeam1() == null || match.getTeam2() == null) continue; //excluir resultados nulos
			if(onlyGroupPhases && match.getTeam1().contains("(")) continue; //não processas resultados com "(" exemplo (QF), (SF)
			
			String team1 = match.getTeam1(); // vai definir os dados de acordo com a lista allMatch
			String team2 = match.getTeam2();
			int score1 = match.getScore1();
			int score2 = match.getScore2();
			
			teams.putIfAbsent(team1, new Team(team1)); //inclui no mapa a info da variável definida acima
			teams.putIfAbsent(team2, new Team(team2));
			
			Team t1 = teams.get(team1); // do mapa o valor vem para t1 e t2
			Team t2 = teams.get(team2);
			
			//add as vitória e derrotas
			if (score1 > score2) {
				t1.addVictory(score1, score2);
				t2.addDefeat(score2, score1);
			} else {
				t1.addDefeat(score1, score2);
				t2.addVictory(score2, score1);
			}
			
			
		}
		
		
	}
	
	//lista de ranking ordenada
	public List<Team> getSortedRanking() {
		return teams.values().stream()
				.sorted(Comparator
						.comparingInt(Team::getVictories).reversed()
						.thenComparingInt(Team::getPointsDifference).reversed()
						.thenComparingInt(Team::getTotalPoints).reversed())
				.collect(Collectors.toList());
						
	}
	
	//lista ranking top8
	
	public List<Team> getTop8FromGroupPhase() {
		processMatches(true);
		return getSortedRanking().stream().limit(8).collect(Collectors.toList());
	}
	
	//lista ranking final
	public List<Team> getFinalRanking() {
		processMatches(false);
		return getSortedRanking(); 
	}
	
	
	
	public void analyzeTeamsByPerformance() {
		System.out.println("\n=== ANÁLISE POR DESEMPENHO ===");
		
		List<Team> undefeated = teams.values().stream() //lista de times sem derrota
				.filter(t -> t.getVictories() >= 2 && t.getDefeats() == 0) // filtrar times que só tem vitoria
				.sorted(Comparator.comparingInt(Team::getPointsDifference).reversed()) // vai comparar a diferença de pontos e ordenar a partir disso
				.collect(Collectors.toList());
		
		System.out.println("\n🏆 TIMES INVICTOS (2 vitórias):");
		
		for(Team team : undefeated) {
			System.out.printf("   %s - Saldo: %+d pontos\n", team.getName(), team.getPointsDifference());
		}
		
		System.out.println("\n🏆 TIMES COM 1 VITÓRIA:");
		
		List <Team> oneWin = teams.values().stream()
				.filter(t -> t.getVictories() == 1 && t.getDefeats() == 1)
				.sorted(Comparator.comparingInt(Team::getPointsDifference).reversed())
				.collect(Collectors.toList());
		
		
		for(Team team : oneWin) {
			System.out.printf("   %s - Saldo: %+d pontos\n", team.getName(), team.getPointsDifference());
		}
		
		System.out.println("\n🏆 TIMES DERROTADOS:");
		
		List<Team> noWins = teams.values().stream()
				.filter(t -> t.getVictories() == 0 && t.getDefeats() == 2)
				.sorted(Comparator.comparingInt(Team::getPointsDifference).reversed())
				.collect(Collectors.toList());
		
		for(Team team : noWins) {
			System.out.printf("   %s - Saldo: %+d pontos\n", team.getName(), team.getPointsDifference());
		}
		
	}

}
