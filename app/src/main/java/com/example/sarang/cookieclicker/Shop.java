package com.example.sarang.cookieclicker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class Shop extends Fragment {

    public static ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_shop, container, false);

        listView = view.findViewById(R.id.id_listview_shop);



        listView.setAdapter(MainActivity.shopAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(Cookie.cookiesInBank >= MainActivity.products.get(position).getPrice()){
                    view.startAnimation(MainActivity.animation);


                    Cookie.cookiesInBank -= MainActivity.products.get(position).getPrice();
                    MainActivity.products.get(position).add();
                    MainActivity.update();

                    MainActivity.shopAdapter.notifyDataSetChanged();
                    if(MainActivity.purchasedAdapter != null)
                        MainActivity.purchasedAdapter.notifyDataSetChanged();
                }
            }
        });


        return view;
    }


}
