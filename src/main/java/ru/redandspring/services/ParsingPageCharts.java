package ru.redandspring.services;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.redandspring.config.Artist;
import ru.redandspring.config.Charts;
import ru.redandspring.config.PeriodCharts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class ParsingPageCharts {

    private static final Logger log = LoggerFactory.getLogger(ParsingPageCharts.class);

    private static final String URL_PAGE_1 = "https://www.last.fm/ru/user/taad2/library/artists?from=2014-01-28&to=%s";
    private static final String URL_PAGE_2 = "https://www.last.fm/ru/user/taad2/library/artists?from=2014-01-28&to=%s&page=2";
    private static final String URL_PAGE_CURRENT_1 = "https://www.last.fm/ru/user/taad2/library/artists";
    private static final String URL_PAGE_CURRENT_2 = "https://www.last.fm/ru/user/taad2/library/artists?page=2";
    private static final SimpleCache<Charts> CACHE = SimpleCache.getInstance("CHARTS");

    List<String> parseChartCache(final PeriodCharts period) throws ServiceException {
        final Charts charts = CACHE.get(period.getDate(), () -> parseChart(period));
        return charts.getValues();
    }

    private Charts parseChart(final PeriodCharts period) throws ServiceException {
        log.info("parseChart(): period={}", period);
        try {
            final List<String> result = new ArrayList<>();
            fillCharts(String.format(URL_PAGE_1, period.getDate()), result);
            fillCharts(String.format(URL_PAGE_2, period.getDate()), result);
            return new Charts(result);
        } catch (IOException e) {
            throw new ServiceException("parseChart(): get url is error", e);
        }
    }

    List<Element> parseChartCurrent() throws ServiceException {
        log.info("parseChartCurrent():");
        try {
            final List<Element> result = new ArrayList<>();
            fillChartsCurrent(URL_PAGE_CURRENT_1, result);
            fillChartsCurrent(URL_PAGE_CURRENT_2, result);
            return result;
        } catch (IOException e) {
            throw new ServiceException("parseChartCurrent(): get url is error", e);
        }
    }

    List<Artist> convertElementsChartToArtists(final List<Element> elements) {
        final List<Artist> result = new ArrayList<>();
        elements.forEach(element -> result.add(createArtist(element)));
        return result;
    }

    private Elements findName(final Element element){
        return element.select("td.chartlist-name a.link-block-target");
    }

    private Artist createArtist(final Element el) {
        String position = findTextChartlistIndex(el);
        String countListen = findCountListen(el);
        String avatar = el.select("td.chartlist-image span.avatar img").get(0).attr("src");
        String stylePercent = el.select("td.chartlist-bar span.chartlist-count-bar-slug").get(0).attr("style");
        String artistLink = el.select("td.chartlist-bar a").get(0).attr("href");
        return new Artist(
                position,
                findName(el).get(0).text(),
                countListen,
                avatar,
                stylePercent,
                artistLink);
    }

    private String findTextChartlistIndex(final Element element){
        return StringUtils.trimToEmpty(element.select("td.chartlist-index").get(0).text());
    }

    private String findHtml(final Element element, final String query){
        return StringUtils.trimToEmpty(element.select(query).get(0).html());
    }

    private String findCountListen(final Element element) {
        String query = "span.chartlist-count-bar-value";
        return StringUtils.substringBefore(findHtml(element, query), " ");
    }

    private void fillCharts(final String url, final List<String> result) throws IOException {
        log.info("getCharts(): url={}", url);

        final Document doc  = Jsoup.connect(url).get();
        final Elements listName = findName(doc);
        for(Element name: listName){
            result.add(name.text());
        }
    }

    private void fillChartsCurrent(final String url, final List<Element> result) throws IOException {
        log.info("getCharts(): url={}", url);

        Document doc  = Jsoup.connect(url).get();
        final Elements listName = doc.select("tr.js-link-block");
        result.addAll(listName);
    }


}
