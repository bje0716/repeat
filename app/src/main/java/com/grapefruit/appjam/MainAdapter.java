package com.grapefruit.appjam;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grapefruit.appjam.databinding.MainItemBinding;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by GrapeFruit on 2017-08-14.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Realm realm;
    private RealmResults<MainItem> results;

    public MainAdapter(RealmResults<MainItem> data) {
        results = data;
        realm = Realm.getDefaultInstance();
    }

    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_item, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MainViewHolder holder, int position) {
        if (results.get(position) != null) {
            holder.binding.check.setChecked(results.get(position).getCheck());
            holder.binding.item.setText(results.get(position).getItem());
            holder.binding.date.setText(results.get(position).getDate());

            switch (position) {
                default:
                    holder.binding.check.setOnCheckedChangeListener((compoundButton, b) -> {
                        if (holder.binding.check.isChecked()) {
                            holder.binding.item.setEnabled(false);
                            holder.binding.date.setEnabled(false);
                        } else {
                            holder.binding.item.setEnabled(true);
                            holder.binding.date.setEnabled(true);
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class MainViewHolder extends RecyclerView.ViewHolder {

        private MainItemBinding binding;

        public MainViewHolder(final View itemView) {
            super(itemView);
            binding = DataBindingUtil.bind(itemView);

            itemView.setOnLongClickListener(view -> {
                switch (getAdapterPosition()) {
                    default:
                        new AlertDialog.Builder(itemView.getContext())
                                .setCancelable(false)
                                .setTitle("삭제할까요?")
                                .setMessage("삭제를 할 경우 해당 할 일이 삭제됩니다")
                                .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> realm.executeTransaction(realm -> {
                                    RealmResults<MainItem> delete = realm.where(MainItem.class)
                                            .equalTo("item", binding.item.getText().toString())
                                            .findAll();
                                    delete.deleteAllFromRealm();
                                    notifyDataSetChanged();
                                }))
                                .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss()).show();
                        break;
                }
                return true;
            });
        }
    }
}
