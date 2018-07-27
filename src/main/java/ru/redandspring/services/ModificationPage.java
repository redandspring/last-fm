package ru.redandspring.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.config.PeriodCharts;


import java.io.IOException;
import java.util.List;

public class ModificationPage {

    private static final Logger log = LoggerFactory.getLogger(ModificationPage.class);

    private final ParsingPageCharts parsingPage = new ParsingPageCharts();

    private static final int NEW_RANK = 99999;
    private static final String HTML_SPAN_NAME = "<span class=\"chartlist-ellipsis-wrap\">";

    public String resultPage(final PeriodCharts period) throws IOException, ServiceException {
        log.info("resultPage(): period={}", period);

        final List<String> charts = parsingPage.parseChartCache(period);
        final int sizeCharts = charts.size();
        final List<Element> chartsCurrent = parsingPage.parseChartCurrent();
        final StringBuilder htmlTr = new StringBuilder();
        for (int i = 0; i < chartsCurrent.size(); i++) {
            Element current = chartsCurrent.get(i);
            final String name = parsingPage.findName(current).get(0).text();
            final int rank = calcRank(charts, name, sizeCharts, i);
            htmlTr.append(innerRank(current, rank));
        }

        String page = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("artists.template.html"),"UTF-8");
        Document doc = Jsoup.parse(page);
        doc.select("#my-tr-list").empty().append(htmlTr.toString());
        return doc.html();
    }

    private int calcRank(final List<String> charts, final String name, final int size, final int last){
        for (int j = 0; j < size; j++) {
            if (name.equalsIgnoreCase(charts.get(j))){
                return j - last;
            }
        }
        return NEW_RANK;
    }

    private String innerRank(final Element element, final int rank){
        if (rank == 0) {
            return element.outerHtml();
        }
        final String style = (rank > 0) ? "success" : "danger";
        final String rankVal = (rank == NEW_RANK) ? "new" : String.valueOf(Math.abs(rank));
        final String pad = StringUtils.rightPad(rankVal, 3);
        return element.outerHtml().replace(HTML_SPAN_NAME, HTML_SPAN_NAME + "<span class='label label-my label-"+style+"'>"+pad+"</span>" );
    }
}
