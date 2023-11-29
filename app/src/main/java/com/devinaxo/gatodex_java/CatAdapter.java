package com.devinaxo.gatodex_java;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import com.bumptech.glide.Glide;

public class CatAdapter extends CursorAdapter {
    private final MainActivity mainActivity;

    public CatAdapter(Context context, Cursor cursor, int flags, MainActivity mainActivity) {
        super(context, cursor, flags);
        this.mainActivity = mainActivity;
    }

    static class ViewHolder {
        ImageView catImageView;
        TextView idTextView;
        TextView nicknameTextView;
        TextView textViewType;
        TextView textViewDate;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate the item layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.cat_item, parent, false);

        // ViewHolder pattern for efficient view reuse
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.idTextView = view.findViewById(R.id.catIdTextView);
        viewHolder.nicknameTextView = view.findViewById(R.id.catNicknameTextView);
        viewHolder.textViewDate = view.findViewById(R.id.catDateTextView);
        viewHolder.textViewType = view.findViewById(R.id.catTypeTextView);
        viewHolder.catImageView = view.findViewById(R.id.catImageView);

        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idTextView = view.findViewById(R.id.catIdTextView);
        TextView nicknameTextView = view.findViewById(R.id.catNicknameTextView);
        TextView textViewType = view.findViewById(R.id.catTypeTextView);
        TextView textViewDate = view.findViewById(R.id.catDateTextView);
        ImageView catImageView = view.findViewById(R.id.catImageView);

        long catId = cursor.getLong(cursor.getColumnIndexOrThrow("_id"));

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.catImageView = catImageView;

            view.setTag(viewHolder);
        }

        // Set click listener on the ImageView to show CatDetailDialogFragment
        viewHolder.catImageView.setOnClickListener(v -> mainActivity.showCatDetailDialog(catId));

        // Extract the data from the cursor
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String nickname = cursor.getString(cursor.getColumnIndexOrThrow("nickname"));
        String type = cursor.getString(cursor.getColumnIndexOrThrow("type"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("date_met"));
        String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("path"));

        // Extract and set data to the views
        String idText = "ID: " + id;
        String nicknameText = "Nickname: " + nickname;
        String typeText = "Species: " + type;
        String dateText = "Date met: " + date;

        idTextView.setText(idText);
        nicknameTextView.setText(nicknameText);
        textViewType.setText(typeText);
        textViewDate.setText(dateText);

        // Load the cat image using Glide
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder) // Placeholder until the image is loaded
                .into(catImageView);

        Button deleteCatButton = view.findViewById(R.id.deleteCatButton);

        deleteCatButton.setOnClickListener(v -> {
            // Handle the "Delete" button click
            showDeleteConfirmationDialog(catId);
        });

    }
    private void showDeleteConfirmationDialog(final long catId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setMessage("Are you sure you want to delete this cat?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mainActivity.getDbHelper().deleteCat(catId);
                Cursor newCursor = mainActivity.getDbHelper().getAllCats();
                CatAdapter.super.swapCursor(newCursor);
                notifyDataSetChanged(); // Notify the adapter that the data set has changed
                mainActivity.updateEmptyViewVisibility();
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    @Override
    public long getItemId(int position) {
        Cursor cursor = getCursor();
        // Assuming that your Cursor contains a column named "_id" as the cat ID
        if (cursor.moveToPosition(position)) {
            return cursor.getLong(cursor.getColumnIndexOrThrow("_id"));
        }
        return super.getItemId(position);
    }


}
