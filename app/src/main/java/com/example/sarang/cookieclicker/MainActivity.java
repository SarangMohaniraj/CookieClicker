package com.example.sarang.cookieclicker;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    ImageView cookieView;
    public static TextView cookiesInBankView;
    public static TextView cookiesPerSecondView;
    public static ScaleAnimation animation;
    ImageView shineClockwise;
    ImageView shineCounterclockwise;
    ActionBar toolbar;
    Fragment removeFragment;
    public static List<Product> products;
    ConstraintLayout layout;
    volatile boolean stopThreads = false;
    public static ShopAdapter shopAdapter;
    public static PurchasedAdapter purchasedAdapter;
    static SharedPreferences prefs;
    static SharedPreferences.Editor editor;
    ImageView reset;
    //int cursors = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_cookie:
                    removeFragment = getSupportFragmentManager().findFragmentByTag("Fragment");
                    if(removeFragment != null)
                        getSupportFragmentManager().beginTransaction().remove(removeFragment).commit();
                return true;
                case R.id.navigation_shop:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, 0).replace(R.id.id_popup,new Shop(),"Fragment").commit();
                    return true;
                case R.id.navigation_purchased:
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_bottom, 0).replace(R.id.id_popup,new Purchased(),"Fragment").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setItemIconTintList(null);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        cookiesInBankView = findViewById(R.id.id_cookiesInBank);
        cookiesPerSecondView = findViewById(R.id.id_cookiesPerSecond);

        products = new ArrayList<>();
        products.add(new Cursor());
        products.add(new Grandma());
        products.add(new Farm());


        shopAdapter = new ShopAdapter(this,R.layout.fragment_shop, products);
        purchasedAdapter = new PurchasedAdapter(this,R.layout.fragment_purchased, products);


        cookieView = findViewById(R.id.id_cookie);
        animation = new ScaleAnimation(0.9f,1.0f,0.9f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(600);
        animation.setInterpolator(new BounceInterpolator());
        cookieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(animation);
                Cookie.click();
                updateCookiesInBankView();
                plus();
                fall(Cookie.getCookiesPerClick());
            }
        });

        layout = findViewById(R.id.id_layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFragment = getSupportFragmentManager().findFragmentByTag("Fragment");
                if(removeFragment != null)
                    getSupportFragmentManager().beginTransaction().remove(removeFragment).commit();
            }
        });

        shineClockwise = findViewById(R.id.id_shine_clockwise);
        shineCounterclockwise = findViewById(R.id.id_shine_counterclockwise);

        Animation clockwise = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);
        clockwise.setInterpolator(new LinearInterpolator());
        clockwise.setDuration(20000);
        clockwise.setRepeatCount(Animation.INFINITE);
        shineClockwise.startAnimation(clockwise);

        Animation counterClockwise = new RotateAnimation(360,0,Animation.RELATIVE_TO_SELF,.5f,Animation.RELATIVE_TO_SELF,.5f);
        counterClockwise.setInterpolator(new LinearInterpolator());
        counterClockwise.setDuration(20000);
        counterClockwise.setRepeatCount(Animation.INFINITE);
        shineCounterclockwise.startAnimation(counterClockwise);

        reset = findViewById(R.id.id_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset.startAnimation(animation);

                new AlertDialog.Builder(MainActivity.this)
                .setTitle("Are you sure you want to reset?")
                        .setMessage("You will not be able to get back your progress.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.reset_dark)
                        .show();
            }
        });

        new GrandmaThread().start();
        new FarmThread().start();
        new CursorThread().start();
        //new CookieThread().start();






    }

    public static String beautify(double num){
        if(num == (int)num)
            return new DecimalFormat("#,###").format(num);
        else if(num<1 && num != 0){
            return new DecimalFormat("0.#").format(num);
        }
        else
            return new DecimalFormat("#,###.0").format(num);
    }


    public static synchronized void updateCookiesInBankView(){
        if (Cookie.cookiesInBank != 1)
            cookiesInBankView.setText(beautify(Cookie.cookiesInBank) + " cookies");
        else
            cookiesInBankView.setText(beautify(Cookie.cookiesInBank) + " cookie");

        if(shopAdapter != null) //dim products in shop
            shopAdapter.notifyDataSetChanged();
    }
    public static synchronized void updateCookiesPerSecondView(){
        cookiesPerSecondView.setText(beautify(Cookie.cookiesPerSecond)+" per second");
    }
    public static synchronized void update(){
        updateCookiesInBankView();
        updateCookiesPerSecondView();
    }

    public void plus() {
        final TextView plus = new TextView(MainActivity.this);
        plus.setId(View.generateViewId());
        plus.setText("+" + Cookie.cookiesPerClick);
        plus.setTextColor(Color.WHITE);
        plus.setTypeface(ResourcesCompat.getFont(this, R.font.merriweather));
        plus.setTextSize(28);
        plus.setElevation(50f);
        plus.bringToFront();
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        plus.setLayoutParams(params);
        layout.addView(plus);

        ConstraintSet constraintSet = new ConstraintSet(); //used to save constraints from current layout
        constraintSet.clone(layout);

        constraintSet.connect(plus.getId(),ConstraintSet.TOP, cookieView.getId(), ConstraintSet.TOP);
        constraintSet.connect(plus.getId(),ConstraintSet.BOTTOM, cookieView.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(plus.getId(),ConstraintSet.LEFT, cookieView.getId(), ConstraintSet.LEFT);
        constraintSet.connect(plus.getId(),ConstraintSet.RIGHT, cookieView.getId(), ConstraintSet.RIGHT);

        float verticalBias = (float)(Math.random()*.15f);
        float horizontalBias = (float)(Math.random());
        constraintSet.setVerticalBias(plus.getId(),verticalBias);
        constraintSet.setHorizontalBias(plus.getId(),horizontalBias);

        constraintSet.applyTo(layout);

        Animation plusAnimation = new TranslateAnimation(0,0,100,0);
        final long duration = 1000;
        plusAnimation.setDuration(duration);
        plusAnimation.setInterpolator(new AccelerateInterpolator());
        plus.startAnimation(plusAnimation);
        plusAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ObjectAnimator fade = ObjectAnimator.ofFloat(plus, "Alpha", 1f, 0);
                fade.setDuration(duration);
                fade.setInterpolator(new AccelerateInterpolator());
                fade.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.removeView(plus);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        final ImageView plusCookie = new ImageView(MainActivity.this);
        plusCookie.setId(View.generateViewId());
        plusCookie.setImageResource(R.drawable.cookie_small);
        plusCookie.setElevation(50f);
        plusCookie.bringToFront();
        ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(48,48);
        plusCookie.setLayoutParams(params2);
        layout.addView(plusCookie);


        constraintSet.connect(plusCookie.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
        constraintSet.connect(plusCookie.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(plusCookie.getId(), ConstraintSet.RIGHT, plus.getId(), ConstraintSet.RIGHT);
        constraintSet.connect(plusCookie.getId(), ConstraintSet.LEFT, plus.getId(), ConstraintSet.LEFT);

        constraintSet.setVerticalBias(plusCookie.getId(),verticalBias);
        constraintSet.setHorizontalBias(plusCookie.getId(),horizontalBias);

        constraintSet.applyTo(layout);

        Animation plusCookieAnimationUp = new TranslateAnimation(0,0,10,0);
        plusCookieAnimationUp.setDuration((long)(duration*.25));
        final Animation plusCookieAnimationDown = new TranslateAnimation(0,0,0,100);
        plusCookieAnimationDown.setDuration((long)(duration*.5));
        plusCookie.startAnimation(plusCookieAnimationUp);
        plusCookieAnimationUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ObjectAnimator fade = ObjectAnimator.ofFloat(plusCookie, "Alpha", 1f, 0);
                fade.setDuration(duration);
                fade.start();

                int rand;
                if(Math.random() >.5)
                    rand = 100;
                else
                    rand = -100;
                ObjectAnimator bounce = ObjectAnimator.ofFloat(plusCookie, "translationX", rand);
                bounce.setDuration((duration));
                bounce.setInterpolator(new AccelerateInterpolator());
                bounce.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                plusCookie.startAnimation(plusCookieAnimationDown);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        plusCookieAnimationDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                layout.removeView(plusCookie);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void fall(double cookies) {
        cookies = (int)cookies;
        if(cookies > 10)
            cookies = 10;
        for(int i = 0; i<cookies; i++) {
            final ImageView fall = new ImageView(MainActivity.this);
            fall.setId(View.generateViewId());
            fall.setImageResource(R.drawable.cookie);
            fall.setElevation(2f);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(96,96);
            fall.setLayoutParams(params);
            layout.addView(fall);


            ConstraintSet constraintSet = new ConstraintSet(); //used to save constraints from current layout
            constraintSet.clone(layout);

            constraintSet.connect(fall.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
            constraintSet.connect(fall.getId(), ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(fall.getId(), ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);
            constraintSet.connect(fall.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);

            constraintSet.setVerticalBias(fall.getId(),0f);
            constraintSet.setHorizontalBias(fall.getId(),  (float) (Math.random()));

            constraintSet.applyTo(layout);


            Animation fallAnimation = new TranslateAnimation(0, 0, 0, getResources().getSystem().getDisplayMetrics().heightPixels);
            final long duration = 6000;
            fallAnimation.setDuration(duration);
            fall.startAnimation(fallAnimation);
            fallAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    ObjectAnimator fade = ObjectAnimator.ofFloat(fall, "Alpha", 1f, 0);
                    fade.setDuration(duration);
                    fade.start();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    layout.removeView(fall);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
//    public void addCursor(){
//        final ImageView cursor = new ImageView(this);
//        cursor.setId(View.generateViewId());
//        cursor.setImageResource(R.drawable.cursor);
//        cursor.bringToFront();
//        cursor.setElevation(45f);
//        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(96,96);
//        cursor.setLayoutParams(params);
//        layout.addView(cursor);
//
//
//        ConstraintSet constraintSet = new ConstraintSet(); //used to save constraints from current layout
//        constraintSet.clone(layout);
//
//        constraintSet.connect(cursor.getId(), ConstraintSet.TOP, cookieView.getId(), ConstraintSet.TOP);
//        constraintSet.connect(cursor.getId(), ConstraintSet.BOTTOM, cookieView.getId(), ConstraintSet.BOTTOM);
//        constraintSet.connect(cursor.getId(), ConstraintSet.RIGHT, cookieView.getId(), ConstraintSet.RIGHT);
//        constraintSet.connect(cursor.getId(), ConstraintSet.LEFT, cookieView.getId(), ConstraintSet.LEFT);
//
//        float verticalBias; //= (Math.random() > .5) ? 0:100;
//        float horizontalBias; //= (Math.random() > .5) ? 0:100;
//        double rand = Math.random();
//        if(rand > .75){
//            verticalBias = 0;
//            horizontalBias = (float)(Math.random()*100);
//        }
//        else if(rand > .5){
//            verticalBias = 100;
//            horizontalBias = (float)(Math.random()*100);
//        }
//        else if(rand > .25){
//            verticalBias = (float)(Math.random()*100);
//            horizontalBias = 0;
//        }
//        else{
//            verticalBias = (float)(Math.random()*100);
//            horizontalBias = 100;
//        }
//
//        constraintSet.setVerticalBias(cursor.getId(),verticalBias);
//        constraintSet.setHorizontalBias(cursor.getId(),  horizontalBias);
//
//        constraintSet.applyTo(layout);
//Log.d("TAG",cursor.isShown()+"");
//
//        Animation cursorAnimation = new ScaleAnimation(0f,1.0f,0f,1.0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
//        final long duration = 1000;
//        cursorAnimation.setDuration(duration);
//        cursor.startAnimation(cursorAnimation);
//
//    }
    /*
    public class CookieThread extends Thread{
        @Override
        public void run() {
            while (!stopThreads) {
                super.run();
                try {
                    if(1000/Cookie.cookiesPerSecond/Cookie.amount == Double.POSITIVE_INFINITY)
                        Thread.sleep((long)(1000/Cookie.cookiesPerSecond));
                    else
                        Thread.sleep((long)(1000/Cookie.cookiesPerSecond/Cookie.amount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Cookie.amount > 0) {
                    final double generate = Cookie.generate();
                    cookiesInBankView.post(new Runnable() { //can also use runOnUiThread
                        @Override
                        public void run() {
                            updateCookiesInBankView();
                            fall(generate);
                        }
                    });
                }
            }
        }
    }
    */


    public class GrandmaThread extends Thread{
        @Override
        public void run() {
            while (!stopThreads) {
                super.run();
                try {
                    if(1000/Grandma.cookiesPerSecond/Grandma.amount == Double.POSITIVE_INFINITY)
                        Thread.sleep((long)(1000/Grandma.cookiesPerSecond));
                    else
                        Thread.sleep((long)(1000/Grandma.cookiesPerSecond/Grandma.amount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Grandma.amount > 0) {
                    final double generate = Grandma.generate();
                        cookiesInBankView.post(new Runnable() { //can also use runOnUiThread
                            @Override
                            public void run() {
                                updateCookiesInBankView();
                                fall(generate);
                            }
                        });
                }
            }
        }
    }

    public class FarmThread extends Thread{
        @Override
        public void run() {
            while (!stopThreads) {
                super.run();
                try {
                    if(1000/Farm.cookiesPerSecond/Farm.amount == Double.POSITIVE_INFINITY)
                        Thread.sleep((long)(1000/Farm.cookiesPerSecond));
                    else
                        Thread.sleep((long)(1000/Farm.cookiesPerSecond/Farm.amount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(Farm.amount > 0) {
                    final double generate = Farm.generate();
                        cookiesInBankView.post(new Runnable() { //can also use runOnUiThread
                            @Override
                            public void run() {
                                updateCookiesInBankView();
                                fall(generate);
                            }
                        });
                }
            }
        }
    }

    public class CursorThread extends Thread{
        @Override
        public void run() {
            while (!stopThreads) {
                super.run();
                try {
                    if(1000/Cursor.cookiesPerSecond/Cursor.amount == Double.POSITIVE_INFINITY)
                        Thread.sleep((long)(1000/Cursor.cookiesPerSecond));
                    else
                        Thread.sleep((long)(1000/Cursor.cookiesPerSecond/Cursor.amount));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(Cursor.amount > 0) {
                    final double generate = Cursor.generate();
                        cookiesInBankView.post(new Runnable() { //can also use runOnUiThread
                            @Override
                            public void run() {
                                updateCookiesInBankView();
                                fall(generate);
                            }
                        });
                }
//                if(Cursor.amount != cursors){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            addCursor();
//                        }
//                    });
//                    cursors = Cursor.amount;
//                }
            }
        }
    }

    public synchronized void reset(){
        Cookie.cookiesInBank = 0;
        Cookie.cookiesPerSecond = 0;
        Cookie.cookiesPerClick = 1;
        Cookie.amount = 0;
        Cursor.amount = 0;
        Grandma.amount = 0;
        Farm.amount = 0;
        //cursors = 0;
        update();
        //recreate(); to remove cursors on screen??
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopThreads = true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        stopThreads = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Cookie.cookiesInBank = Double.longBitsToDouble(prefs.getLong("COOKIES_IN_BANK",(int)Cookie.cookiesInBank));
        Cookie.cookiesPerSecond = Double.longBitsToDouble(prefs.getLong("COOKIES_PER_SECOND",(int)Cookie.cookiesPerSecond));
        Cookie.cookiesPerClick = prefs.getInt("COOKIES_PER_CLICK",Cookie.cookiesPerClick);
        Cookie.amount = prefs.getInt("COOKIE_AMOUNT",Cookie.amount);
        Cursor.amount = prefs.getInt("CURSOR_AMOUNT",Cursor.amount);
        Grandma.amount = prefs.getInt("GRANDMA_AMOUNT",Grandma.amount);
        Farm.amount = prefs.getInt("FARM_AMOUNT",Farm.amount);
        //cursors = prefs.getInt("cursors",cursors);
        update();
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putLong("COOKIES_IN_BANK", Double.doubleToRawLongBits(Cookie.cookiesInBank));
        editor.putLong("COOKIES_PER_SECOND", Double.doubleToRawLongBits(Cookie.cookiesPerSecond));
        editor.putInt("COOKIES_PER_CLICK",Cookie.cookiesPerClick);
        editor.putInt("COOKIE_AMOUNT",Cookie.amount);
        editor.putInt("CURSOR_AMOUNT",Cursor.amount);
        editor.putInt("GRANDMA_AMOUNT",Grandma.amount);
        editor.putInt("FARM_AMOUNT",Farm.amount);
        //editor.putInt("cursors",cursors);
        editor.apply();
    }
}
