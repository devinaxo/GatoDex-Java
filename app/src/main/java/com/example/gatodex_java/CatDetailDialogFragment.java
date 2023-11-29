package com.example.gatodex_java;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;

public class CatDetailDialogFragment extends DialogFragment {
    private static final String ARG_CAT_ID = "cat_id";

    public static CatDetailDialogFragment newInstance(long catId) {
        CatDetailDialogFragment fragment = new CatDetailDialogFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CAT_ID, catId);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        long catId = getArguments().getLong(ARG_CAT_ID);

        // Fetch cat details from the database
        CatDbHelper dbHelper = new CatDbHelper(requireContext());
        Cat cat = dbHelper.getCatById(catId);


        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.cat_detail_dialog_fragment, null);

        // Retrieve views from the layout
        ImageView imageViewCatDetail = view.findViewById(R.id.imageViewCatDetail);
        TextView textViewNickname = view.findViewById(R.id.textViewNickname);
        TextView textViewType = view.findViewById(R.id.textViewType);
        TextView textViewPlace = view.findViewById(R.id.textViewPlace);
        TextView textViewDate= view.findViewById(R.id.textViewDate);

        // Set cat details to the views
        Glide.with(requireContext())
                .load(cat.getImagePath())
                .placeholder(R.drawable.placeholder)
                .into(imageViewCatDetail);

        String aux1 = "Nickname: " + cat.getNickname();
        String aux2 = "Type/Species: " + cat.getType();
        String aux3 = "Place met: " + cat.getPlaceMet();
        String aux4 = "Date met: " + cat.getDateMet();

        textViewNickname.setText(aux1);
        textViewType.setText(aux2);
        textViewPlace.setText(aux3);
        textViewDate.setText(aux4);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(view)
                .setPositiveButton("OK", null);

        return builder.create();
    }
}
