package com.gjasinski.awssarjava.utils;

import com.gjasinski.awssarjava.entity.SarFunctionMain;

import java.util.regex.Pattern;

public class FunctionUtils {
    private final static Pattern TOO_LONG_PATTERN_TO_CUT = Pattern.compile("https://github.com/.+/.+/.+");
    public static String getFunctionLocalPath(SarFunctionMain f){
        String path =  "/sar/data/" + f.getId() + "_" + f.getName();
        if (f.getHomePageUrl().contains("tree")){
            String subDirectory = f.getHomePageUrl().substring(f.getHomePageUrl().indexOf("tree/master") + 11);
            return path + "/" + subDirectory + "/";
        }
        return path;
    }

    public static String removeRepositorySpecificUrl(SarFunctionMain f){
        String homePageUrl = f.getHomePageUrl();
        if(homePageUrl.contains("tree")){
            homePageUrl = homePageUrl.substring(0, homePageUrl.indexOf("tree"));
        }
        if (homePageUrl.contains("blob")) {
            homePageUrl = homePageUrl.substring(0, homePageUrl.indexOf("blob"));
        }
        if (homePageUrl.contains("#")) {
            homePageUrl = homePageUrl.substring(0, homePageUrl.indexOf("#"));
        }
        if(!homePageUrl.startsWith("http")){
            homePageUrl = "https://" + homePageUrl;
        }

        if (TOO_LONG_PATTERN_TO_CUT.matcher(homePageUrl).matches()){
            homePageUrl = homePageUrl.substring(0, homePageUrl.lastIndexOf("/"));
        }
        return homePageUrl;
    }

}
