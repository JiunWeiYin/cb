/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.model.Bond;
import org.misc.model.Configuration;
import org.misc.util.Apps;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.misc.constant.ConstVar.*;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String... args) throws Exception {

        Map<String, Bond> bonds = new HashMap<>();

        // set configuration
        LOGGER.info("Loading the configuration file.");
        Configuration config = Apps.getConfiguration();
        if (config != null) {
            LOGGER.info(String.format("Loaded the configuration: %s.", config));
        }

        // setup connection
        String urlBondDaily = config.getUrlBondDaily();
        if (urlBondDaily != null) {
            LOGGER.info(String.format("Connecting to %s.", urlBondDaily));
        }
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
            return;
        }
        LOGGER.debug(String.format("The connection response status code is %s.", resp.statusCode()));

        // convert HTML to doc
        Document doc = conn.get();
        LOGGER.debug("The HTML has been converted as a Document object.");

        // select all <table>
        Elements tables = doc.select(TABLE);
        if (tables.size() <= 0) {
            LOGGER.error(String.format("<%s> was not found.", TABLE));
            return;
        }
        LOGGER.debug(String.format("Got all <%s>.", TABLE));

        // get a specific <table>
        Element table = Apps.searchTable(tables, CLASS, YUI_TEXT_LEFT);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, CLASS, YUI_TEXT_LEFT));
            return;
        }
        LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, CLASS, YUI_TEXT_LEFT));

        // get a specific <table>
        table = Apps.searchTable(table.select(TABLE), BGCOLOR, BGCOLOR_VALUE);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, BGCOLOR, BGCOLOR_VALUE));
            return;
        }
        LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, BGCOLOR, BGCOLOR_VALUE));

        // get all <tr>
        Elements tr = table.select(TR);
        if (tr.isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TR, urlBondDaily));
            return;
        }
        LOGGER.debug(String.format("Got the <%s>.", TR));

        // check if <td> is present
        if (tr.select(TD).isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TD, urlBondDaily));
            return;
        }
        LOGGER.debug(String.format("Got the <%s>.", TD));

        // get the first index of <td>
        int idxRecord = Apps.indexOfRecord(tr, TD);
        LOGGER.debug(String.format("The first index of <%s> is %s.", TD, idxRecord));
        for (int i = idxRecord; i < tr.size(); i++) {
            Elements td = tr.get(i).select(TD);

            String[] bond = Apps.getValueAsString(td, BOND).split(SEPERATOR_SPACE);
            String bondId = bond[0]; // eg. 12581 or 49581E

            Bond dBond = new Bond();
            dBond.setBondName(bond[1]); // eg. 其祥一KY
            dBond.setTime(Apps.getValueAsString(td, TIME)); // eg. 10:12
            dBond.setClosingPrice(Apps.getValueAsFloat(td, CLOSING_PRICE)); // eg. 108.5
            dBond.setBidPrice(Apps.getValueAsFloat(td, BID_PRICE)); // eg. 107.6
            dBond.setOfferPrice(Apps.getValueAsFloat(td, OFFER_PRICE)); // eg. 108.5
            dBond.setDailyPricing(Apps.getValueAsString(td, DAILY_PRICING)); // eg. △1.30
            dBond.setBoardLot(Apps.getValueAsInt(td, BOARD_LOT)); // eg. 0.0
            dBond.setYdayClosingPrice(Apps.getValueAsFloat(td, YDAY_CLOSING_PRICE)); // eg. 108.5
            dBond.setOpeningPrice(Apps.getValueAsFloat(td, OPENING_PRICE)); // eg. 0.0
            dBond.setDayHigh(Apps.getValueAsFloat(td, DAY_HIGH)); // eg. 108.5
            dBond.setDayLow(Apps.getValueAsFloat(td, DAY_LOW)); // eg. 108.5
            dBond.setPresent(new Date()); // eg. 2017/02/28

            bonds.put(bondId, dBond);
        }

        BufferedReader br = Apps.readFileAsBufferedReader(config.geturlBondPublish());
        String line = br.readLine();
        LOGGER.debug(line);
        System.out.println("Bond_Id\tBond_Name\tClosing_Price\tPresent_Date\tDue_Date\tROI\tROI_Year");

        while ((line = br.readLine().replace("\"", "").replace(" ", "")) != null) {
            LOGGER.debug(line);
            String[] lineSplit = line.split(SEPERATOR_COMMA);
            String bondId = lineSplit[2]; // eg. 12581 or 49581E

            if (!bonds.containsKey(bondId)) {
                LOGGER.warn(String.format("Bond ID '%s' cannot be found from daily bonds.", bondId));
            } else {
                LOGGER.debug(String.format("Bond ID '%s' was found in daily bonds.", bondId));
                LOGGER.debug(line);

                Bond idxBond = bonds.get(bondId);
                SimpleDateFormat formatter = new SimpleDateFormat(FORMATTER);
                idxBond.setIssued(Apps.formatDate(lineSplit[6], formatter));
                idxBond.setDue(Apps.formatDate(lineSplit[7], formatter));

                final float roi = idxBond.getRoi();
                final float roiOY = idxBond.getRoiOverYear();

                System.out.printf("%s\t%s\t%s\t%s%n", bondId, idxBond.toLine(), roi, roiOY);

                if (bondId.equals("36626")) {
                    throw new Exception("hit 36626");
                }
            }


        }


        LOGGER.info("This program was running successfully.");
    }


}
