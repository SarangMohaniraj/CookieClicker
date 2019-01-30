package com.example.sarang.cookieclicker;

public class Grandma implements Product{

    static double cookiesPerSecond = 1;
    static int image = R.drawable.grandma;
    static int icon = R.drawable.grandma_icon;
    public static int background = R.drawable.grandma_background_repeat;
    static String name = "Grandma";
    static int basePrice = 100;
    public static int amount = 0;



    @Override
    public double getCookiesPerSecond() {
        return cookiesPerSecond;
    }

    public void setCookiesPerSecond(int cookiesPerSecond){
        this.cookiesPerSecond = cookiesPerSecond;
    }

    public static synchronized double generate(){
//        Cookie.cookiesInBank += (cookiesPerSecond*amount);
//        return cookiesPerSecond*amount;
        Cookie.cookiesInBank++;
        return 1;
    }


    @Override
    public int getImage() {
        return image;
    }

    @Override
    public int getIcon() {
        return icon;
    }

    @Override
    public String getName() {
        return name;

    }

    @Override
    public int getPrice() {
        return (int)Math.ceil(basePrice*Math.pow(1.15,amount)); //basePrice*1.15^number of products owned
    }

    @Override
    public int add() {
        ++amount;
        ++Cookie.amount;
        Cookie.cookiesPerSecond += cookiesPerSecond;
        return amount;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public int getBackground() {
        return background;
    }
}
