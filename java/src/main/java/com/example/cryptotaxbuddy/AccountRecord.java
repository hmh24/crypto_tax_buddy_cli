package com.example.cryptotaxbuddy;


public class AccountRecord {
	private String id;
	private String currency;
	private String balance;
	private String available;
	private String hold;
	private String profile_id;
	private boolean trading_enabled;

	public AccountRecord() {}

	public AccountRecord(String id, String currency, String balance,  String available, String hold,
						 String profile_id, boolean trading_enabled) {
		this.id = id;
		this.currency = currency;
		this.balance = balance;
		this.available = available;
		this.hold = hold;
		this.profile_id = profile_id;
		this.trading_enabled = trading_enabled;
	}

	public String getAvailable() {
		return available;
	}

	public String getBalance() {
		return balance;
	}

	public String getCurrency() {
		return currency;
	}

	public String getHold() {
		return hold;
	}

	public String getId() {
		return id;
	}

	public String getProfile_id() {
		return profile_id;
	}

	public Boolean getTrading_enabled() {
		return trading_enabled;
	}
}
