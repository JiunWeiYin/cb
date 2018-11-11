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

package org.cb;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cb.constant.ConstVar;
import org.cb.model.Firm;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.cb.model.Bond;
import org.cb.model.Configuration;
import org.cb.util.Apps;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.cb.constant.ConstVar.*;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String... args) throws Exception {

        // handle configuration
        LOGGER.info("Loading the configuration file.");
        Configuration config = Apps.getConfiguration();
        if (config != null) {
            LOGGER.info(String.format("Loaded the configuration: %s", config));
        }

        String urlBondDaily = config.getUrlBondDaily();
        if (urlBondDaily != null) {
            LOGGER.info(String.format("url of daily bond: %s", urlBondDaily));
        }

        String urlBondPublished = config.getUrlBondPublished();
        if (urlBondPublished != null) {
            LOGGER.info(String.format("url of published bond info: %s", urlBondPublished));
        }

        float fee = config.getFee();
        LOGGER.info(String.format("fee: %s", fee));

        float tax = config.getTax();
        LOGGER.info(String.format("tax: %s", tax));

        float returnPrice = config.getReturnPrice();
        LOGGER.info(String.format("returnPrice: %s", returnPrice));

        float thrshd = config.getThresholdClosingPrice();
        LOGGER.info(String.format("thresholdClosingPrice: %s", thrshd));

        String urlCash = config.getUrlCash();
        if (urlCash != null) {
            LOGGER.info(String.format("url of cash: %s", urlCash));
        }

        String urlProfile = config.getUrlProfile();
        if (urlProfile != null) {
            LOGGER.info(String.format("url of urlProfile: %s", urlProfile));
        }

        String urlTsePrice = config.getUrlTsePrice();
        LOGGER.info(String.format("url of TSE price: %s", urlTsePrice));





        Map<String, Bond> bonds = new HashMap<>();

        processDailyBond(urlBondDaily, bonds, thrshd);

        processPublishedBond(urlBondPublished, bonds, returnPrice);

        processCash(urlCash, bonds);

        calculateValues(bonds, fee, tax);

        processProfile(urlProfile, bonds);

        processTsePrice(urlTsePrice, bonds);

        printResults(bonds, config.getOutputFilePath());

        LOGGER.info("This program was running successfully.");
    }


    private static void processDailyBond(String urlBondDaily, Map<String, Bond> bonds, float thrshd) throws Exception {

        LOGGER.info("===== Processing daily bond info ...... =====");

        Connection conn = Apps.getConnection(urlBondDaily, USER_AGENT, REFERRER, TIME_OUT);
        if (conn != null) {
            LOGGER.info(String.format("Verifying the connection: %s", urlBondDaily));
        }
        conn.timeout(60000);

        // execute connection
        Connection.Response resp = conn.execute();
        if (resp != null) {
            LOGGER.info("The connection has been established.");
        }

        // get connection response status code
        if (resp.statusCode() != 200) {
            LOGGER.error(String.format("The connection response status code is %s. " +
                    "Please check if the internet is working.", resp.statusCode()));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("The connection response status code is %s.", resp.statusCode()));

        // convert HTML to doc
        Document doc = conn.get();
        LOGGER.debug("The HTML has been loaded as a Document object.");

        // select all <table>
        Elements tables = doc.select(TABLE);
        if (tables.size() <= 0) {
            LOGGER.error(String.format("<%s> was not found.", TABLE));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("Got all <%s>.", TABLE));

        // get a specific <table>
        Element table = Apps.searchTable(tables, CLASS, YUI_TEXT_LEFT);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, CLASS, YUI_TEXT_LEFT));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, CLASS, YUI_TEXT_LEFT));

        // get a specific <table>
        table = Apps.searchTable(table.select(TABLE), BGCOLOR, BGCOLOR_VALUE);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, BGCOLOR, BGCOLOR_VALUE));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, BGCOLOR, BGCOLOR_VALUE));

        // get all <tr>
        Elements tr = table.select(TR);
        if (tr.isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TR, urlBondDaily));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("Got the <%s>.", TR));

        // check if <td> is present
        if (tr.select(TD).isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TD, urlBondDaily));
            throw new IllegalArgumentException();
        }
        LOGGER.debug(String.format("Got the <%s>.", TD));

        // get the first index of <td>
        int idxRecord = Apps.indexOfRecord(tr, TD);
        LOGGER.debug(String.format("The first index of <%s> is %s.", TD, idxRecord));
        for (int i = idxRecord; i < tr.size(); i++) {
            Elements td = tr.get(i).select(TD);

            String[] bond = Apps.getValueAsString(td, BOND).split(SEPERATOR_SPACE);
            String id = bond[0]; // eg. 12581 or 49581E
            String name = bond[1]; // eg. 其祥一KY
            float closingPrice = Apps.getValueAsFloat(td, CLOSING_PRICE);
            if (closingPrice == Float.MIN_VALUE) {
                closingPrice = Apps.getValueAsFloat(td, YDAY_CLOSING_PRICE);
            }

            if (closingPrice == Float.MIN_VALUE) {
                LOGGER.warn(String.format("The closing price '%s' of '%s' is an invalid number. Skipped this record.",
                        Apps.getValueAsString(td, CLOSING_PRICE), name));
                continue;
            } else if (closingPrice >= thrshd) {
                LOGGER.warn(String.format("The closing price '%s' of '%s' >= user-defined threshold '%s'. Skipped this record.",
                        closingPrice, name, thrshd));
                continue;
            }

            Bond b = new Bond();
            b.setId(id); // eg. 12581
            b.setName(name); // eg. 其祥一KY
//            b.setTime(Apps.getValueAsString(td, TIME)); // eg. 10:12
            b.setClosingPrice(closingPrice); // eg. 108.5
//            b.setBidPrice(Apps.getValueAsFloat(td, BID_PRICE)); // eg. 107.6
//            b.setOfferPrice(Apps.getValueAsFloat(td, OFFER_PRICE)); // eg. 108.5
//            b.setDailyPricing(Apps.getValueAsString(td, DAILY_PRICING)); // eg. △1.30
//            b.setBoardLot(Apps.getValueAsInt(td, BOARD_LOT)); // eg. 0.0
//            b.setYdayClosingPrice(Apps.getValueAsFloat(td, YDAY_CLOSING_PRICE)); // eg. 108.5
//            b.setOpeningPrice(Apps.getValueAsFloat(td, OPENING_PRICE)); // eg. 0.0
//            b.setDayHigh(Apps.getValueAsFloat(td, DAY_HIGH)); // eg. 108.5
//            b.setDayLow(Apps.getValueAsFloat(td, DAY_LOW)); // eg. 108.5
            b.setPresentDate(new Date()); // eg. 2017/02/28

            bonds.put(id, b);
        }

        LOGGER.info("===== Processing daily bonds finished =====\n\n");
    }

    private static void processPublishedBond(String urlBondPublished, Map<String, Bond> bonds, float returnPrice) throws Exception {

        LOGGER.info("===== Processing published bond info ...... =====");
        LOGGER.info(String.format("Verifying the connection: %s", urlBondPublished));
        CSVParser parser = Apps.readAsCSVParser(urlBondPublished);
        LOGGER.info("The connection has been established.");
        LOGGER.debug("Loaded the CSV as a CSVParser object.");

        Iterator<CSVRecord> iCSVRd = parser.iterator();
        if (iCSVRd.hasNext()) {
            CSVRecord fstRd = iCSVRd.next();
            LOGGER.debug(String.format("first CSV record: %s", fstRd));

            if (!isValid(fstRd, parser.getHeaderMap())) {
                LOGGER.error(String.format("Invalid file: the size of records and header is unidentical in '%s'. " +
                        "Is today a holiday? You may try running this program again during weekday.", urlBondPublished));
                System.exit(5566);
            } else {
                extractCSVRecord(bonds, fstRd, returnPrice);
            }
        } else {
            LOGGER.error(String.format("Invalid CSV file downloaded from '%s'. " +
                    "Is today a holiday? You may try running this program again during weekday.", urlBondPublished));
            System.exit(5566);
        }

        while (iCSVRd.hasNext()) {
            CSVRecord fstRd = iCSVRd.next();
            if (!isValid(fstRd, parser.getHeaderMap())) return;

            extractCSVRecord(bonds, fstRd, returnPrice);
        }

        LOGGER.info("===== Processing published bonds finished =====\n\n");
    }

    private static void processCash(String url, Map<String, Bond> bonds) throws Exception {

        LOGGER.info("===== Processing cash info ...... =====");

        if (bonds.isEmpty()) {
            LOGGER.warn("Since there is no bonds, skip processing cash");
            return;
        }

        for(String idxBondId : bonds.keySet()) {
            LOGGER.debug(String.format("idxBondId: %s", idxBondId));

            String formattedUrl = String.format(url, idxBondId.substring(0, 4));
            LOGGER.debug(String.format("formattedUrl: %s", formattedUrl));

            Connection conn = Apps.getConnection(formattedUrl, USER_AGENT, REFERRER, TIME_OUT);
            if (conn != null) {
                LOGGER.info(String.format("Verifying the connection: %s", formattedUrl));
            }

            // execute connection
            Connection.Response resp = conn.execute();
            if (resp != null) {
                LOGGER.info("The connection has been established.");
            }

            // get connection response status code
            if (resp.statusCode() != 200) {
                LOGGER.error(String.format("The connection response status code is %s. " +
                        "Please check if the internet is working.", resp.statusCode()));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("The connection response status code is %s.", resp.statusCode()));

            // convert HTML to doc
            Document doc = conn.get();
            LOGGER.debug("The HTML has been loaded as a Document object.");

            // select all <table>
            Elements tables = doc.select(TABLE);
            if (tables.size() <= 0) {
                LOGGER.error(String.format("<%s> was not found.", TABLE));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got all <%s>.", TABLE));

            // get a specific <table>
            Element table = Apps.searchTable(tables, CLASS, T01);
            if (table == null) {
                LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, CLASS, T01));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, CLASS, T01));

            // get all <tr>
            Elements tr = table.select(TR);
            if (tr.isEmpty()) {
                LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TR, formattedUrl));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s>.", TR));

            // check if <td> is present
            if (tr.select(TD).isEmpty()) {
                LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TD, formattedUrl));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s>.", TD));

            // get the first index of <td>
            int idxRecord = Apps.indexOfRecord(tr, TD);
            LOGGER.debug(String.format("The first index of <%s> is %s.", TD, idxRecord));

            Bond idxB = bonds.get(idxBondId);
            int cashTotal = 0;
            for (int i = idxRecord; i < tr.size(); i++) {
                Elements td = tr.get(i).select(TD);
                String rowTypeVal = Apps.getValueAsString(td, 0);
                LOGGER.debug(String.format("rowTypeVal: %s", rowTypeVal));

                if (rowTypeVal.endsWith(CASH_1) || rowTypeVal.endsWith(CASH_2)) {
                    cashTotal += Apps.getValueAsInt(td, 1);
                    LOGGER.debug(String.format("cashTotal: %s", cashTotal));
                }

                if (rowTypeVal.endsWith(CASH_2)) {
                    idxB.setAccountsReceivable(Apps.getValueAsInt(td, 1));
                }
            }

            idxB.setCash(cashTotal);
        }

        LOGGER.info("===== Processing url of cash finished =====\n\n");
    }

    // check if the size of records and header is identical
    private static boolean isValid(CSVRecord csvRd, Map<String, Integer> header) {
        return csvRd.size() == header.size();
    }

    private static void extractCSVRecord(Map<String, Bond> bonds, CSVRecord csvRecord, float returnPrice) throws Exception {

        LOGGER.debug(csvRecord.toString());

        String companyCode = csvRecord.get(0).trim();
        if (companyCode.isEmpty()) return;

        String id = csvRecord.get(2).trim(); // eg. 12581 or 49581E

        if (id.isEmpty()) {
            LOGGER.warn(String.format("Bond ID '%s' of company code '%s' in the CSV is empty.", id, companyCode));

        } else if (!bonds.containsKey(id)) {
            LOGGER.warn(String.format("Bond ID '%s' of company code '%s' in the CSV cannot be found from the website of daily bonds.", id, companyCode));

        } else {
            LOGGER.debug(String.format("Bond ID '%s' of company code '%s' in the CSV was found from the website of daily bonds.", id, companyCode));

            Bond idxB = bonds.get(id);
            SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER);
            idxB.setIssuedDate(Apps.formatDate(csvRecord.get(6), formatter));
            idxB.setDueDate(Apps.formatDate(csvRecord.get(7), formatter));
            final int amount = (int) (Long.parseLong(csvRecord.get(8)) / 1000000L);
            final int balance = (int) (Long.parseLong(csvRecord.get(9)) / 1000000L);
            idxB.setAmount(amount);
            idxB.setBalance(balance);
            idxB.setBalanceRatio((float) balance / (float) amount * 100f);
            if (csvRecord.get(30).trim().equals("0") && idxB.getDueDate() != null) {
                idxB.setPutRightDate(idxB.getDueDate());

            } else {
                idxB.setPutRightDate(Apps.formatDate(csvRecord.get(30), formatter));
            }

            final int daysToPutRightDate = (int) TimeUnit.DAYS.convert(idxB.getPutRightDate().getTime() - idxB.getPresentDate().getTime(), TimeUnit.MILLISECONDS);
            idxB.setDaysToPutRightDate(daysToPutRightDate);

            if (daysToPutRightDate >= 0) {
                idxB.setPutRightPrice(Math.max(Float.parseFloat(csvRecord.get(31)), returnPrice));
            } else {
                idxB.setPutRightPrice(returnPrice);
            }

            final int daysToDueDate = (int) TimeUnit.DAYS.convert(idxB.getDueDate().getTime() - idxB.getPresentDate().getTime(), TimeUnit.MILLISECONDS);
            idxB.setDaysToDueDate(daysToDueDate);
        }
    }

    private static void processProfile(String url, Map<String, Bond>  bonds) throws Exception {

        LOGGER.info("===== Getting profile info ...... =====");

        if (bonds.isEmpty()) {
            LOGGER.warn("Since there is no bonds, skip processing cash");
            return;
        }

        for(String idxBondId : bonds.keySet()) {
            LOGGER.debug(String.format("idxBondId: %s", idxBondId));

            String formattedUrl = String.format(url, idxBondId.substring(0, 4));
            LOGGER.debug(String.format("formattedUrl: %s", formattedUrl));

            Connection conn = Apps.getConnection(formattedUrl, USER_AGENT, REFERRER, TIME_OUT);
            if (conn != null) {
                LOGGER.info(String.format("Verifying the connection: %s", formattedUrl));
            }
            conn.timeout(60000);

            // execute connection
            Connection.Response resp = conn.execute();
            if (resp != null) {
                LOGGER.info("The connection has been established.");
            }

            // get connection response status code
            if (resp.statusCode() != 200) {
                LOGGER.error(String.format("The connection response status code is %s. " +
                        "Please check if the internet is working.", resp.statusCode()));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("The connection response status code is %s.", resp.statusCode()));

            // convert HTML to doc
            Document doc = conn.get();
            LOGGER.debug("The HTML has been loaded as a Document object.");

            // select all <table>
            Elements tables = doc.select(TABLE);
            if (tables.size() <= 0) {
                LOGGER.error(String.format("<%s> was not found.", TABLE));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got all <%s>.", TABLE));

            // get a specific <table>
            Element table = Apps.searchTable(tables, CLASS, T01);
            if (table == null) {
                LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, CLASS, T01));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, CLASS, T01));

            // get all <tr>
            Elements tr = table.select(TR);
            if (tr.isEmpty()) {
                LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TR, formattedUrl));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s>.", TR));

            // check if <td> is present
            if (tr.select(TD).isEmpty()) {
                LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TD, formattedUrl));
                throw new IllegalArgumentException();
            }
            LOGGER.debug(String.format("Got the <%s>.", TD));

            // get the first index of <td>
            int idxRecord = Apps.indexOfRecord(tr, TD);
            LOGGER.debug(String.format("The first index of <%s> is %s.", TD, idxRecord));

            Bond idxB = bonds.get(idxBondId);
            for (int i = idxRecord; i < tr.size(); i++) {
                Elements td = tr.get(i).select(TD);

                String rowTypeVal = Apps.getValueAsString(td, 0);
                if (rowTypeVal.equals("開盤價")) {
                    LOGGER.debug(String.format("rowTypeVal: %s", rowTypeVal));
                    float price = Apps.getValueAsFloat(td, 7); // 收盤價, eg. 75.6
                    idxB.setPriceYesterday(price);
                    LOGGER.debug(String.format("昨日收盤價: %s", price));

                } else if (rowTypeVal.equals("今年以來")) {
                    LOGGER.debug(String.format("rowTypeVal: %s", rowTypeVal));
                    float netAssetValue = Apps.getValueAsFloat(td, 3); // 每股淨值(元), eg. 32.96
                    idxB.setNetAssetValue(netAssetValue);
                    LOGGER.debug(String.format("每股淨值(元): %s", netAssetValue));
                }
            }
        }

        LOGGER.info("===== Processing url of profile finished =====\n\n");
    }

    private static void processTsePrice(String url, Map<String, Bond>  bonds) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(ConstVar.FORMATTER); // eg. 2018/02/06
        SimpleDateFormat formatter3 = new SimpleDateFormat(ConstVar.FORMATTER3); // eg. /02/06

        for (String id : bonds.keySet()) {
            final Bond idxB = bonds.get(id);
            final String issuedDate =  formatter.format(idxB.getIssuedDate());
            final String issuedDate3 =  formatter3.format(idxB.getIssuedDate());
            final String firmId = id.substring(0,4);
            final String formattedUrl = String.format(url, issuedDate, firmId);
            LOGGER.info(String.format("formattedUrl: %s", formattedUrl));

            Thread.sleep(5000);

            URL urlFinal = new URL(formattedUrl);
            HttpURLConnection con = (HttpURLConnection) urlFinal.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(60000);
            con.setReadTimeout(60000);
            int resCode = con.getResponseCode();

            if (resCode != 200) {
                String resMsg = con.getResponseMessage();
                LOGGER.error(String.format("response code: %s", resCode));
                LOGGER.error(String.format("response message: %s", resMsg));

                continue;
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            Gson gson = new GsonBuilder().setDateFormat("yyyyMMdd").create();
            Firm firm = gson.fromJson(content.toString(), Firm.class);
            LOGGER.info(String.format("firm: %s", firm));

            List<List<String>> data = firm.getData();
            LOGGER.debug(String.format("data: %s", data));
            if (data == null || data.size() == 0) {
                continue;
            }

            for (List<String> idxData : data) {
                if (idxData.size() < 7) {
                    continue;
                }

                final String date = idxData.get(0); // eg. 107/02/06
                if (date.endsWith(issuedDate3)) {
                    final float closePrice = Float.valueOf(idxData.get(6));
                    LOGGER.info(String.format("closePrice: %s", closePrice));
                    idxB.setPriceIssuedDate(closePrice);
                }
            }
        }
    }


    private static void calculateValues(Map<String, Bond> bonds, float fee, float tax) {

        LOGGER.info("Calculating based on our knowledge ...... =====");

        for (String key : bonds.keySet()) {
            Bond b = bonds.get(key);

            if (b.getPutRightPrice() != Float.NaN && b.getClosingPrice() != 0.0f) {
                b.setRoi(b.getPutRightPrice(), b.getClosingPrice(), fee);
            }

            if (b.getRoi() != Float.NaN && b.getPresentDate() != null && b.getPutRightDate() != null) {
                b.setAnnualizedReturn(b.getRoi(), b.getPresentDate(), b.getPutRightDate(), b.getDueDate());
            }

            if (b.getPutRightPrice() != Float.NaN) {
                b.setEarlyOutPrice(b.getPutRightPrice(), tax);
            }
        }

        LOGGER.info("===== Calculating processes finished =====\n\n");
    }

    private static void printResults(Map<String, Bond> bonds, String outputFilePath) throws Exception {
        LOGGER.info("Writing results.");
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8));
        Iterator<String> iter = bonds.keySet().iterator();
        String firstKey;

        if (iter.hasNext()) {
            firstKey = iter.next();
        } else {
            LOGGER.error("There is no any bonds available for further discussion.");
            throw new RuntimeException("There is no any bonds available for further discussion.");
        }

        w.write(bonds.get(firstKey).printHeader());

        String oLine = bonds.get(firstKey).toString();
        w.write(oLine);

        while (iter.hasNext()) {
            oLine = bonds.get(iter.next()).toString();
            w.write(oLine);
        }

        w.close();

        LOGGER.info(String.format("Wrote results to path '%s' completely.", outputFilePath));
    }
}
