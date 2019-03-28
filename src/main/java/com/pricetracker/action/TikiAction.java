package com.pricetracker.action;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pricetracker.core.Const;
import com.pricetracker.core.UserAgent;
import com.pricetracker.entities.Product;

public class TikiAction {
    private static final Logger logger = LoggerFactory.getLogger(TikiAction.class);

    public List<Product> productList(String url) throws Exception {
        List<Product> productList = new ArrayList<>();
        System.out.println(String.format("[FETCH][%s]", url));
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Elements itemElements = doc.select(".product-box-list .product-item");

        for (Element item : itemElements) {
            Product product = new Product();
            product.setId(StringUtils.trim(item.attr("data-id")));
            product.setTitle(StringUtils.trim(item.attr("data-title")));
            product.setBrand(StringUtils.trim(item.attr("data-brand")));
            product.setPrice(StringUtils.trim(item.attr("data-price")));
            product.setImage(StringUtils.trim(item.select("a[href] .product-image").attr("src")));
            product.setUrl(StringUtils.trim(item.select("a[href]").attr("href")));

            productList.add(product);
        }

        return productList;
    }

    public List<String> productPageList(String url, Integer page) throws Exception {
        List<String> pageList = new ArrayList<>();

        Document doc = Jsoup.connect(String.format(url))
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Elements itemElements = doc.select(".product-box-list .product-item");
        Elements itemResultCount = doc.select("h4[name='results-count']");
        int limitOnPage = itemElements.size();
        int totalResults = NumberUtils.toInt(StringUtils.removeAll(itemResultCount.text(), "[\\p{L}]+|\\s+"));
        int calcPage = (totalResults/limitOnPage) + 1;

        for (int i = 1; i <= (page != null ? page : calcPage); i++ ) {
            pageList.add(url.concat("&page=") + i);
            System.out.println(String.format("[PAGE][%s]", url.concat("&page=") + i));
        }

        return pageList;
    }

    public List<Product> start(String url) throws Exception {
        List<Product> productList = new ArrayList<>();

        List<String> productPageList = productPageList(url, 2);

        productPageList.forEach(page -> {
            try {
                productList.addAll(productList(page));
            } catch (Exception ex) {
                logger.error("Product List", ex);
            }
        });

        return productList;
    }

    public static void main(String[] args) throws Exception {
        new TikiAction().start("https://tiki.vn/dien-thoai-may-tinh-bang/c1789?order=newest");
    }
}
