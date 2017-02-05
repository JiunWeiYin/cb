package org.misc;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.misc.util.Apps;

import static org.misc.ConstVar.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws IOException {

        Connection con = Jsoup.connect(Apps.getConfiguration().getUrlBondDaily())
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com")
                .timeout(12000);
        Connection.Response resp = con.execute();

        Document doc = null;
        if (resp.statusCode() == 200) {
            doc = con.get();
            Elements table = doc.select("table"); //select the first table.

            System.out.printf("table.text(): %s %n", table.text());

            Elements rows = table.select("tr");

            List<String> downServers = new ArrayList<>();
            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");

//                if (cols.get(7).text().equals("down")) {
//                    downServers.add(cols.get(5).text());
//                }


            }




        } else {
            System.out.printf("resp.statusCode(): %s %n", resp.statusCode());
        }



    }

}
