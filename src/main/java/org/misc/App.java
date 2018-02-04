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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.misc.constant.ConstVar.*;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String... args) throws Exception {

        // handle configuration
        LOGGER.info("Loading the configuration file.");
        Configuration config = Apps.getConfiguration();
        if (config != null) {
            LOGGER.info(String.format("Loaded the configuration: %s.", config));
        }

        String urlBondDaily = config.getUrlBondDaily();
        if (urlBondDaily != null) {
            LOGGER.info(String.format("url of daily bond: %s.", urlBondDaily));
        }

        String urlBondPublished = config.getUrlBondPublished();
        if (urlBondPublished != null) {
            LOGGER.info(String.format("url of published bond info: %s.", urlBondPublished));
        }

        Map<String, Bond> bonds = new HashMap<>();

        processDailyBond(urlBondDaily, bonds);

        processPublishedBond(urlBondPublished, bonds);


        System.out.println("Bond_Id\tBond_Name\tClosing_Price\tPresent_Date\tDue_Date\tROI\tROI_Year");


        LOGGER.info("This program was running successfully.");
    }


    private static void processDailyBond(String urlBondDaily, Map<String, Bond> bonds) throws Exception {

        Connection conn = Apps.getConnection(urlBondDaily, USER_AGENT, REFERRER, TIME_OUT);
        if (conn != null) {
            LOGGER.info("Verifying the connection.");
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

            Bond b = new Bond();
            b.setBondName(bond[1]); // eg. 其祥一KY
            b.setTime(Apps.getValueAsString(td, TIME)); // eg. 10:12
            b.setClosingPrice(Apps.getValueAsFloat(td, CLOSING_PRICE)); // eg. 108.5
            b.setBidPrice(Apps.getValueAsFloat(td, BID_PRICE)); // eg. 107.6
            b.setOfferPrice(Apps.getValueAsFloat(td, OFFER_PRICE)); // eg. 108.5
            b.setDailyPricing(Apps.getValueAsString(td, DAILY_PRICING)); // eg. △1.30
            b.setBoardLot(Apps.getValueAsInt(td, BOARD_LOT)); // eg. 0.0
            b.setYdayClosingPrice(Apps.getValueAsFloat(td, YDAY_CLOSING_PRICE)); // eg. 108.5
            b.setOpeningPrice(Apps.getValueAsFloat(td, OPENING_PRICE)); // eg. 0.0
            b.setDayHigh(Apps.getValueAsFloat(td, DAY_HIGH)); // eg. 108.5
            b.setDayLow(Apps.getValueAsFloat(td, DAY_LOW)); // eg. 108.5
            b.setPresent(new Date()); // eg. 2017/02/28

            bonds.put(bondId, b);
        }

    }

    private static void processPublishedBond(String urlBondPublished, Map<String, Bond> bonds) throws Exception {

        CSVParser parser = Apps.readAsCSVParser(urlBondPublished);
        LOGGER.debug("Got parser.");

        for (CSVRecord csvRecord : parser) {
            LOGGER.debug(csvRecord.toString());
            String bondId = csvRecord.get(2); // eg. 12581 or 49581E

            if (!bonds.containsKey(bondId)) {
                LOGGER.warn(String.format("Bond ID '%s' cannot be found from daily bonds.", bondId));

            } else {
                LOGGER.debug(String.format("Bond ID '%s' was found in daily bonds.", bondId));

                Bond idxBond = bonds.get(bondId);
                SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER);
                idxBond.setIssued(Apps.formatDate(csvRecord.get(6), formatter));
                idxBond.setDue(Apps.formatDate(csvRecord.get(7), formatter));

                final float roi = idxBond.getRoi();
                final float roiOY = idxBond.getRoiOverYear();

                System.out.printf("%s\t%s\t%s\t%s%n", bondId, idxBond.toLine(), roi, roiOY);

                if (bondId.equals("36626")) {
                    throw new Exception("hit 36626");
                }
            }


            System.exit(-1);
        }
    }










}
