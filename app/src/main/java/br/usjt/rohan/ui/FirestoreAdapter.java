package br.usjt.rohan.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<Location, FirestoreAdapter.LocationViewHolder> {

    private OnListItemLongClick onListItemLongClick;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<Location> options, OnListItemLongClick onListItemLongClick) {
        super(options);
        this.onListItemLongClick = onListItemLongClick;
    }

    @Override
    protected void onBindViewHolder(@NonNull LocationViewHolder holder, int position, @NonNull Location model) {
        holder.location_name.setText(model.getLocation_name());
        holder.description.setText(model.getDescription());
        holder.coordinates.setText(model.getCoordinates());
        holder.dt_created.setText(model.getDt_created());
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private final TextView location_name;
        private final TextView description;
        private final TextView coordinates;
        private final TextView dt_created;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            location_name = itemView.findViewById(R.id.textViewLocationName);
            description = itemView.findViewById(R.id.textViewLocationDescription);
            coordinates = itemView.findViewById(R.id.textViewLocationCoordinates);
            dt_created = itemView.findViewById(R.id.textViewLocationDtCreated);

            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            onListItemLongClick.onItemLongClick(getItem(getAdapterPosition()), getAdapterPosition());
            return false;
        }
    }

    public interface OnListItemLongClick {
        void onItemLongClick(Location snapshot, int position);
    }

}
