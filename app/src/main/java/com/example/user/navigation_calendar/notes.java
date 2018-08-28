package com.example.user.navigation_calendar;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class notes extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private noteAdapter adapter;

    public notes() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notes, container, false);
        ImageButton add_notes=(ImageButton)view.findViewById(R.id.btn_addNotes);
        add_notes.setOnClickListener(this);

        recyclerView = view.findViewById(R.id.recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        List<note> notes = new ArrayList<>();
        notes.add(new note("title1", "content1"));
        notes.add(new note("title2", "content2"));

        adapter = new noteAdapter(notes);
        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_addNotes:
                Intent itadd = new Intent(getActivity(),AddNotes.class);
                startActivity(itadd);
                break;
        }
    }
}

class note {
    private String title;
    private String content;

    public note(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

class noteAdapter extends RecyclerView.Adapter<noteAdapter.ViewHolder> {
    private List<note> data;

    public noteAdapter(List<note> data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.note_title);
            content = v.findViewById(R.id.note_content);
        }
    }

    @Override
    public noteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notes_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.title.setText(data.get(position).getTitle());
        holder.content.setText(data.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}