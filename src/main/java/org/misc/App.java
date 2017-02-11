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

    ////////
    public static void main(String[] args) throws IOException {

        // set configuration
        Configuration config = Apps.getConfiguration();
        LOGGER.info("The configuration is ready.");

        // setup connection
        String urlBondDaily = config.getUrlBondDaily();
        Connection conn = Apps.getConnection(urlBondDaily, USER_AGENT, REFERRER, TIME_OUT);
        LOGGER.info(String.format("Connected to %s.", urlBondDaily));

        // execute connection
        Connection.Response resp = conn.execute();
        LOGGER.info("The connection has been tested.");

        // get connection response status code
        if (resp.statusCode() != 200) {
            LOGGER.error(String.format("The connection response status code is %s. " +
                    "Please check if the internet is working.", resp.statusCode()));
            return;
        }
        LOGGER.info(String.format("The connection response status code is %s. %n", resp.statusCode()));

        // convert HTML to doc
        Document doc = conn.get();
        LOGGER.info("The HTML has been converted as a Document object.");

        // select all <table>
        Elements tables = doc.select(TABLE);
        if (tables.size() <= 0) {
            LOGGER.error(String.format("<%s> was not found.", TABLE));
            return;
        }
        LOGGER.info(String.format("Got all <%s>.", TABLE));

        // get a specific <table>
        Element table = Apps.searchTable(tables, CLASS, YUI_TEXT_LEFT);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, CLASS, YUI_TEXT_LEFT));
            return;
        }
        LOGGER.info(String.format("Got the <%s %s=%s>.", TABLE, CLASS, YUI_TEXT_LEFT));

        // get a specific <table>
        table = Apps.searchTable(table.select(TABLE), BGCOLOR, BGCOLOR_VALUE);
        if (table == null) {
            LOGGER.error(String.format("<%s %s=%s> was not found.", TABLE, BGCOLOR, BGCOLOR_VALUE));
            return;
        }
        LOGGER.info(String.format("Got the <%s %s=%s>.", TABLE, BGCOLOR, BGCOLOR_VALUE));

        // get all <tr>
        Elements tr = table.select(TR);
        if (tr.isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TR, urlBondDaily));
            return;
        }
        LOGGER.info(String.format("Got the <%s>.", TR));

        // check if <td> is present
        Elements td = tr.select(TD);
        if (td.isEmpty()) {
            LOGGER.error(String.format("<%s> was not found. Please check the HTML structure in '%s'.", TD, urlBondDaily));
            return;
        }
        LOGGER.info(String.format("Got the <%s>.", TD));

        System.out.printf("%s%n", tr.get(0));

        for (int i = tr.indexOf(td); i < tr.size(); i++) {
            System.out.printf("%s%n", i);
            System.out.printf("%s%n", tr.get(i));
        }




    }

}
