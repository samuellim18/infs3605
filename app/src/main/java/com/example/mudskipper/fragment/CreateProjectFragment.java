package com.example.mudskipper.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.mudskipper.R;

public class CreateProjectFragment extends Fragment {
    private EditText project_name, project_description, video_link;
    private String project_nameS, project_descriptionS, video_linkS;
    private Button add_project_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_project, container, false);

        project_name = view.findViewById(R.id.et_project_name);
        project_description = view.findViewById(R.id.et_project_description);
        video_link = view.findViewById(R.id.et_video_link);
        add_project_btn = view.findViewById(R.id.add_project_btn);
        add_project_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewProject();
            }
        });

        return view;
    }

    private void addNewProject(){
        project_nameS = project_name.getText().toString();
        project_descriptionS = project_description.getText().toString();
        video_linkS = video_link.getText().toString();

        System.out.println("CHECKING!!");
        System.out.println(project_nameS);
    }

}
