package client;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.web.client.HttpClientErrorException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.http.HTTPException;
import java.net.SocketTimeoutException;

import org.xml.sax.InputSource;
import java.io.StringReader;
import org.json.JSONArray;
import service.core.Portfolio;
import service.core.Report;
import service.core.Stock;
import service.core.StockRequest;


public class Main {
	private static boolean loggedIn = false;
	private static int sessionID = -1;
	private static boolean runApplication = true;
	private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static OkHttpClient client = new OkHttpClient.Builder()
			.connectTimeout(10000, TimeUnit.MILLISECONDS)
			.readTimeout(10000, TimeUnit.MILLISECONDS)
			.writeTimeout(10000, TimeUnit.MILLISECONDS)
			.build();
	
	public static void main(String[] args) throws IOException{
		System.out.println("Welcome to the Quotation System");
		while(runApplication){
			if(!loggedIn){
				String ad = getAd();
				if (ad==null){
					ad = "Trade smarter, not harder! Join our market for expert insights and low fees.";
				}

				System.out.println("=====================ADVERTISEMENT======================");
				System.out.println(ad);
				System.out.println("========================================================");

				System.out.println("----------MENU----------");
				System.out.println("1. Login");
				System.out.println("2. Create Account");
				System.out.println("3. Get Stock Information");
				System.out.println("4. Get News Headlines");
				System.out.println("5. Crash User Service (To Show Fault Tolerance, This does take some time to recover, you can watch in Docker)");
				System.out.println("6. Exit");
				System.out.println("------------------------");

				System.out.println("Please enter the number of the option you would like to select: ");
				String optionStr = reader.readLine();
				if(optionStr.matches("[0-9]+")){
					int option = Integer.parseInt(optionStr);
					NotLoggedInOptions(option);
				}
				else{
					System.out.println("Invalid option selected. Please try again.");
				}
			}else{
				System.out.println("----------MENU----------");
				System.out.println("1. Buy Stock");
				System.out.println("2. Sell Stock");
				System.out.println("3. Get Stock Information");
				System.out.println("4. Get Trending Stocks");
				System.out.println("5. Get Portfolio Report");
				System.out.println("6. Add Funds");
				System.out.println("7. Withdraw Funds");
				System.out.println("8. Logout");
				System.out.println("------------------------");


				System.out.println("Please enter the number of the option you would like to select: ");
				String optionStr = reader.readLine();
				if(optionStr.matches("[0-9]+")){
					int option = Integer.parseInt(optionStr);
					LoggedInOptions(option);
				}
				else{
					System.out.println("Invalid option selected. Please try again.");
				}
			}
		}
		System.exit(0);
	}

	public static void NotLoggedInOptions(int option){
		switch(option){
			case 1:
				try {
					sessionID = login();
				} catch (IOException e) {
					System.out.println("An error occurred while logging in. Please try again later.");
				}

				break;
			case 2:
				try {
					sessionID = createAccount();
					if(sessionID != -1){
						loggedIn = true;
						getPortfolio(sessionID);
					}
				} catch (IOException e) {
					System.out.println("An error occurred while creating the account. Please try again later.");
				}
				break;
			case 3:
				System.out.println("Please enter the stock symbol you would like to get information for: ");
				String symbol = "";
				try {
					symbol = reader.readLine();
				} catch (IOException e) {
					System.out.println("Something went wrong. Please try again.");
				}
				if(symbol.length() == 0){
					System.out.println("Invalid stock symbol. Please try again.");
				}
				else{
					getStockInformation(symbol);
				}
				break;
			case 4:
				System.out.println("\n=====================NEWS=====================");
				String ip = getServiceIp();
				getNewsHeadlines();
				System.out.println("==============================================\n");
				break;
			case 5:
				System.out.println("Crashing User Service...");
				crashUserService();
				break;
			case 6:
				System.out.println("Exiting application...");
				runApplication = false;
				break;
			default:
				System.out.println("Invalid option selected. Please try again.");
		}
	}

	public static void LoggedInOptions(int option) {
		switch (option) {
			case 1:
                try {
                    buyStock();
					break;
                } catch (IOException e) {
                   System.out.println("An error occurred while buying the stock. Please try again later.");
                }
			case 2:
				try{ 
					sellStock();
					break;
				} catch (IOException e) {
					System.out.println("An error occurred while selling the stock. Please try again later.");
				}
			case 3:
				System.out.println("Please enter the stock symbol you would like to get information for: ");
				String symbol = "";
				try {
					symbol = reader.readLine();
				} catch (IOException e) {
					System.out.println("Something went wrong. Please try again.");
				}
				if(symbol.length() == 0){
					System.out.println("Invalid stock symbol. Please try again.");
				}
				else{
					getStockInformation(symbol);
				}
				break;
			case 4:
				System.out.println("Getting trending stocks...");
				getTrendingStocks();
				break;
			case 5:
				System.out.println("Getting report...");
				getReport();
				break;
			case 6:
				System.out.println("Please enter the amount you would like to add to your account: ");
				String amountStr = "";
				try {
					amountStr = reader.readLine();
				} catch (IOException e) {
					System.out.println("Something went wrong. Please try again.");
				}
				if(amountStr.matches("[0-9]+")){
					int amount = Integer.parseInt(amountStr);
					addFunds(amount);
				}
				else{
					System.out.println("Invalid amount entered. Please try again.");
				}
				break;
			case 7:
				System.out.println("Please enter the amount you would like to withdraw from your account: ");
				String withdrawAmountStr = "";
				try {
					withdrawAmountStr = reader.readLine();
				} catch (IOException e) {
					System.out.println("Something went wrong. Please try again.");

				}
				if(withdrawAmountStr.matches("[0-9]+")){
					int amount = Integer.parseInt(withdrawAmountStr);
					withdrawFunds(amount);
				}
				else{
					System.out.println("Invalid amount entered. Please try again.");
				}
				break;
			case 8:
				System.out.println("Logging out...");
				loggedIn = false;
				sessionID = -1;
				break;
			default:
				System.out.println("Invalid option selected. Please try again.");
		}
	}

	public static void crashUserService(){
		String url = getServiceIp() + "/crash";
		OkHttpClient crashClient = new OkHttpClient.Builder()
						.connectTimeout(10000, TimeUnit.MILLISECONDS)
						.readTimeout(5000, TimeUnit.MILLISECONDS)
						.writeTimeout(10000, TimeUnit.MILLISECONDS)
						.build();
				Request request = new Request.Builder()
						.url(url)
						.get()
						.build();
		try {
			crashClient.newCall(request).enqueue(new Callback() {
				@Override
				public void onFailure(Call call, IOException e) {
				}
			
				@Override
				public void onResponse(Call call, Response response) throws IOException {
					response.close();
				}
			});
			crashClient = null;
		} 
		 catch (Exception e) {
		}
		
		System.out.println("User service has been crashed. Please wait for it to recover.");
	}

	public static String getServiceIp(){
		return "http://localhost:8080";
	}

	public static void withdrawFunds(int amount) {
		double balance = getBalance();
		if (balance < amount) {
			System.out.println("Insufficient funds");
			return;
		}
		double newBalance = balance - amount;
	
		try {
			updateBalance(newBalance);
		} catch (IOException e) {
			System.out.println("An error occurred while withdrawing funds. Please try again later.");
			return;
		}
	
		double updatedBalance = getBalance();
		System.out.println("Your balance is: " + updatedBalance);
	}
	public static void updateBalance(double amount) throws IOException {
		String url = getServiceIp() + "/updatebalance" + "/" + sessionID;
	
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(amount);
	
		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(requestBody, MediaType.parse("application/json")))
				.build();
	
		try (Response response = client.newCall(request).execute()){
			if (!response.isSuccessful()) {
				System.out.println("An error occurred while updating the balance. Please try again later.");
				return;
			}
			String responseBody = response.body().string();
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while updating the balance. Please try again later.");
		}
	}


	public static void addFunds(int amount) {
		try {
			updateBalance(getBalance() + amount);
		} catch (IOException e) {
			System.out.println("An error occurred while adding funds. Please try again later.");
		}
	}
	public static double getBalance() {
		String url = getServiceIp() + "/getbalance" + "/" + sessionID;
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			response.close();
			return Double.parseDouble(responseBody);
		} catch (Exception e) {
			System.out.println("An error occurred while getting the balance. Please try again later.");
		}
		return -1;
	}

	private static String getAd(){
		String url = getServiceIp() + "/getadvertisement";
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			if (!response.isSuccessful()) {
				if (response.code() == 404){
					System.out.println("No advertisement available at the moment.");
				}


				return null;
			}
			String responseBody = response.body().string();
			response.close();
			return responseBody;
		} catch (Exception e) {
			System.out.println("An error occurred while getting the advertisement. Please try again later, if the problem persists contact support.");
		}
		return null;
	
	}


	public static boolean buyStock() throws IOException{
		System.out.println("Please enter the stock symbol you would like to buy: ");
		String symbol = reader.readLine();
		System.out.println("Please enter the quantity you would like to buy: ");
		String quantityStr = reader.readLine();
		int quantity = Integer.parseInt(quantityStr);
		String url = getServiceIp() + "/buystock" + "/" + sessionID;
	
		StockRequest requestObj = new StockRequest(symbol, quantity);
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(requestObj);

		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(requestBody, MediaType.parse("application/json")))
				.build();

		try (Response response = client.newCall(request).execute()){
			if (response.code() != 200) {
				switch (response.code()) {
					case 401:
						System.out.println("Insufficient funds to buy stock. Please add funds to your account and try again");
						break;
					case 404:
						System.out.println("Balance not found. Please try again.");
						break;
					case 503:
						System.out.println("Buy stock service is unavailable. Please try again later.");
						break;
					default:
						System.out.println("An error occurred while buying the stock. Please try again later, if the problem persists contact support.");
						break;
				}
				return false;
			}
			String responseBody = response.body().string();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Portfolio portfolio = mapper.readValue(responseBody, Portfolio.class);
			System.out.println(portfolio.toString());
			response.close();
			return true;
		} catch (Exception e) {
			System.out.println("An error occurred while buying the stock. Please try again later.");
		}
		return false;
	}

	public static boolean sellStock() throws IOException{
		System.out.println("Please enter the stock symbol you would like to sell: ");
		String symbol = reader.readLine();
		System.out.println("Please enter the quantity you would like to sell: ");
		String quantityStr = reader.readLine();

		int quantity = 0;
		try {
			quantity = Integer.parseInt(quantityStr);
		} catch (NumberFormatException e) {
			System.out.println("Invalid quantity entered. Please try again.");
			return false;
		}
		String url = getServiceIp() + "/sellstock" + "/" + sessionID;
	
		StockRequest requestObj = new StockRequest(symbol, quantity);
		ObjectMapper mapper = new ObjectMapper();
		String requestBody = mapper.writeValueAsString(requestObj);
	
		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(requestBody, MediaType.parse("application/json")))
				.build();
	
		try (Response response = client.newCall(request).execute()){
			if (!response.isSuccessful()) {
				switch (response.code()) {
					case 401:
						System.out.println("Insufficient stock quantity to sell. Please try again.");
						break;
					case 404:
						System.out.println("Balance not found. Please try again.");
						break;
					case 500:
						System.out.println("Sell stock service is unavailable. Please try again later.");
						break;
					case 406:
						System.out.println("You don't own the stock you are trying to sell. Please purchase the stock first.");
						break;
					case 400:
						System.out.println("You Don't own any stock, Please buy some stock first");
						break;
					default:
						System.out.println("An error occurred while selling the stock. Please try again later, if the problem persists contact support.");
						break;
				}
				return false;
			}
			String responseBody = response.body().string();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Portfolio portfolio = mapper.readValue(responseBody, Portfolio.class);
			System.out.println(portfolio.toString());
			response.close();
			return true;
		} catch (Exception e) {
			System.out.println("An error occurred while selling the stock. Please try again later.");
		}
		return false;
	}






		public static void getNewsHeadlines(){
		String url = getServiceIp() + "/news";
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			JSONArray json = new JSONArray(responseBody);
			System.out.println("News Headlines:");
			int counter = 0;
			for(int i = 0; i < json.length(); i++){
				int random = (int)(Math.random() * 2);
				if(random != 0){
					JSONObject obj = json.getJSONObject(i);
					System.out.println(obj.getString("title"));
					counter++;
				}
				if(counter == 5){
					break;
				}
			}
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while getting the news headlines. Please try again later.");
		}
	}

	public static int login() throws IOException{
		String url = getServiceIp() + "/login";
		System.out.println("Please enter your email: ");
		String email = reader.readLine();
		while(!email.contains("@") || !email.contains(".")){
			System.out.println("Invalid email. Please enter a valid email: ");
			email = reader.readLine();
		}
		System.out.println("Please enter your password: ");
		String password = reader.readLine();
		while(password.length() == 0){
			System.out.println("Please enter a valid password: ");
			password = reader.readLine();
		}
		JSONArray json = new JSONArray();
		json.put(email);
		json.put(password);
		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			System.out.println(responseBody);
			if(!responseBody.matches("[0-9]+")){
				System.out.println("Login failed. Please try again.");
				response.close();
				return -1;
			}
			int returnedSessionID = Integer.parseInt(responseBody);
			if(returnedSessionID != -1){
				System.out.println("Login successful. Welcome " + email + "!");
				loggedIn = true;
				sessionID = returnedSessionID;
				getPortfolio(sessionID);
			}
			else{
				System.out.println("Login failed. Please try again.");
			}
			response.close();
			return returnedSessionID;
		} catch (HttpClientErrorException.NotFound ex ) {
			System.out.println("No account found with the provided email. Please try again.");
		} catch (HttpClientErrorException.Unauthorized e) {
			System.out.println("Incorrect password. Please try again.");
		} catch (Exception e) {
			if(e.getMessage().contains("Connection refused")){
				System.out.println("The login service is currently unavailable. Please try again later.");
		}
			System.out.println("An error occurred while logging in. Please try again later.");
		}
		return -1;
	}

	public static int createAccount() throws IOException{
		String url = getServiceIp() + "/createuser";
		System.out.println("Please enter your first name: ");
		String firstName = reader.readLine();
		while(firstName.length() == 0){
			System.out.println("Please enter a valid first name: ");
			firstName = reader.readLine();
		}
		System.out.println("Please enter your last name: ");
		String lastName = reader.readLine();
		while(lastName.length() == 0){
			System.out.println("Please enter a valid last name: ");
			lastName = reader.readLine();
		}
		System.out.println("Please enter your email: ");
		String email = reader.readLine();
		while(!email.contains("@") || !email.contains(".")){
			System.out.println("Invalid email. Please enter a valid email: ");
			email = reader.readLine();
		}
		System.out.println("Please enter your password: ");
		String password = reader.readLine();
		while(password.length() == 0){
			System.out.println("Please enter a valid password: ");
			password = reader.readLine();
		}
		JSONArray json = new JSONArray();
		json.put(firstName);
		json.put(lastName);
		json.put(email);
		json.put(password);
		Request request = new Request.Builder()
				.url(url)
				.post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			System.out.println(responseBody);
			if(responseBody.matches("[0-9]+")){
				System.out.println("Account created successfully. You are now logged in.");
				response.close();
				return Integer.parseInt(responseBody);
			}
			else{
				System.out.println("Account creation failed. Please try again.");
				response.close();
				return -1;
			}
		} catch (Exception e) {
			System.out.println("An error occurred while creating the account. Please try again later.");
		}
		return -1;
	}

	public static Portfolio getPortfolio(int sessionID){
		String url = getServiceIp() + "/getportfolio/"  + sessionID;
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			Portfolio portfolio = mapper.readValue(responseBody, Portfolio.class);
			System.out.println(portfolio.toString());
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while getting the portfolio. Please try again later.");
		}
		return null;
	}

	public static void getTrendingStocks(){
		String url = getServiceIp() + "/gettrending";
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			if (!response.isSuccessful()) {
				if (response.code() == 503){
					System.out.println("Trending stocks service is unavailable. Please try again later.");
				}
				else{
					System.out.println("An error occurred while getting the trending stocks. Please try again later.");
				}
				return;
			}
			String responseBody = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<Stock> stocks = mapper.readValue(responseBody, new TypeReference<List<Stock>>(){});
			System.out.println("Trending Stocks:");
			for(Stock stock : stocks){
				System.out.println(stock.toString());
			}
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while getting the trending stocks. Please try again later.");
		}
	}

	public static void getStockInformation(String symbol){
		String url = getServiceIp() + "/getstock/" + symbol;
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			ObjectMapper mapper = new ObjectMapper();
			String responseBody = response.body().string();

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Stock stock = mapper.readValue(responseBody, new TypeReference<Stock>(){});
			System.out.println(stock.toString());
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while getting the stock information. Please try again later.");
		}
	}


	public static void getReport(){
		String url = getServiceIp() + "/getreport/" + sessionID;
		Request request = new Request.Builder()
				.url(url)
				.get()
				.build();
		try (Response response = client.newCall(request).execute()){
			String responseBody = response.body().string();
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Report report = mapper.readValue(responseBody, Report.class);
			System.out.println(report.toString());
			response.close();
		} catch (Exception e) {
			System.out.println("An error occurred while getting the report. Please try again later.");
		}
	}
}

