package com.wfzcx.ieos.data.bean;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Copyright (C) 2016
 * All right reserved.
 *
 * @author:赵小布
 * @email: zhaocz2015@163.com
 * @date: 2016-08-23
 */
public class ResultMap extends LinkedHashMap {

    public int getIntVal(String i) {
        return get(i) == null ? -1 : Integer.valueOf(String.valueOf(get(i)));
    }

    public boolean getBoolean(String b) {
        return get(b) == null ? false : Boolean.valueOf(String.valueOf(get(b)));
    }

    public String getString(String s) {
        return get(s) == null ? "" : String.valueOf(get(s));
    }

    public ArrayList getRsList(String rs) {
        return get(rs) == null ? new ArrayList<>() : (ArrayList) get(rs);
    }

    public LinkedTreeMap getRsMap(String rs) {
        return get(rs) == null ? new LinkedTreeMap() : (LinkedTreeMap) get(rs);
    }

}
