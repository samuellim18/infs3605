package com.example.mudskipper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AddCollaboratorDialog extends AppCompatDialogFragment implements AdapterView.OnItemSelectedListener {
    private EditText et_name, et_role;
    private String role;
    private Spinner role_spinner;
    private AddCollaboratorDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_collaborator_dialog, null);

        role_spinner = view.findViewById(R.id.role_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.roles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role_spinner.setAdapter(adapter);
        role_spinner.setOnItemSelectedListener(this);

        builder.setView(view)
                .setTitle("Add Collaborator")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton("add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        String collab_name = et_name.getText().toString();
                        //String collab_role = et_role.getText().toString();
                        AddCollaboratorDialogListener listener = (AddCollaboratorDialogListener) getTargetFragment();
                        listener.applyTexts(collab_name, role);
                    }
                });

        et_name = view.findViewById(R.id.edit_collaborator_name);
        //et_role = view.findViewById(R.id.edit_collaborator_role);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (AddCollaboratorDialogListener) context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        role = parent.getItemAtPosition(position).toString();
        //Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface AddCollaboratorDialogListener{
        void applyTexts(String name, String role);
    }
}
