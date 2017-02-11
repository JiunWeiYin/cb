package org.misc;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.util.Apps;

import java.io.IOException;

import static org.misc.ConstVar.*;

public class App {
    private static final Logger LOGGER = Logger.getLogger(App.class);

    public static void main(String[] args) throws IOException {

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
//        for (int i = idxRecord; i < tr.size(); i++) {
//            System.out.printf("%s%n", tr.get(i));
//        }

        System.out.printf("%s%n%n%n%n", tr.get(1).select(TD));

        Elements td = tr.get(FEATURE.valueOf(BOND).ordinal()).select(TD);

        String[] bond = Apps.getValueAsString(td, BOND).split(SEPERATOR);
        int bondId = Integer.parseInt(bond[0]); // eg. 12581
        String bondName = bond[1]; // eg. 其祥一KY

        String time = Apps.getValueAsString(td, TIME); // eg. 10:12
        float closingPrice = Apps.getValueAsFloat(td, CLOSING_PRICE); // eg. 108.5
        float bidPrice = Apps.getValueAsFloat(td, BID_PRICE); // eg. 107.6
        float offerPrice = Apps.getValueAsFloat(td, OFFER_PRICE); // eg. 108.5
        float dailyPricing = Apps.getValueAsFloat(td, DAILY_PRICING); // eg. 0.0
        float boardLot = Apps.getValueAsFloat(td, BOARD_LOT); // eg. 0.0
        float ydayClosingPrice = Apps.getValueAsFloat(td, YDAY_CLOSING_PRICE); // eg. 108.5
        float openingPrice = Apps.getValueAsFloat(td, OPENING_PRICE); // eg. 0.0
        float dayHigh = Apps.getValueAsFloat(td, DAY_HIGH); // eg. 108.5
        float dayLow = Apps.getValueAsFloat(td, DAY_LOW); // eg. 108.5

        System.out.printf("bondId\t%s%n", bondId);
        System.out.printf("bondName\t%s%n", bondName);
        System.out.printf("time\t%s%n", time);
        System.out.printf("closingPrice\t%s%n", closingPrice);
        System.out.printf("bidPrice\t%s%n", bidPrice);
        System.out.printf("offerPrice\t%s%n", offerPrice);
        System.out.printf("dailyPricing\t%s%n", dailyPricing);
        System.out.printf("boardLot\t%s%n", boardLot);
        System.out.printf("ydayClosingPrice\t%s%n", ydayClosingPrice);
        System.out.printf("openingPrice\t%s%n", openingPrice);
        System.out.printf("dayHigh\t%s%n", dayHigh);
        System.out.printf("dayLow\t%s%n", dayLow);


        //









    }


}
