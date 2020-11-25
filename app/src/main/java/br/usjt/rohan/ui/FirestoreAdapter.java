package br.usjt.rohan.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import br.usjt.rohan.R;
import br.usjt.rohan.model.Location;

import static br.usjt.rohan.R.anim.fade_scale_anim;

public class FirestoreAdapter extends FirestoreRecyclerAdapter<Location, FirestoreAdapter.LocationViewHolder> {

    Context mContext;
    private OnListItemLongClick onListItemLongClick;

    public FirestoreAdapter(@NonNull FirestoreRecyclerOptions<Location> options, OnListItemLongClick onListItemLongClick, Context mContext) {
        super(options);
        this.mContext = mContext;
        this.onListItemLongClick = onListItemLongClick;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    @Override
    protected void onBindViewHolder(@NonNull LocationViewHolder holder, int position, @NonNull Location model) {
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(mContext, fade_scale_anim));
        holder.location_name.setText(model.getLocation_name());
        holder.description.setText(model.getDescription());
        holder.coordinates.setText(model.getCoordinates());
        holder.dt_created.setText(model.getDt_created());
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    public class LocationViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        private final TextView location_name;
        private final TextView description;
        private final TextView coordinates;
        private final TextView dt_created;

        CardView cardView;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewLista);
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
