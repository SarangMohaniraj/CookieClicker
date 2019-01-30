package com.example.sarang.cookieclicker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class Purchased extends Fragment {

    public static ListView listView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_purchased, container, false);

        listView = view.findViewById(R.id.id_listview_purchased);


        listView.setAdapter(MainActivity.purchasedAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.startAnimation(MainActivity.animation);
                int amount = MainActivity.products.get(position).getAmount();
                String name = MainActivity.products.get(position).getName();
                if(amount != 1)
                    name+="s";

                //Toast.makeText(getActivity(), amount+" "+name, Toast.LENGTH_SHORT).show();

                String toast = amount+" "+name + " generating " + MainActivity.beautify(MainActivity.products.get(position).getCookiesPerSecond()*amount);
                if(MainActivity.products.get(position).getCookiesPerSecond()*amount != 1)
                   toast += " cookies per second";
                else
                    toast += " cookie per second";
                Snackbar.make(view,toast, Snackbar.LENGTH_SHORT).show();
            }
        });

        return view;
    }


}
