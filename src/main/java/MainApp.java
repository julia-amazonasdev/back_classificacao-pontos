
import com.rollerderby.scrapper.GoogleSheetScraper;
import com.rollerderby.scrapper.Scraper;
import com.rollerderby.services.MatchService;



public class MainApp {
	public static void main(String[] args) {
		MatchService matchservice = new MatchService();
		GoogleSheetScraper sheetScraper = new GoogleSheetScraper(matchservice);
		
        String websiteUrl = "https://www.jkershaw.com/roller-derby.html";
        
        sheetScraper.scrapeFromGoogleSheet(); 
		matchservice.generateClassification(); 
        matchservice.showMatchHistory(); 
        
        System.out.println("\n=== PRÓXIMOS JOGOS ===");
        System.out.println("05/07/2025 - Terceiro dia de competições");
        System.out.println("06/07/2025 - Finais (3º/4º lugar às 15:30, Final às 17:30)");

	}

}
