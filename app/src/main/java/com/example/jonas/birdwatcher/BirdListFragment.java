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
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Fragment that displays a list of all the Birds.
 * The user can tap the app bar to create a new Bird.
 *
 * File:       BirdListFragment.java
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

        birds = BirdBank.get(getActivity()).getBirds();

        BirdAdapter adapter = new BirdAdapter(birds);
        setListAdapter(adapter);

        setHasOptionsMenu(true);
    }

    /**
     * Inflate the app bar menu.
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_bird_list, menu);
    }

    /**
     * Start CreateBirdActivity to create a new Bird if the user
     * taps the new bird icon in the app bar menu.
     *
     * @param item Menu item.
     * @return True or menu item.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_bird :
                Intent intent = new Intent(getActivity(), CreateBirdActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Starts BirdActivity to display more information about a Bird
     * when the user taps on a item in the list.
     *
     * @param l ListView.
     * @param v View.
     * @param position Position.
     * @param id Id.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        Bird bird = ((BirdAdapter)getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), BirdActivity.class);
        i.putExtra("BirdID", ""+bird.getmId());
        startActivity(i);
    }

    /**
     * Update the birds when returning to the fragment if any
     * changes to the Birds has been made.
     */
    @Override
    public void onResume() {
        super.onResume();
        birds = BirdBank.get(getActivity()).getBirds();
        ((BirdAdapter)getListAdapter()).notifyDataSetChanged();
    }

    /**
     * ArrayAdapter for the list, sets the list item to a
     * TextView holding the Bird's name;
     */
    private class BirdAdapter extends ArrayAdapter<Bird> {

        public BirdAdapter(ArrayList<Bird> birds) {
            super(getActivity(), 0, birds);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.list_item_bird, null);
            }
            Bird bird = getItem(position);

            TextView titleTextView =
                    (TextView) convertView.findViewById(R.id.bird_list_bird);
            if(bird != null) {
                titleTextView.setText(bird.getName());
            }

            return convertView;
        }

    }
}
