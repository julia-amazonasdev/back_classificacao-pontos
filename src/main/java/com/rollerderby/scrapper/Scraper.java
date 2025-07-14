package com.rollerderby.scrapper;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.rollerderby.services.*;


public class Scraper {
	
	
	
	public void scrapeDataFromWebSite(String url) {
		try {
			System.out.println("Conectando ao site:" + url);
			Document doc = Jsoup.connect(url)
					.timeout(15000)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
					.get();
			
			System.out.println("Página carregada, carregando resultados...");
			
			Elements gameElements = tryMultipleSelectors(doc);
			
			 
			
			if (gameElements.isEmpty()) {
				
				System.out.println("Nenhum resultado encontrado com seletores automáticos.");
				System.out.println("MODO DEBUG: Vou mostrar a estrutura da página para você ajustar:");
				debugPageStructure(doc);
				addExampleData();
				return;
			}
			
			System.out.println("Encontrados" + gameElements.size() + "elementos potenciais.");
			
			int processedGames = 0;
			
			for (Element game : gameElements) {
				if (parseGameResult(game)) {
					processedGames++;
				}
			}
			
			System.out.println("Processados " + processedGames + " jogos");
			
			if (processedGames == 0) {
				System.out.println("Nenhum jogo foi processado. Usando dados de exemplo:");
				addExampleData();
			}
			
		} catch (IOException e) {
			System.err.println("Erro ao conectar com o site: " + e.getMessage());
			addExampleData();
		}
		    
		}
	
	
	private boolean parseGameResult(Element gameElement) {
			try {
			String gameText = gameElement.text().trim();
			
			//texto completo com "vs" ou "x" 
				if (parseTextFormat(gameText)) {
				return true;
				}
			
			//elementos filhos separados
				if (parseChildElements(gameElement)) {
					return true;
				}
				
			//tabela
				if (parseTableFormat(gameElement)) {
				return true;
				}
			
			//atributos de dados
				if (parseDataAttributes(gameElement)) {
				return true;
				}
			
				
			
		
			return false;
		
			} catch (Exception e) {
				System.err.println("Erro ao processar elemento: " + e.getMessage());
				return false;
			}
	}		
		
		
	


	private boolean parseDataAttributes(Element gameElement) {
		String team1 = gameElement.attr("data-team1");
		String team2 = gameElement.attr("data-team2");
		String score1Str = gameElement.attr("data-score1");
		String score2Str = gameElement.attr("data-score2");
		
		if(!team1.isEmpty() && !team2.isEmpty() && !score1Str.isEmpty() && !score2Str.isEmpty()) {
			try {
				int score1 = Integer.parseInt(score1Str);
				int score2 = Integer.parseInt(score2Str);
				matchservice.addMatch(team1, team2, score1, score2, getCurrentDate());
				return true;
			} catch (NumberFormatException e) {
                return false;
			
		 }
		}
		
		
		return false;
	}


	private boolean parseTableFormat(Element gameElement) {
		if(gameElement.tagName().equals("tr")) {
			Elements cells = gameElement.select("td");
			if(cells.size() >= 3) {
				try {
					String teams1 = cells.get(0).text().trim();
					String scoreText = cells.get(1).text().trim();
					String teams2 = cells.get(2).text().trim();
					
					// Extrair placares do formato "000-000" ou "000 x 000"
					
					String[] scores = scoreText.split("[-×x]");
					if (scores.length ==2) {
						int score1 = Integer.parseInt(scores[0].trim());
						int score2 = Integer.parseInt(scores[1].trim());
						matchservice.addMatch(teams1, teams2, score1, score2, getCurrentDate());
						return true;
					}
				} catch (NumberFormatException e) {
                    return false;				
				   		}
			}
		}
		return false;
	}		


	private boolean parseChildElements(Element gameElement) {
		Elements teams = gameElement.select(".team, .team-name, club");
		Elements scores = gameElement.select(".score, .points, .results");
		
		if(teams.size() >= 2 && scores.size() >= 2) {
			try {
				String team1 = teams.get(0).text().trim();
				String team2 = teams.get(1).text().trim();
				int score1 = Integer.parseInt(scores.get(0).text().replaceAll("\\D", ""));
				int score2 = Integer.parseInt(scores.get(1).text().replaceAll("\\D", ""));
				
				matchservice.addMatch(team1, team2, score1, score2, getCurrentDate());
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
			
		}
		
		return false;
	}


	private boolean parseTextFormat(String gameText) {
		//Exemplos de textos procurados:
		// "Team USA vs Team Canada 245-198"
        // "Team USA 245 x 198 Team Canada"
		
		
		// Padrão 1: "Team A vs Team B 000-000"
		String pattern1 = "(.+?)\\s+vs\\s+(.+?)\\s+(\\d+)-(\\d+)";
		
		if(gameText.matches(pattern1)) {
			String[] parts = gameText.split("\\s+vs\\s+");
			if(parts.length == 2) {
				String team1 = parts[0].trim();
				String secondPart = parts[1].trim();
				String[] scoreParts = secondPart.split("\\s+(\\d+)-(\\d+)");
				if(scoreParts.length >= 1) {
					String team2 = scoreParts[0].trim();
					String scores = secondPart.replaceAll(".*?(\\d+)-(\\d+).*", "$1-$2");
					String[] scoreArray = scores.split("-");
					if (scoreArray.length == 2) {
						int score1 = Integer.parseInt(scoreArray[0]);
						int score2 = Integer.parseInt(scoreArray[1]);
						matchservice.addMatch(team1, team2, score1, score2, getCurrentDate());
						return true;
					}
				}
				
			}
			
		} 
		
        //Padrão 2: "Team A 000 x 000 Team B"
		
		String pattern2 = "(.+?)\\s+(\\d+)\\s+[x×]\\s+(\\d+)\\s+(.+)";
		if(gameText.matches(pattern2)) {
			java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern2);
			java.util.regex.Matcher m = p.matcher(gameText);
			if(m.find()) {
				String team1 = m.group(1).trim();
				int score1 = Integer.parseInt(m.group(2));
				int score2 = Integer.parseInt(m.group(3));
				String team2 = m.group(4).trim();
				matchservice.addMatch(team1, team2, score1, score2, getCurrentDate());
				return true;
				
			}
		}

		
		
		return false;
	}

	private String getCurrentDate() {
		java.time.LocalDate today = java.time.LocalDate.now();
		return today.getDayOfMonth() + "/" + String.format("%02d", today.getMonthValue());
	}





	private final MatchService matchservice; //incluindo dependencia do MatchService
	
	public Scraper(MatchService matchservice) {
		this.matchservice = matchservice;
	} //construtor

	private void addExampleData() {
		System.out.println("Adicionando dados de exemplo...");
		

		
		// Exemplos dos dois primeiros dias
		matchservice.addMatch("Team Australia", "Team England", 212, 189, "03/07");
		matchservice.addMatch("Team France", "Team Germany", 198, 201, "03/07");
		matchservice.addMatch("Team Brazil", "Team Argentina", 176, 165, "03/07");
        
        
		matchservice.addMatch("Team USA", "Team Australia", 234, 210, "04/07");
		matchservice.addMatch("Team Canada", "Team England", 203, 195, "04/07");
		matchservice.addMatch("Team Germany", "Team Brazil", 189, 178, "04/07");
		matchservice.addMatch("Team France", "Team Argentina", 167, 172, "04/07");
		
	}
	
	

	
	private void debugPageStructure(Document doc) {
		System.out.println("\n ESTRUTURA DA PÁGINA (primeiros 20 elementos relevantes):");
		System.out.println("=" .repeat(60));
		
		Elements elementsWithNumbers = doc.select("*:matches(.*\\d+.*)");
		
		int count = 0;
		for (Element element : elementsWithNumbers) {
			if (count >= 100) break; 
			
			String text = element.text().trim();
			if (!text.isEmpty() && text.matches(".*\\d+.*")) {
				System.out.println("Elemento: " + element.tagName());
                System.out.println("Classe: " + element.className());
                System.out.println("ID: " + element.id());
                System.out.println("Texto: " + text);
                System.out.println("Seletor: " + element.cssSelector());
                System.out.println("-".repeat(40));
                System.out.println("HTML: " + element.outerHtml());
                count++;
				
			}
		}
		
		System.out.println("DICA: Copie um dos seletores acima e cole no método 'parseGameResult'");
        System.out.println("Exemplo: doc.select(\"div.score-line\") ou doc.select(\"#results tr\")");
	}

	private Elements tryMultipleSelectors(Document doc) {
		
		String[] selectors = {
				".game-score",".game-result", ".match", ".score" //seletor encotrado no html da pagina para o resultado do jogo
	};
		for (String selector : selectors) {
			
			Elements elements = doc.select(selector);
			if (!elements.isEmpty()) {
				System.out.println("✅ Encontrados elementos com seletor: " + selector);
				return elements;
			}
		}
		
		return new Elements();
		
	}

}
