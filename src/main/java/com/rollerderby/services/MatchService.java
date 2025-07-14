package com.rollerderby.services;

import com.rollerderby.entity.*;
import java.util.*;
import java.util.stream.Collectors;

public class MatchService {
	
	private Map<String, Team> teams = new HashMap<>(); // separação dos times e suas estatisticas
	private List<Match> matches = new ArrayList<>(); // separação dos jogos 
	

	
	// metodo para add os dados que foram pegos no map e na list. esse metodo define que esses dados sao 3 strings  e 2 ints. 
	public void addMatch(String team1, String team2, int score1, int score2, String date) {
		Match match = new Match(team1, team2, score1, score2, date); //instancia para definir o que é uma partida 
		matches.add(match); //add o match(instancia) no array match

		
	}
	
	
	 // método para gerar a classificação
	public void generateClassification() {
		 System.out.println("\n=== CLASSIFICAÇÃO MUNDIAL DE ROLLER DERBY 2025 ===");
	     System.out.println("Dados atualizados até: " + new Date());
	     
	     RankingService ranking = new RankingService(matches); // instancia a classe RankingService e passa os dados do array matches para esse classe, informando que esse ranking será formado através desse array. 

	     
	    System.out.println("\n--- CLASSIFICAÇÃO FASE DE GRUPOS ---"); 
	    List <Team> groupRanking = ranking.getTop8FromGroupPhase(); // cria uma lista de times chamada de groupRanking que será formada pelo return do método getTop8FromGroupPhase 

	    printTeam(groupRanking);  //imprime o groupRanking
	    
	    
	    
	    System.out.println("\n--- CLASSIFICAÇÃO GERAL ---");
	    List <Team> groupFinal = ranking.getFinalRanking();
	    printTeam(groupFinal);
	    
        
	    
	    
	    ranking.analyzeTeamsByPerformance(); // chama o método analyzeTeamsByPerformance do RankingService
	}
	
	//criação do método printTeam que terá como retorno uma lista de times 
	private void printTeam(List<Team> teams) {
		System.out.println("Pos | Time                 | J | V | D | PF  | PC  | Saldo | %");
	    System.out.println("----+---------------------+---+---+---+-----+-----+-------+------");
	    
	  //para cada linha o tamanho da lista teams vai aumentar um team, esse team será pego através da entity Team que foi mapeada no inicio da classe
	 // as linhas vao chamar esses métodos do Team
	    for(int i = 0; i < teams.size(); i++) {
	    	Team team = teams.get(i);
		   	System.out.printf("%-20s | J: %d | V: %d | D: %d | PF: %d | PC: %d | Saldo: %+d | %4.1f%%\n",
                i + 1, 
		   		team.getName(),
                team.getGamesPlayed(),
                team.getVictories(),
                team.getDefeats(),
                team.getTotalPoints(),
                team.getTotalPointsAgainst(),
                team.getPointsDifference(),
                team.getWinPercentage());
		
	    }
	}
	

	
		
	//apresenta o histórico de jogos		
		public void showMatchHistory() {
			System.out.println("\n=== HISTÓRICO DE JOGOS ===");
			
			
			Map<String, List<Match>> matchesByDate = matches.stream() //pega o array dos matches e faz um mapa com data
					.filter(m -> m.getDate() != null)
					.collect(Collectors.groupingBy(Match::getDate));
			
			for(String date : matchesByDate.keySet().stream().sorted().collect(Collectors.toList())) {  // para cada data imprime uma linha com as variáveis do match, será ordenado

				System.out.println("\n📅 " + date + ":");
				for(Match match : matchesByDate.get(date)) {
					System.out.printf("   %s %d x %d %s\n",
	                        match.getTeam1(),
	                        match.getScore1(),
	                        match.getScore2(),
	                        match.getTeam2());
				}
				
			}

		}
}
