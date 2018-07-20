package com.yunche.loan.config.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author liuzhe
 * @date 2018/6/11
 */
public class MapSortUtils {

    /**
     * Map 按key进行排序
     *
     * @param originMap
     * @return
     */
    public static Map<String, String> sortMapByKey(Map<String, String> originMap) {

        if (CollectionUtils.isEmpty(originMap)) {
            return null;
        }

        Map<String, String> sortMap = new TreeMap<>(new MapKeyComparator());

        sortMap.putAll(originMap);

        return sortMap;
    }

    /**
     * Map 按value进行排序
     *
     * @param originMap
     * @return
     */
    public static Map<String, Long> sortMapByValue(Map<String, Long> originMap) {

        if (CollectionUtils.isEmpty(originMap)) {
            return null;
        }

        Map<String, Long> sortedMap = Maps.newLinkedHashMap();

        // entry放到List
        List<Map.Entry<String, Long>> entryList = Lists.newArrayList(originMap.entrySet());

        // 排序
        Collections.sort(entryList, new MapValueComparator());

        Iterator<Map.Entry<String, Long>> it = entryList.iterator();
        Map.Entry<String, Long> tmpEntry = null;
        while (it.hasNext()) {
            tmpEntry = it.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }

        return sortedMap;
    }

    /**
     * 根据key排序
     */
    static class MapKeyComparator implements Comparator<String> {

        @Override
        public int compare(String str1, String str2) {

            return str1.compareTo(str2);
        }
    }

    /**
     * 根据val排序
     */
    static class MapValueComparator implements Comparator<Map.Entry<String, Long>> {

        @Override
        public int compare(Map.Entry<String, Long> entry1, Map.Entry<String, Long> entry2) {

            return entry1.getValue().compareTo(entry2.getValue());
        }
    }

}
