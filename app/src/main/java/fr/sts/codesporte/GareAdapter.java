package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GareAdapter extends RecyclerView.Adapter<GareAdapter.GareViewHolder> {
    private final List<GareItem> gareList;
    private OnItemClickListener listener;
    public GareAdapter(List<GareItem> gareList) {
        this.gareList = gareList;
    }

    @NonNull
    @Override
    public GareViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gare, parent, false);
        return new GareViewHolder(itemView, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull GareViewHolder holder, int position) {
        GareItem currentItem = gareList.get(position);
        holder.gareName.setText(currentItem.getNom());
        holder.gareCodeCount.setText("Nombre de codes: " + currentItem.getPorteList().size());
    }

    @Override
    public int getItemCount() {
        return gareList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public static class GareViewHolder extends RecyclerView.ViewHolder {
        public TextView gareName;
        public TextView gareCodeCount;

        public GareViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            gareName = itemView.findViewById(R.id.gare_name);
            gareCodeCount = itemView.findViewById(R.id.gare_code_count);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateGareList(List<GareItem> newGareList) {
        gareList.clear();
        gareList.addAll(newGareList);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Ajoute une nouvelle gare à la liste
    public void addGare(GareItem newGare) {
        gareList.add(newGare);
        notifyItemInserted(gareList.size() - 1);
    }

    // Supprime une gare de la liste
    public void removeGare(int position) {
        if (position >= 0 && position < gareList.size()) {
            gareList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Met à jour une gare existante
    public void updateGare(int position, GareItem updatedGare) {
        if (position >= 0 && position < gareList.size()) {
            gareList.set(position, updatedGare);
            notifyItemChanged(position);
        }
    }
}