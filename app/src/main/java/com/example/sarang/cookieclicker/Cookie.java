package com.example.sarang.cookieclicker;


public class Cookie {
    public static double cookiesInBank = 0;
    public static double cookiesPerSecond = 0;
    public static int cookiesPerClick = 1;
    public static int amount = 0;
    public static int image = R.drawable.cookie;
    public static int imageSmall = R.drawable.cookie_small;


    public static int getCookiesPerClick() {
        return cookiesPerClick;
    }


    public static synchronized void click(){
        cookiesInBank += cookiesPerClick;
    }

    public static synchronized double generate(){
        cookiesInBank++;
        return 1;
    }



}
