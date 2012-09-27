package com.example.autotest;

import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;


public class InvokeMenuBuilder {
    
    public static View getMenuView(Menu menu, int id, ViewGroup vg){
        return (View) Invoker.invoke(menu, "getMenuView", new Class[]{int.class, ViewGroup.class} , new Object[]{id, vg});
    }
}
