package com.example.swip.entity.enumtype;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tendency {
    //Active: 활발한 대화와 동기부여 원해요
    //Feedback: 피드백을 주고 받고 싶어요
    //Focus: 조용히 집중하고 싶어요
    public enum Element {
        Active, Feedback, Focus
    }
    private static final Map<String,Element> map;
    static {
        map = new HashMap<>();
        map.put("active", Element.Active);
        map.put("feedback", Element.Feedback);
        map.put("focus", Element.Focus);
    }
    public static Element toTendency(String tendency){
        return map.get(tendency);
    }
    public static String toString(Element tendency){
        String result = null;
        for (String key : map.keySet()) {
            if(tendency.equals(map.get(key)))
                result = key;
        }
        return result;
    }

    public static List<String> longToString (Long tendency) {
        List<String> tendencyList = new ArrayList<>();
        for (Element type : Element.values()) {
            if(tendency == 0)
                break;
            if(tendency % 2 == 1)
                tendencyList.add(toString(type));
            tendency = tendency >> 1;
        }
        return tendencyList;
    }
    public static Long stringToLong (List<String> tendencyList) {  //2^0: Active, 2^1: Focus, 2^2: Feedback
        Long tendency = 0L;
        for (String value : tendencyList) {
            Long addValue = 1L;
            for (Element type : Element.values()) {
                if (toTendency(value).equals(type)) {
                    tendency += addValue;
                    break;
                }
                addValue = addValue << 1;
            }
        }
        return tendency;
    }
}