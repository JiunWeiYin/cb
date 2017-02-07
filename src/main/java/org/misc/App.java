package org.misc;

import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.util.Apps;
import java.io.IOException;
import static org.misc.ConstVar.*;

public class App {
    public static void main(String[] args) throws IOException {
        Connection conn = Apps.getConnection(Apps.getConfiguration().getUrlBondDaily(), USER_AGENT, REFERRER, TIME_OUT);
        Connection.Response resp = conn.execute();

        if (resp.statusCode() == 200) {
            Document doc = conn.get();
            Elements tables = doc.select("table"); //select the first table.

            for (Element t : tables) {
                if (t.hasClass(YUI_TEXT_LEFT)) {
                    System.out.println(t);
                }

            }


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
