package com.pricetracker.utils;

import com.pricetracker.entities.Product;

public class PrintUtils {
    public static void printProduct(Product product) {
        System.out.println(product.getId());
        System.out.println(product.getTitle());
        System.out.println(product.getBrand());
        System.out.println(product.getImage());
        System.out.println(product.getPrice());
        System.out.println(product.getUrl());
        System.out.println(product.getSourceSite());

        System.out.println("=============================================================");
    }
}
