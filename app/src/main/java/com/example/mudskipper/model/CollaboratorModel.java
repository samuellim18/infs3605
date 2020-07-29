package com.example.mudskipper.model;

public class CollaboratorModel {
    private String collaborator_name, collaborator_role;

    public CollaboratorModel(String collaborator_name, String collaborator_role) {
        this.collaborator_name = collaborator_name;
        this.collaborator_role = collaborator_role;
    }

    public String getCollaborator_name() {
        return collaborator_name;
    }

    public String getCollaborator_role() {
        return collaborator_role;
    }
}
