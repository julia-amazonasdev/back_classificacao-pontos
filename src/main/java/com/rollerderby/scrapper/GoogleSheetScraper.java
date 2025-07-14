package com.rollerderby.scrapper;

import com.rollerderby.services.MatchService;
import com.rollerderby.entity.Match;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.*;

public class GoogleSheetScraper {
	private static final String SHEET_URL = "https://docs.google.com/spreadsheets/d/11UCFnwJwGB60JF2prV_Yp4ccTMonGWWzSeMMWXNjL7s/gviz/tq?tqx=out:json";
	
	private MatchService matchservice;
	
	public GoogleSheetScraper(MatchService matchservice) {
		this.matchservice = matchservice; 
	}
	
	public void scrapeFromGoogleSheet() {
		try {
			HttpClient client =  HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(SHEET_URL))
					.build();
			
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			
			System.out.println("Status code: " + response.statusCode());
			System.out.println("Body:\n" + response.body());
			
			
			String rawJson = response.body();
			
			//Limpar prefixo do Google
			String cleanJson = rawJson.replaceFirst(".*setResponse\\(", "").replaceFirst("\\);\\s*$", "");
			
			//Parsear JSON com Gson
			
			JsonObject jsonobject = JsonParser.parseString(cleanJson).getAsJsonObject();
			JsonArray rows = jsonobject
					.getAsJsonObject("table")
					.getAsJsonArray("rows");
			
			for(JsonElement row : rows) {
				JsonArray cells = row.getAsJsonObject().getAsJsonArray("c");
				
				
				String score1Str = getCell(cells, 3);
				String score2Str = getCell(cells, 4);
				String teamsText = getCell(cells, 5);
				String date = "03/07";
				
				if(teamsText != null && teamsText.contains(" v ") && score1Str != null && score2Str != null) {
					String[] teams = teamsText.split(" v ");
					if(teams.length == 2) {
						String team1 = teams[0].trim();
						String team2 = teams[1].trim();
						
						try {
		                    int score1 = (int) Double.parseDouble(score1Str);
		                    int score2 = (int) Double.parseDouble(score2Str);
		                    System.out.printf("Adicionando jogo: %s %d x %d %s\n", team1, score1, score2, team2);
		                    matchservice.addMatch(team1, team2, score1, score2, date);
		                } catch (NumberFormatException e) {
		                	System.err.println("Erro ao converter placar: " + score1Str + " x " + score2Str);
		                }
					}
				}
			}
			
			System.out.println("✅ Dados extraídos da planilha do Google com sucesso!");

        } catch (IOException | InterruptedException e) {
        	System.err.println("Erro ao buscar dados da planilha:");
        	e.printStackTrace(); // mostra a linha e o tipo de erro

        }
					
	}
	
	 private String getCell(JsonArray cells, int index) {
		 if (index >= cells.size() || cells.get(index) == null || cells.get(index).isJsonNull()) return null;
	        JsonObject cell = cells.get(index).getAsJsonObject();
	        return cell.has("v") ? cell.get("v").getAsString() : null;
	    }
}
