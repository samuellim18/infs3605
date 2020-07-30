package com.example.mudskipper.model;

import java.util.ArrayList;

public class SkillModel {
    ArrayList<String> SkillName;

    public SkillModel(ArrayList<String> skillName) {
        SkillName = skillName;
    }

    public ArrayList<String> getSkillName() {
        return SkillName;
    }

    public void setSkillName(ArrayList<String> skillName) {
        SkillName = skillName;
    }
}
