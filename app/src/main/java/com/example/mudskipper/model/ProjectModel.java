package com.example.mudskipper.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProjectModel {
    private String project_name, project_description, video_link, video_id;

    public ProjectModel(String project_name, String project_description, String video_link) {
        this.project_name = project_name;
        this.project_description = project_description;
        this.video_link = video_link;
    }

    public String getProject_name() {
        return project_name;
    }

    public String getProject_description() {
        return project_description;
    }

    public String getVideo_link() {
        return video_link;
    }

    public String getVideo_id() {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";

        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(video_link);

        if(matcher.find()){
            return matcher.group();
        }
        return null;
    }
}