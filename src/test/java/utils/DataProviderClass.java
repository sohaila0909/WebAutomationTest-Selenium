package utils;


import org.testng.annotations.DataProvider;

public class DataProviderClass {
	@DataProvider(name = "SearchTermTest") 
	public Object[][] tData(){
	
		return new Object[][] {
			{"Vodafone"}, 
			{"Vodafone VOIS"},
			{"Vodafone logo"},
		};
	}
}
