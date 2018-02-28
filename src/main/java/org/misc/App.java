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

package org.misc;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.model.Bond;
import org.misc.model.Configuration;
import org.misc.util.Apps;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.misc.constant.ConstVar.*;

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
        if (fee >= 0) {
            LOGGER.info(String.format("fee: %s", fee));
        }

        float thrshd = config.getThresholdClosingPrice();
        if (thrshd >= 0) {
            LOGGER.info(String.format("thresholdClosingPrice: %s", thrshd));
        }

        Map<String, Bond> bonds = new HashMap<>();

        processDailyBond(urlBondDaily, bonds, thrshd);

        processPublishedBond(urlBondPublished, bonds);

        calculateValues(bonds, fee);

        printResults(bonds, config.getOutputFilePath());

        LOGGER.info("This program was running successfully.");
    }


    private static void processDailyBond(String urlBondDaily, Map<String, Bond> bonds, float thrshd) throws Exception {

        Connection conn = Apps.getConnection(urlBondDaily, USER_AGENT, REFERRER, TIME_OUT);
        if (conn != null) {
            LOGGER.info(String.format("Verifying the connection: %s", urlBondDaily));
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
            String bondId = bond[0]; // eg. 12581 or 49581E
            String bondName = bond[1]; // eg. 其祥一KY
            float closingPrice = Apps.getValueAsFloat(td, CLOSING_PRICE);

            if (closingPrice == Float.MIN_VALUE) {
                LOGGER.warn(String.format("The closing price '%s' of '%s' is an invalid number. Skipped this record.",
                        Apps.getValueAsString(td, CLOSING_PRICE), bondName));
                continue;
            } else if (closingPrice >= thrshd) {
                LOGGER.warn(String.format("The closing price '%s' of '%s' >= user-defined threshold '%s'. Skipped this record.",
                        closingPrice, bondName, thrshd));
                continue;
            }

            Bond b = new Bond();
            b.setBondId(bondId); // eg. 12581
            b.setBondName(bondName); // eg. 其祥一KY
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

            bonds.put(bondId, b);
        }

        LOGGER.info("Processing daily bonds finished.\n\n");
    }

    private static void processPublishedBond(String urlBondPublished, Map<String, Bond> bonds) throws Exception {

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
                extractCSVRecord(bonds, fstRd);
            }
        } else {
            LOGGER.error(String.format("Invalid CSV file downloaded from '%s'. " +
                    "Is today a holiday? You may try running this program again during weekday.", urlBondPublished));
            System.exit(5566);
        }

        while (iCSVRd.hasNext()) {
            CSVRecord fstRd = iCSVRd.next();
            if (!isValid(fstRd, parser.getHeaderMap())) return;

            extractCSVRecord(bonds, fstRd);
        }

        LOGGER.info("Processing published bonds finished.\n\n");
    }

    // check if the size of records and header is identical
    private static boolean isValid(CSVRecord csvRd, Map<String, Integer> header) {
        return csvRd.size() == header.size();
    }

    private static void extractCSVRecord(Map<String, Bond> bonds, CSVRecord csvRecord) throws Exception {
        LOGGER.debug(csvRecord.toString());

        String companyCode = csvRecord.get(0).trim();
        if (companyCode.isEmpty()) return;

        String bondId = csvRecord.get(2).trim(); // eg. 12581 or 49581E

        if (bondId.isEmpty()) {
            LOGGER.warn(String.format("Bond ID '%s' of company code '%s' in the CSV is empty.", bondId, companyCode));

        } else if (!bonds.containsKey(bondId)) {
            LOGGER.warn(String.format("Bond ID '%s' of company code '%s' in the CSV cannot be found from the website of daily bonds.",
                    bondId, companyCode));

        } else {
            LOGGER.debug(String.format("Bond ID '%s' of company code '%s' in the CSV was found from the website of daily bonds.",
                    bondId, companyCode));

            Bond idxB = bonds.get(bondId);
            SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER);
//                idxB.setIssuedDate(Apps.formatDate(csvRecord.get(6), formatter));
            idxB.setDueDate(Apps.formatDate(csvRecord.get(7), formatter));
//                idxB.setAmount(Long.parseLong(csvRecord.get(8)));
//                idxB.setBalance(Long.parseLong(csvRecord.get(9)));
//                idxB.setCouponRate(Float.parseFloat(csvRecord.get(10)));
            if (csvRecord.get(30).trim().equals("0") && idxB.getDueDate() != null) {
                idxB.setPutRightDate(idxB.getDueDate());

            } else {
                idxB.setPutRightDate(Apps.formatDate(csvRecord.get(30), formatter));
            }

            idxB.setPutRightPrice(Float.parseFloat(csvRecord.get(31)));
        }
    }

    private static void calculateValues(Map<String, Bond> bonds, float fee) {

        for (String key : bonds.keySet()) {
            Bond b = bonds.get(key);

            if (b.getPutRightPrice() != Float.NaN && b.getClosingPrice() != 0.0f) {
                b.setRoi(b.getPutRightPrice(), b.getClosingPrice(), fee);
            }

            if (b.getRoi() != Float.NaN && b.getPresentDate() != null && b.getPutRightDate() != null) {
                b.setAnnualizedReturn(b.getRoi(), b.getPresentDate(), b.getPutRightDate(), b.getDueDate());
            }
        }

        LOGGER.info("Calculating processes finished.\n\n");
    }

    private static void printResults(Map<String, Bond> bonds, String outputFilePath) throws Exception {
        LOGGER.info("Writing results.");
        Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_8));
        Iterator<String> iter = bonds.keySet().iterator();
        StringBuilder header = new StringBuilder();
        String firstKey;

        if (iter.hasNext()) {

            firstKey = iter.next();
            for (Field field : bonds.get(firstKey).getClass().getDeclaredFields()) {
                if (!header.toString().isEmpty()) {
                    header.append(",");
                }
                if (!Modifier.isStatic(field.getModifiers())) {
                    header.append(field.getName());
                }
            }
        } else {
            LOGGER.error("There is no any bonds available for further discussion.");
            throw new RuntimeException("There is no any bonds available for further discussion.");
        }

        String fmtHeader = header.append("\n").toString().replace(",", "\t");
        w.write(fmtHeader);

        String oLine = bonds.get(firstKey).toString().replace(",", "\t").concat("\n");
        w.write(oLine);

        while (iter.hasNext()) {
            oLine = bonds.get(iter.next()).toString().replace(",", "\t").concat("\n");
            w.write(oLine);
        }

        w.close();

        LOGGER.info(String.format("Wrote results to path '%s' completely.", outputFilePath));
    }
}
