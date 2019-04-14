/**
 * (C) Copyright Chun-Pei Cheng All Rights Reserved
 * NOTICE:  All information contained herein is, and remains the
 * property of Chun-Pei Cheng. The intellectual and technical
 * concepts contained herein are proprietary to Chun-Pei Cheng
 * and are protected by trade secret, patent law or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Chun-Pei Cheng.
 *
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 *
 **/

package org.cb.constant;

public class ConstVar {
    public static final int TIME_OUT = 12000;

    public static final String CONFIG_FILE_NAME = "config.yml";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    public static final String REFERRER = "http://www.google.com";
    public static final String TABLE = "table";
    public static final String TR = "tr";
    public static final String TH = "th";
    public static final String TD = "td";
    public static final String YUI_TEXT_LEFT = "yui-text-left";
    public static final String PLACEHOLDER = "-";

    public static final String PLACEHOLDER2 = "----";
    public static final String CLASS = "class";
    public static final String BGCOLOR = "bgcolor";
    public static final String BGCOLOR_VALUE = "#ffffff";

    public static final String SEPERATOR_SPACE = " ";


    public static final String BOND = "bond";
    public static final String TIME = "time";
    public static final String CLOSING_PRICE = "closingPrice";
    public static final String BID_PRICE = "bidPrice";
    public static final String OFFER_PRICE = "offerPrice";
    public static final String DAILY_PRICING = "dailyPricing";
    public static final String BOARD_LOT = "boardLot";
    public static final String YDAY_CLOSING_PRICE = "ydayClosingPrice";
    public static final String OPENING_PRICE = "openingPrice";
    public static final String DAY_HIGH = "dayHigh";
    public static final String DAY_LOW = "dayLow";
    public static final String BIG5 = "big5";
    public static final String UTF_8 = "UTF-8";

    public static final String FORMATTER = "yyyyMMdd";
    public static final String FORMATTER2 = "yyyy/MM/dd";
    public static final String FORMATTER3 = "/MM/dd";
    public static final int DAYS_YEAR = 365;
    public static final String TBODY = "tbody";

    // profile
    public static final String T01 = "t01";
    public static final String CASH_1 = "現金及約當現金";
    public static final String CASH_2 = "應收帳款及票據";



    public enum FEATURE {
        choice,
        bond,
        time,
        closingPrice,
        bidPrice,
        offerPrice,
        dailyPricing,
        boardLot,
        ydayClosingPrice,
        openingPrice,
        dayHigh,
        dayLow,
    }
}
