package fr.sts.codesporte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PorteAdapter extends RecyclerView.Adapter<PorteAdapter.ViewHolder> {
    private final List<PorteItem> porteList;
    private OnItemClickListener listener;

    public PorteAdapter(List<PorteItem> porteList) {
        this.porteList = porteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_porte, parent, false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PorteItem currentItem = porteList.get(position);
        holder.porteTextView.setText("Description : " + currentItem.getDescription());
        holder.codeTextView.setText("Code : " +currentItem.getCode());
    }

    @Override
    public int getItemCount() {
        return porteList.size();
    }

    // Méthode pour supprimer un élément
    public void removeItem(int position) {
        porteList.remove(position);
        notifyItemRemoved(position);
    }

    // Méthode pour modifier un élément
    public void modifyItem(int position, PorteItem newItem) {
        porteList.set(position, newItem);
        notifyItemChanged(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView porteTextView;
        TextView codeTextView;

        public ViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            porteTextView = itemView.findViewById(R.id.textViewPorte);
            codeTextView = itemView.findViewById(R.id.textViewCode);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}