package fr.sts.codesporte;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CodeAdapter extends RecyclerView.Adapter<CodeAdapter.ViewHolder> {
    private final List<CodeItem> codeList;

    public CodeAdapter(List<CodeItem> codeList) {
        this.codeList = codeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_porte, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CodeItem currentItem = codeList.get(position);
        holder.porteTextView.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return codeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView porteTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            porteTextView = itemView.findViewById(R.id.textViewPorte);
        }
    }
}