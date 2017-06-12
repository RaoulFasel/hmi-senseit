package com.example.bobloos.fragments;

/**
 * Created by bob.loos on 30/05/16.
 */

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
import android.widget.TextView;

import com.example.bobloos.coach.EditSelfReport;
import com.example.bobloos.coach.MainActivity;
import com.example.bobloos.coach.NewSelfReport;
import com.example.bobloos.coach.R;
import com.example.bobloos.database.DatabaseHandler;
import com.example.bobloos.model.PhysStateModel;
import com.example.bobloos.model.SelfReportModel;

import java.util.List;

public class SelfReportFragment extends Fragment {
    DatabaseHandler db;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    SimpleStringRecyclerViewAdapter recycleAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = new DatabaseHandler(getActivity());
        View inflatedView = inflater.inflate(R.layout.fragment_self_report, container, false);
        mRecyclerView = (RecyclerView) inflatedView.findViewById(R.id.recyclerview_self_report);
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
        List <SelfReportModel> items = db.getAllSelfReports();
        if (items.size() != 0) {
            recycleAdapter.update(items);
        }
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        List<SelfReportModel> listWithSelfReports = db.getAllSelfReports();
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recycleAdapter = new SimpleStringRecyclerViewAdapter(getActivity(),
                listWithSelfReports);
        recyclerView.setAdapter(recycleAdapter);
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>{

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<SelfReportModel> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
            public final View mView;
            public final TextView story;
            public final TextView time;
            Integer SelfReportId;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                view.setOnClickListener(this);
                time = (TextView) view.findViewById(R.id.text1);
                story = (TextView) view.findViewById(R.id.story);


            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + mTextView.getText();
//            }

            @Override
            public void onClick(View v) {
                Log.d("LOG", "GEETTING CLICKED");
                Log.d("LOG ID", SelfReportId.toString());

                Intent intent = new Intent(v.getContext(), EditSelfReport.class);
                intent.putExtra("SelfReportId", SelfReportId.toString());
                v.getContext().startActivity(intent);

            }
        }



        public void update(List<SelfReportModel> items) {
            mValues = items;
            notifyDataSetChanged();
        }


        public SelfReportModel getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<SelfReportModel> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.self_report_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.SelfReportId = mValues.get(position).getId();
            holder.time.setText(mValues.get(position).getDate());
            holder.story.setText(mValues.get(position).getReportText());
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
