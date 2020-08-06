package com.example.mudskipper.model;

public class CollaboratorModel {
    private String collaborator_name, collaborator_role, collaborator_email;

    public CollaboratorModel(String collaborator_name, String collaborator_role, String collaborator_email) {
        this.collaborator_name = collaborator_name;
        this.collaborator_role = collaborator_role;
        this.collaborator_email = collaborator_email;
    }

    public String getCollaborator_name() {
        return collaborator_name;
    }

    public String getCollaborator_role() {
        return collaborator_role;
    }

    public String getCollaborator_email() {
        return collaborator_email;
    }
}
