package com.example.jonas.birdwatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 * File:       ${FILE_NAME}.java
 * Author:     Jonas Nyman
 * Assignment: Inlämningsuppgift 3 - Valfri Applikation
 * Course:     Utveckling av mobila applikationer
 * Version:    1.0
 */
public class BirdListFragment extends ListFragment {

    private ArrayList<Bird> birds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().setTitle("Your birds");

        //birds = new ArrayList<Bird>();
        birds = BirdBank.get(getActivity()).getBirds();

        /*
        for (int i = 0; i < 10; i++) {
            Bird bird = new Bird("Name"+i, i);
            birds.add(bird);
        } */
        BirdAdapter adapter = new BirdAdapter(birds);
        setListAdapter(adapter);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_bird_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_bird :
                Log.d("LisftFragment", "Tryckte på new bird");
                Intent intent = new Intent(getActivity(), CreateBirdActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //TODO behöver bara skiicka birdID och sen hämta med BirdBank
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bird bird = ((BirdAdapter)getListAdapter()).getItem(position);
        Log.d("InnanPos", "Position: "+position);
        Log.d("Innan", ""+bird.getName()+","+bird.getmId());
        Intent i = new Intent(getActivity(), BirdActivity.class);
        i.putExtra("BirdID", ""+bird.getmId());
        //i.putExtra("Hej1", bird);
        //i.putParcelableArrayListExtra("Hej2",bird.getPhotos());
        startActivity(i);
    }


    @Override
    public void onResume() {
        super.onResume();
        birds = BirdBank.get(getActivity()).getBirds();
        ((BirdAdapter)getListAdapter()).notifyDataSetChanged();
    }


    private class BirdAdapter extends ArrayAdapter<Bird> {

        public BirdAdapter(ArrayList<Bird> birds) {
            super(getActivity(), 0, birds);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_bird, null);
            }
            // Configure the view for this Crime
            Bird bird = getItem(position);
            if (bird == null) {
                Log.d("TAG", "Bird var null");
            } else {
                //Log.d("TAG", ""+bird.getName());
            }


            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.bird_list_bird);
            titleTextView.setText(bird.getName());


            return convertView;
        }

    }
}
