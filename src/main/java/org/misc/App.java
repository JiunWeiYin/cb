/**
 * Author: Chun-Pei Cheng
 * Contact: ccp0625@gmail.com
 */

package org.misc;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.util.Apps;
import org.misc.util.Bond;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.misc.ConstVar.*;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException, URISyntaxException {

        Map<String, Bond> bonds = new HashMap<>();

        // set configuration
        Configuration config = Apps.getConfiguration();
        LOGGER.debug("The configuration is ready.");

        // setup connection
        String urlBondDaily = config.getUrlBondDaily();
        Connection conn = Apps.getConnection(urlBondDaily, USER_AGENT, REFERRER, TIME_OUT);
        LOGGER.info(String.format("Connected to %s.", urlBondDaily));

        // execute connection
        Connection.Response resp = conn.execute();
        LOGGER.info("The connection has been established.");

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

            bonds.put(bondId, dBond);
        }

        BufferedReader br = Apps.readFileAsBufferedReader(config.geturlBondPublish());
        String line = br.readLine();

        while ((line = br.readLine().replace("\"", "").replace(" ", "")) != null) {
            String[] lineSplit = line.split(SEPERATOR_COMMA);
            String bondId = lineSplit[2]; // eg. 12581 or 49581E

            if (!bonds.containsKey(bondId)) {
                LOGGER.warn(String.format("Bond ID '%s' cannot be found from daily bonds.", bondId));
            } else {
                LOGGER.debug(String.format("Bond ID '%s' was found in daily bonds.", bondId));

                Bond idxBond = bonds.get(bondId);
                idxBond.setRefund(REFUND); // eg. 100
                final float roi = idxBond.getROI();
                System.out.printf("ROI\t%s%n", roi);
            }


        }


        LOGGER.info("This program was running successfully.");
    }


}
