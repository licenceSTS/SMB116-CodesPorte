package fr.sts.codesporte;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PorteAdapter extends RecyclerView.Adapter<PorteAdapter.PorteViewHolder> {
    private final List<PorteItem> porteList;
    private OnItemClickListener listener;
    public PorteAdapter(List<PorteItem> porteList) {
        this.porteList = porteList;
    }

    @NonNull
    @Override
    public PorteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_porte, parent, false);
        return new PorteViewHolder(itemView, listener);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PorteViewHolder holder, int position) {
        PorteItem currentItem = porteList.get(position);
        holder.porteTextView.setText("Nom : " + currentItem.getDescription());
        holder.codeTextView.setText("Code : " +currentItem.getCode());
    }

    @Override
    public int getItemCount() {
        return porteList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updatePorteList(List<PorteItem> newPorteList) {
        porteList.clear();
        porteList.addAll(newPorteList);
        notifyDataSetChanged();
    }

    // Methode pour ajouter un élément
    public void addPorte(PorteItem newPorte) {
        porteList.add(newPorte);
        notifyItemInserted(porteList.size() - 1);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Supprime une gare de la liste
    public void removePorte(int position) {
        if (position >= 0 && position < porteList.size()) {
            porteList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Met à jour une gare existante
    public void updatePorte(int position, PorteItem updatedPorte) {
        if (position >= 0 && position < porteList.size()) {
            porteList.set(position, updatedPorte);
            notifyItemChanged(position);
        }
    }

    public static class PorteViewHolder extends RecyclerView.ViewHolder {
        TextView porteTextView;
        TextView codeTextView;

        public PorteViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            porteTextView = itemView.findViewById(R.id.textViewPorte);
            codeTextView = itemView.findViewById(R.id.textViewCode);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }
}