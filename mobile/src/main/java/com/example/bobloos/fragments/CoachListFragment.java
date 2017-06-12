package com.example.bobloos.coach;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by bob.loos on 04/05/16.
 */
public class CoachListFragment extends Fragment {
    DatabaseHandler db;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    SimpleStringRecyclerViewAdapter recycleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        View inflatedView = inflater.inflate(R.layout.fragment_coach_list, container, false);
        mRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview);
        setupRecyclerView(mRecyclerView);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflatedView.findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                refreshItems();
                Log.d("LOG", "REGISTERING Refresh");
            }
        });
        return inflatedView;
    }

    void refreshItems() {
        List <PhysStateModel> items = db.getAllPhysStates();
        if (items.size() != 0) {
            recycleAdapter.update(items);
        }
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<PhysStateModel>listWithState = db.getAllPhysStates();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recycleAdapter = new SimpleStringRecyclerViewAdapter(getActivity(), listWithState);
        recyclerView.setAdapter(recycleAdapter);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<PhysStateModel> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public final View mView;
            public final TextView level;
            public final TextView time;
            public final TextView contextDescription;
            public final ImageView levelImage;
            public final RelativeLayout contextDescriptionContainer;
            Integer physStateId;
            DatabaseHandler db;

            public ViewHolder(View view) {
                super(view);
                mView = view;

                db = new DatabaseHandler(view.getContext());
                level = (TextView) view.findViewById(R.id.levelText);
                levelImage = (ImageView) view.findViewById(R.id.levelImage);
                time = (TextView) view.findViewById(R.id.text1);
                contextDescriptionContainer = (RelativeLayout) view.findViewById(R.id.contextDescriptionContainer);
                view.setOnClickListener(this);
                contextDescription = (TextView) view.findViewById(R.id.contextDescriptionTextView);
                Log.d("LOG", "ViewHolder: level:" + level + "time:" + time);

            }

            @Override
            public void onClick(View v) {
                Log.d("LOG", "GEETTING CLICKED");
                Log.d("LOG ID", physStateId.toString());

                Intent intent = new Intent(v.getContext(), EditPhysStateContext.class);
                intent.putExtra("PhysStateId", physStateId.toString());
                v.getContext().startActivity(intent);

            }
        }

        public void update(List<PhysStateModel> items) {
            mValues = items;
            notifyDataSetChanged();
        }

        public PhysStateModel getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<PhysStateModel> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.level.setText(mValues.get(position).getLevel());
            String level = mValues.get(position).getLevel();
            holder.time.setText(mValues.get(position).getDate());
            holder.physStateId = mValues.get(position).getId();
            String contextDescription = mValues.get(position).getContextDescription();
            if (contextDescription != null || level.equals("4") || level.equals("5") ) {
                holder.contextDescriptionContainer.setVisibility(View.VISIBLE);
                if (contextDescription != null) {
                    holder.contextDescription.setText(mValues.get(position).getContextDescription());
                } else {
                    holder.contextDescription.setText("You are currently in the highest physical state. Take a note");
                }
            } else {
                holder.contextDescription.setText(null);
                holder.contextDescriptionContainer.setVisibility(View.GONE);
            }

            switch (level) {
                case "-5":
                    holder.levelImage.setBackgroundResource(R.drawable.app_min5);
                    break;
                case "-4":
                    holder.levelImage.setBackgroundResource(R.drawable.app_min4);
                    break;
                case "-3":
                    holder.levelImage.setBackgroundResource(R.drawable.app_min3);
                    break;
                case "-2":
                    holder.levelImage.setBackgroundResource(R.drawable.app_min2);
                    break;
                case "-1":
                    holder.levelImage.setBackgroundResource(R.drawable.app_min1);
                    break;
                case "1":
                    holder.levelImage.setBackgroundResource(R.drawable.app_1);
                    break;
                case "2":
                    holder.levelImage.setBackgroundResource(R.drawable.app_2);
                    break;
                case "3":
                    holder.levelImage.setBackgroundResource(R.drawable.app_3);
                    break;
                case "4":
                    holder.levelImage.setBackgroundResource(R.drawable.app_4);
                    break;
                case "5":
                    holder.levelImage.setBackgroundResource(R.drawable.app_5);
                    break;
            }
        }



        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


}

