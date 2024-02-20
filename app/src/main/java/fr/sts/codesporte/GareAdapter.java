package fr.sts.codesporte;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GareAdapter extends RecyclerView.Adapter<GareAdapter.GareViewHolder> {
    private List<GareItem> gareList;

    public GareAdapter(List<GareItem> gareList) {
        this.gareList = gareList;
    }

    @NonNull
    @Override
    public GareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gare, parent, false);
        return new GareViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull GareViewHolder holder, int position) {
        GareItem currentItem = gareList.get(position);
        holder.gareName.setText(currentItem.getName());
        holder.gareCodeCount.setText("Nombre de codes: " + currentItem.getCodeCount());
    }

    @Override
    public int getItemCount() {
        return gareList.size();
    }

    public static class GareViewHolder extends RecyclerView.ViewHolder {
        public TextView gareName;
        public TextView gareCodeCount;
        public ImageView forwardIcon;

        public GareViewHolder(View itemView) {
            super(itemView);
            gareName = itemView.findViewById(R.id.gare_name);
            gareCodeCount = itemView.findViewById(R.id.gare_code_count);
            forwardIcon = itemView.findViewById(R.id.forward_icon);
        }
    }
}

