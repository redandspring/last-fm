package ru.redandspring.services;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.config.Artist;
import ru.redandspring.config.PeriodCharts;


import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

public class ModificationPage {

    private static final Logger log = LoggerFactory.getLogger(ModificationPage.class);

    private final ParsingPageCharts parsingPage = new ParsingPageCharts();

    private static final int NEW_RANK = 99999;
    private static final String HTML_SPAN_NAME = "<span class=\"chartlist-ellipsis-wrap\">";
    private static final String ROW_TEMPLATE;
    private static final String ARTISTS_TEMPLATE;
    private static final String ICO_DOWN = "data:image/gif;base64,R0lGODlhAQABAAAAACH5BAEKAAEALAAAAAABAAEAAAICTAEAOw==";

    static {
        ROW_TEMPLATE = getTemplate("row.template.html");
        ARTISTS_TEMPLATE = getTemplate("artists.template.html");
    }

    public String resultPage(final PeriodCharts period) throws ServiceException {
        log.info("resultPage(): period={}", period);

        final List<String> charts = parsingPage.parseChartCache(period);
        final int sizeCharts = charts.size();
        final List<Element> chartsCurrent = parsingPage.parseChartCurrent();
        final List<Artist> artistsCurrent = parsingPage.convertElementsChartToArtists(chartsCurrent);

        final StringBuilder htmlTr2 = new StringBuilder();
        for (final Artist art: artistsCurrent) {
            final int rank = calcRank(charts, art.getArtistName(), sizeCharts, art.getPosition() - 1);
            final String row = ROW_TEMPLATE
                    .replace("%position%", String.valueOf(art.getPosition()))
                    .replace("%artistName%", art.getArtistName())
                    .replace("%countListen%", art.getCountListen())
                    .replace("%avatar%", art.getAvatar())
                    .replace("%stylePercent%", art.getStylePercent())
                    .replace("%artistLink%", art.getArtistLink());
            htmlTr2.append(innerRank(row, rank));
        }

        // Issue #1. Show artists, who leave the top
        for (String old: charts) {
            if (artistsCurrent.stream().noneMatch(a -> StringUtils.equalsIgnoreCase(a.getArtistName(), old))){
                final String row = ROW_TEMPLATE
                        .replace("%position%", "-")
                        .replace("%artistName%", old)
                        .replace("%countListen%", "-")
                        .replace("%avatar%", ICO_DOWN)
                        .replace("%stylePercent%", "width: 0%")
                        .replace("%artistLink%", "#")
                        .replace(HTML_SPAN_NAME, HTML_SPAN_NAME + "<span class='label label-my label-danger'>-</span>" );
                htmlTr2.append(row);
            }
        }

        Document doc = Jsoup.parse(ARTISTS_TEMPLATE);
        doc.select("#my-tr-list").empty().append(htmlTr2.toString());
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

    private String innerRank(final String html, final int rank){
        if (rank == 0) {
            return html;
        }
        final String style = (rank > 0) ? "success" : "danger";
        final String rankVal = (rank == NEW_RANK) ? "new" : String.valueOf(Math.abs(rank));
        final String pad = StringUtils.rightPad(rankVal, 3);
        return html.replace(HTML_SPAN_NAME, HTML_SPAN_NAME + "<span class='label label-my label-"+style+"'>"+pad+"</span>" );
    }

    private static String getTemplate(final String fileName) {
        try (final InputStream is = ModificationPage.class.getClassLoader().getResourceAsStream(fileName)){
            Objects.requireNonNull(is);
            return IOUtils.toString(is, "UTF-8");
        } catch (IOException ignored) {}
        return "";
    }
}
