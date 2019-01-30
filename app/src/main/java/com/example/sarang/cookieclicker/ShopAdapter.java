package com.example.sarang.cookieclicker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShopAdapter extends ArrayAdapter<Product> {
    Context context;
    int resource;
    List<Product> products;

    public ShopAdapter(Context context, int resource, List<Product> products) {
        super(context, resource, products);
        this.context = context;
        this.resource = resource;
        this.products = products;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View adapterLayout = layoutInflater.inflate(R.layout.index,null);

        if(Cookie.cookiesInBank >= MainActivity.products.get(position).getPrice()){
            adapterLayout.setAlpha(1f);
        }else
            adapterLayout.setAlpha(0.7f);

        TextView name = adapterLayout.findViewById(R.id.id_product_name);
        ImageView icon = adapterLayout.findViewById(R.id.id_product_icon);
        TextView price = adapterLayout.findViewById(R.id.id_product_price);
        TextView cps = adapterLayout.findViewById(R.id.id_product_cps);
        TextView amount = adapterLayout.findViewById(R.id.id_product_amount);

        name.setText(products.get(position).getName());
        price.setText(MainActivity.beautify(products.get(position).getPrice()) +"");
        icon.setImageResource(products.get(position).getIcon());
        cps.setText("+" + MainActivity.beautify(products.get(position).getCookiesPerSecond()) + " CPS");
        amount.setText(MainActivity.beautify(products.get(position).getAmount()) +"");


        return adapterLayout;
    }


}

