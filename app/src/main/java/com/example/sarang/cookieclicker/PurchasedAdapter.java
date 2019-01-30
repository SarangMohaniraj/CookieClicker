package com.example.sarang.cookieclicker;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

public class PurchasedAdapter extends ArrayAdapter<Product> {
    Context context;
    int resource;
    List<Product> products;


    public PurchasedAdapter(Context context, int resource, List<Product> products) {
        super(context, resource, products);
        this.context = context;
        this.resource = resource;
        this.products = products;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(R.layout.purchased_background,null);

        ConstraintLayout layout = adapterLayout.findViewById(R.id.id_layout_background);

        if(position == 0) {
            layout.setVisibility(View.GONE);
            return adapterLayout;
        }

        layout.setBackgroundResource(products.get(position).getBackground());
        for(int i = 0; i<products.get(position).getAmount(); i++){
            Log.d("Products",products.get(position).getAmount()+"");
            ImageView draw = new ImageView(context);
            draw.setId(View.generateViewId());
            draw.setImageResource(products.get(position).getImage());
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT);
            draw.setLayoutParams(params);
            layout.addView(draw);

            ConstraintSet constraintSet = new ConstraintSet(); //used to save constraints from current layout
            constraintSet.clone(layout);

            constraintSet.connect(draw.getId(),ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP);
            constraintSet.connect(draw.getId(),ConstraintSet.BOTTOM, layout.getId(), ConstraintSet.BOTTOM);
            constraintSet.connect(draw.getId(),ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT);
            constraintSet.connect(draw.getId(),ConstraintSet.RIGHT, layout.getId(), ConstraintSet.RIGHT);

            constraintSet.setVerticalBias(draw.getId(),(float)((Math.random()*.5)+.5));
            constraintSet.setHorizontalBias(draw.getId(),(float)Math.random());

            constraintSet.applyTo(layout);
            Log.d("TAG",draw.isShown()+"");
        }


        return adapterLayout;
    }

}

