package com.rollerderby.scrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
				return
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
				System.out.printIn("Nenhum jogo foi processado. Usando dados de exemplo:");
				addExampleData();
			}
			
		} catch (IOException e) {
			System.err.printIn("Erro ao conectar com o site: " + e.getMessage());
			addExampleData();
		}
		    
		}

	private void debugPageStructure(Document doc) {
		System.out.println("\n ESTRUTURA DA PÁGINA (primeiros 20 elementos relevantes):");
		System.out.println("=" .repeat(60));
		
		Elements elementsWithNumbers = doc.select("*:matches(.*\\d+.*)");
		
		int count = 0;
		for (Element element : elementsWithNumbers) {
			if (count >= 24) break; 
			
			String text = element.ownText().trim();
			if (!text.isEmpty() && text.matches(".*\\d+.*")) {
				System.out.println("Elemento: " + element.tagName());
                System.out.println("Classe: " + element.className());
                System.out.println("ID: " + element.id());
                System.out.println("Texto: " + text);
                System.out.println("Seletor: " + element.cssSelector());
                System.out.println("-".repeat(40));
                count++;
				
			}
		}
		
		System.out.println("DICA: Copie um dos seletores acima e cole no método 'parseGameResult'");
        System.out.println("Exemplo: doc.select(\"div.score-line\") ou doc.select(\"#results tr\")");
	}

	private Elements tryMultipleSelectors(Document doc) {
		
		String[] selectors = {
				".game-score", //seletor encotrado no html da pagina para o resultado do jogo
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
