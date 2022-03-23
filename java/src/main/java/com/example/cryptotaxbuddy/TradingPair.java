package com.example.cryptotaxbuddy;

public class TradingPair {
    private String id;
    private String base_currency;
    private String quote_currency;
    private String base_min_size;
    private String base_max_size;
    private String quote_increment;
    private String base_increment;
    private String display_name;
    private String min_market_funds;
    private String max_market_funds;
    private boolean margin_enabled;
    private boolean fx_stablecoin;
    private String max_slippage_percentage;
    private boolean post_only;
    private boolean limit_only;
    private boolean cancel_only;
    private boolean trading_disabled;
    private String status;
    private String status_message;
    private boolean auction_mode;

    public TradingPair() {
        this.status = "online";
    }

    public TradingPair(String id, String base_currency, String quote_currency, String base_min_size, String base_max_size,
                       String quote_increment, String base_increment, String display_name, String min_market_funds,
                       String max_market_funds, boolean margin_enabled, boolean fx_stablecoin, String status,
                       String status_message, String max_slippage_percentage, boolean post_only, boolean limit_only,
                       boolean cancel_only, boolean trading_disabled, boolean auction_mode) {
        this.id = id;
        this.base_currency = base_currency;
        this.quote_currency = quote_currency;
        this.base_min_size = base_min_size;
        this.base_max_size = base_max_size;
        this.quote_increment = quote_increment;
        this.base_increment = base_increment;
        this.display_name = display_name;
        this.min_market_funds = min_market_funds;
        this.max_market_funds = max_market_funds;
        this.margin_enabled = margin_enabled;
        this.fx_stablecoin = fx_stablecoin;
        this.status = status;
        this.status_message = status_message;
        this.max_slippage_percentage = max_slippage_percentage;
        this.post_only = post_only;
        this.limit_only = limit_only;
        this.cancel_only = cancel_only;
        this.trading_disabled = trading_disabled;
        this.auction_mode = auction_mode;
    }

    public String getId() {
        return id;
    }

    public String getBase_currency() {
        return base_currency;
    }

    public String getQuote_currency() {
        return quote_currency;
    }

    public String getBase_min_size() {
        return base_min_size;
    }

    public String getBase_max_size() {
        return base_max_size;
    }

    public String getQuote_increment() {
        return quote_increment;
    }

    public String getBase_increment() {
        return base_increment;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public String getMin_market_funds() {
        return min_market_funds;
    }

    public String getMax_market_funds() {
        return max_market_funds;
    }

    public boolean isMargin_enabled() {
        return margin_enabled;
    }

    public boolean isFx_stablecoin() {
        return fx_stablecoin;
    }

    public String getStatus() {
        return status;
    }

    public String getStatus_message() {
        return status_message;
    }

    public String getMax_slippage_percentage() {
        return max_slippage_percentage;
    }

    public boolean isPost_only() {
        return post_only;
    }

    public boolean isLimit_only() {
        return limit_only;
    }

    public boolean isCancel_only() {
        return cancel_only;
    }

    public boolean isTrading_disabled() {
        return trading_disabled;
    }

    public boolean isAuction_mode() {
        return auction_mode;
    }
}
