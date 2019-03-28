package com.pricetracker.action;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.pricetracker.core.UserAgent;
import com.pricetracker.entities.Product;
import com.pricetracker.entities.shopee.Categories;
import com.pricetracker.entities.shopee.Item;
import com.pricetracker.entities.shopee.ItemObject;

public class ShopeeAction {
    private static final Logger logger = LoggerFactory.getLogger(ShopeeAction.class);

    // https://shopee.vn/api/v1/category_list/
    // https://shopee.vn/api/v2/item/get?itemid=1719586677&shopid=13480277
    // https://shopee.vn/api/v2/search_items/?by=ctime&limit=50&match_id=2349&newest=0&order=desc&page_type=search

    public void start(String cateId, Integer pageTotal) throws Exception {
        for (int i = 0; i < pageTotal; i++) {
            productList(cateId, i * 50);
        }
    }

    public List<Product> productList(String cateId, Integer page) throws Exception {

        List<Product> productList = new ArrayList<>();
        JsonReader jsonReader = executeGet(String.format("https://shopee.vn/api/v2/search_items/?by=ctime&limit=50&match_id=%s&newest=%s&order=desc&page_type=search", cateId, page));

        Categories categoryList = new Gson().fromJson(jsonReader, Categories.class);

        for (Item item : categoryList.items) {
            productList.add(productDetail(item.itemid, item.shopid));
        }

        return productList;
    }

    public Product productDetail(String itemId, String shopId) throws Exception {

        JsonReader jsonReader = executeGet(String.format("https://shopee.vn/api/v2/item/get?itemid=%s&shopid=%s", itemId, shopId));

        ItemObject itemObject = new Gson().fromJson(jsonReader, ItemObject.class);

        Product product = new Product();
        product.setBrand(itemObject.item.brand);
        product.setId(itemObject.item.itemid);
        product.setImage(itemObject.item.image);
        product.setPrice(itemObject.item.price);
        product.setTitle(itemObject.item.name);

        System.out.println(itemObject.item.name);

        return product;
    }

    public JsonReader executeGet(String url) throws Exception {
        final HttpClient client = HttpClientBuilder.create().build();
        final HttpGet request = new HttpGet(url);
        request.addHeader(HttpHeaders.USER_AGENT, UserAgent.getUserAgent());
        request.addHeader(HttpHeaders.ACCEPT, "application/json;charset=utf-8");

        HttpResponse response = client.execute(request);

        InputStream inputStream = response.getEntity().getContent();
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));

        return jsonReader;
    }

    public static void main(String[] args) throws Exception {
//        new ShopeeAction().productDetail("1719586677","13480277");
        new ShopeeAction().start("2349", 2);
    }
}
