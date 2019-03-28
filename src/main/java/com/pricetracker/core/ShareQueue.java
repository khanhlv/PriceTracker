package com.pricetracker.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;


public final class ShareQueue {
    public static ConcurrentLinkedDeque<String> shareQueue = new ConcurrentLinkedDeque<>();

    public static void addItem(List<String> linkList) {
        if (shareQueue.size() < Const.TOP_CHAPTER) {
            for (String link : linkList) {
                if (!shareQueue.contains(link)) {
                    shareQueue.add(link);
                }
            }
        }
    }

    public static List<String> getItem() {
        List<String> listItem = new ArrayList<>();

        int size = shareQueue.size() > Const.SHARE_QUEUE_SIZE ? Const.SHARE_QUEUE_SIZE : shareQueue.size();

        for (int i = 0; i < size; i++) {
            listItem.add(shareQueue.poll());
        }

        return listItem;
    }
}
