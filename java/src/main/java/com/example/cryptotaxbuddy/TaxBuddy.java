package com.example.cryptotaxbuddy;

import java.security.NoSuchAlgorithmException;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Scanner;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

public class TaxBuddy {

    private static final String URL = "https://api.pro.coinbase.com";
    private static final String CB_ACCESS_TIMESTAMP = String.valueOf(Instant.now().getEpochSecond());
    private static final String CB_ACCESS_KEY = System.getenv("CB_ACCESS_KEY");
    private static final String CB_ACCESS_PASSPHRASE = System.getenv("CB_ACCESS_PASSPHRASE");
    private static final String SECRET = System.getenv("SECRET");
    private Mac sha256_HMAC;
    private RestTemplate restTemplate = new RestTemplate();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        TaxBuddy start = new TaxBuddy();
        start.run();
    }

    public void run()  {
        System.out.println("Press 1 to get account balances, or 2 to get historical data for a trading pair " +
                "at a specific date and time.");
        String userInput = "";

        while(!userInput.equals("1") && !userInput.equals("2")) {
            userInput = scanner.nextLine();
            if(userInput.equals("1")) {
                accountBalance();
            } else if (userInput.equals("2")) {
                ohlcData();
            } else {
                System.out.println("Not a valid input, try again.");
            }
        }
    }

    public void accountBalance() {
        String path = "/accounts";
        HttpEntity<String> httpEntity = createHeaders(path);

        try {
            ResponseEntity<AccountRecord[]> responseEntity = restTemplate.exchange(URL + path, HttpMethod.GET,
                    httpEntity, AccountRecord[].class);

            AccountRecord[] accounts = responseEntity.getBody();

            System.out.println();

            for (AccountRecord account : accounts) {
                if (Float.parseFloat(account.getBalance()) > 0) {
                    System.out.println("You have " + account.getAvailable() + " " + account.getCurrency());
                }
            }
        } catch (HttpStatusCodeException e) {
            System.out.println(e.getStatusCode().value());
        }

        scanner.close();

        System.exit(0);
    }

    public void ohlcData() {
        System.out.println("");
        String pair = userTradingPair();
        System.out.println("");

        LocalDate date = userDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println("");

        LocalTime time = userTime();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        String path = "/products/" + pair + "/candles?start=" + date.format(dateFormatter) + "T" +
                time.format(timeFormatter) + ":00&end="  + date.format(dateFormatter) + "T" +
                time.plusSeconds(60).format(timeFormatter) + ":00&granularity=60";

        try {
            ResponseEntity<String[][]> responseEntity = restTemplate.getForEntity(URL + path, String[][].class);

            System.out.println("");
            System.out.println("Historical OHLC data for " + pair + " on " + date.format(dateFormatter)
                    + " at " + time.format(timeFormatter) + " UTC");

            String[][] candlesticks = responseEntity.getBody();
            String[] rightCandle = candlesticks.length == 1 ? candlesticks[0] : candlesticks[1];

            System.out.println("open: " + rightCandle[3]);
            System.out.println("high: " + rightCandle[2]);
            System.out.println("low: " + rightCandle[1]);
            System.out.println("close: " + rightCandle[4]);
        }
        catch (HttpStatusCodeException e) {
            System.out.println(e.getStatusCode().value());
        }

        System.exit(0);
    }

    public HttpEntity<String> createHeaders(String path) {
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
        } catch(NoSuchAlgorithmException e) {
            System.out.println("invalid algorithm");
        }

        //decode base64 secret and create a sha256 hmac with it
        SecretKeySpec key = new SecretKeySpec(Base64.getDecoder().decode(SECRET), "HmacSHA256");
        try {
            sha256_HMAC.init(key);
        } catch(InvalidKeyException e) {
            System.out.println("invalid key");
        }

        //prehash string
        HttpMethod httpMethod = HttpMethod.GET;
        String message = CB_ACCESS_TIMESTAMP + httpMethod.name() + path;

        //sign the message with hmac and base64 encode the result
        String cb_access_sign = Base64.getEncoder().encodeToString(sha256_HMAC.doFinal(message.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "APPLICATION_JSON");
        headers.set("cb-access-key", CB_ACCESS_KEY);
        headers.set("cb-access-passphrase", CB_ACCESS_PASSPHRASE);
        headers.set("cb-access-sign", cb_access_sign);
        headers.set("cb-access-timestamp", CB_ACCESS_TIMESTAMP);

        HttpEntity result = new HttpEntity(headers);
        return result;
    }

    public String userTradingPair() {
        boolean validSymbol = false;
        String requestPath = "";
        String pair = "";

        System.out.println("Enter a trading pair: ");
        System.out.println("ex. btc-usd, eth-usdc");

        while(!validSymbol) {
            pair = scanner.nextLine();

            if(pair.equals("1")) {
                System.exit(0);
            }

            requestPath = "/products/" + pair;
            HttpEntity<String> entity = createHeaders(requestPath);

            try {
                ResponseEntity<TradingPair> responseEntity = restTemplate.exchange(URL + requestPath, HttpMethod.GET,
                        entity, TradingPair.class);
                if (responseEntity.getStatusCode() == HttpStatus.OK) {
                    validSymbol = true;
                }
            } catch (HttpStatusCodeException e) {
                System.out.println("Not a valid trading pair, please try again or press 1 to quit");
            }
        }

        return pair;
    }

    public LocalDate userDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = null;

        System.out.println("Now, enter the date or press 2 to get current data: ");
        System.out.println("ex. 2020-08-24");

        while(date == null) {

            String dateString = scanner.nextLine();

            if(dateString.equals("1")) {
                System.exit(0);
            } else if(dateString.equals("2")) {
                date = LocalDate.now(ZoneOffset.UTC);
            } else {
                try {
                    date = LocalDate.parse(dateString, dateFormatter);
                    if(date.isAfter(LocalDate.now())) {
                        System.out.println("Date is in the future, try again or press 1 to quit");
                        date = null;
                    }
                } catch(DateTimeParseException e) {
                    System.out.println("Not a valid date, please try again or press 1 to quit");
                }
            }
        }

        return date;
    }

    public LocalTime userTime() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime time = null;

        System.out.println("Lastly, enter the time in UTC or press 2 if you want the current data: ");
        System.out.println("ex. 14:30");

        while(time == null) {
            String timeString = scanner.nextLine();

            if(timeString.equals("1")) {
                System.exit(0);
            } else if(timeString.equals("2")) {
                time = LocalTime.now(ZoneOffset.UTC).minusMinutes(1).truncatedTo(ChronoUnit.MINUTES);
            } else {
                try {
                    time = LocalTime.parse(timeString, timeFormatter);
                } catch(DateTimeParseException e) {
                    System.out.println("Not a valid time, please try again or press 1 to quit");
                }
            }
        }

        scanner.close();

        return time;
    }
}