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
import com.pricetracker.enums.SourceSite;
import com.pricetracker.utils.PrintUtils;

public class AdayroiAction {
    private static final Logger logger = LoggerFactory.getLogger(AdayroiAction.class);

    // https://www.adayroi.com/dam-c35?q=:new&sort=new&page=1

    public List<Product> productList(String url) throws Exception {
        List<Product> productList = new ArrayList<>();
        System.out.println(String.format("[FETCH][%s]", url));
        Document doc = Jsoup.connect(url)
                .userAgent(UserAgent.getUserAgent())
                .timeout(Const.TIMEOUT)
                .get();

        Elements itemElements = doc.select(".product-list__container .product-item");

        for (Element item : itemElements) {
            Product product = new Product();
            product.setId(StringUtils.split(item.select(".product-item__couple-btn button.d-button--watch-fast").attr("data-offer-code"), "_")[0]);
            product.setTitle(StringUtils.trim(item.select("a[href].product-item__info-title").text()));
            product.setBrand(StringUtils.trim(item.select(".product-item__info .product-item__info-brand").text()));
            product.setPrice(StringUtils.trim(item.select(".product-item__info-price .product-item__info-price-sale").text()));
            product.setImage(StringUtils.trim(item.select("a[href].product-item__thumbnail img.default").attr("data-src")));
            product.setUrl(StringUtils.trim(item.select("a[href].product-item__info-title").attr("href")));
            product.setSourceSite(SourceSite.ADAYROI);
            productList.add(product);

            PrintUtils.printProduct(product);
        }

        return productList;
    }

    public List<Product> start(String url, Integer page) throws Exception {
        List<Product> productList = new ArrayList<>();

        for (int i = 1; i <= page; i++ ) {
            System.out.println(String.format("[PAGE][%s]", url.concat("&page=") + i));
            List<Product> products = productList(url.concat("&page=") + i);

            if (products.size() == 0) {
                break;
            }

            productList.addAll(productList(url.concat("&page=") + i));
        }

        return productList;
    }

    public static void main(String[] args) throws Exception {
        new AdayroiAction().start("https://www.adayroi.com/dam-c35?q=:new&sort=new", 1);
    }
}
