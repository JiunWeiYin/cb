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

        Configuration config = Apps.getConfiguration();
        LOGGER.debug(String.format("The configuration is ready."));

        Connection conn = Apps.getConnection(config.getUrlBondDaily(), USER_AGENT, REFERRER, TIME_OUT);
        LOGGER.debug(String.format("The connection is ready."));

        Connection.Response resp = conn.execute();
        LOGGER.debug(String.format("The connection has been tested."));

        if (resp.statusCode() == 200) {
            Document doc = conn.get();
            LOGGER.debug(String.format("The HTML has been converted as a Document object."));


            Elements tables = doc.select(TABLE); // get all tables
            if (tables.size() <= 0) {
                LOGGER.debug(String.format("<%s> was not found.", TABLE));
                return;
            }
            LOGGER.debug(String.format("Got all <%s>.", TABLE));


            Element table = Apps.searchTable(tables, CLASS, YUI_TEXT_LEFT);
            if (table == null) {
                LOGGER.debug(String.format("<%s %s=%s> was not found.", TABLE, CLASS, YUI_TEXT_LEFT));
                return;
            }
            LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, CLASS, YUI_TEXT_LEFT));


            table = Apps.searchTable(table.select(TABLE), BGCOLOR, BGCOLOR_VALUE);
            if (table == null) {
                LOGGER.debug(String.format("<%s %s=%s> was not found.", TABLE, BGCOLOR, BGCOLOR_VALUE));
                return;
            }
            LOGGER.debug(String.format("Got the <%s %s=%s>.", TABLE, BGCOLOR, BGCOLOR_VALUE));


            System.out.printf("table: %s%n", table);


//            Elements rows = tables.select("tr");
//
//            List<String> downServers = new ArrayList<>();
//            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
//                Element row = rows.get(i);
//                Elements cols = row.select("td");
//                if (cols.get(7).text().equals("down")) {
//                    downServers.add(cols.get(5).text());
//                }
//            }


        } else {
            System.out.printf("resp.statusCode(): %s %n", resp.statusCode());
        }

    }

}
