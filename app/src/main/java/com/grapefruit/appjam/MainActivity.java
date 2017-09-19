package com.grapefruit.appjam;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.grapefruit.appjam.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMainBinding binding;

    private Realm realm;
    private RealmQuery<MainItem> query;
    private RealmResults<MainItem> results;
    private MainAdapter adapter;
    private SimpleDateFormat sdf;
    private SimpleDateFormat mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        realm = Realm.getDefaultInstance();
        query = realm.where(MainItem.class);
        results = query.findAll();

        if (adapter == null) {
            adapter = new MainAdapter(results);
            adapter.notifyDataSetChanged();
        }

        if (adapter.getItemCount() == 0) {
            binding.noItem.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
        } else {
            binding.noItem.setVisibility(View.GONE);
        }

        binding.recycler.setHasFixedSize(true);
        binding.recycler.setItemAnimator(new DefaultItemAnimator());
        binding.recycler.setLayoutManager(new LinearLayoutManager(this));
        binding.recycler.setAdapter(adapter);

        binding.fab.setOnClickListener(this);

        sdf = new SimpleDateFormat("MM월 dd일 E요일", Locale.getDefault());
        binding.date.setText(sdf.format(new Date()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (adapter.getItemCount() == 0) {
                    Toast.makeText(getBaseContext(), "삭제할 할 일이 없습니다", Toast.LENGTH_SHORT).show();
                } else {
                    new MaterialDialog.Builder(this)
                            .cancelable(false)
                            .title("정말로 전체삭제 하시겠습니까?")
                            .content("전체삭제를 하시면 복구가 불가능 합니다")
                            .positiveText(android.R.string.ok)
                            .negativeText(android.R.string.cancel)
                            .onPositive((dialog, which) -> realm.executeTransaction(realm -> {
                                results.deleteAllFromRealm();
                                adapter.notifyDataSetChanged();
                                binding.noItem.setVisibility(View.VISIBLE);
                            }))
                            .onNegative((dialog, which) -> dialog.dismiss()).show();
                }
                break;
            case R.id.menu_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
        }
        return true;
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.fab:
                new MaterialDialog.Builder(this)
                        .cancelable(false)
                        .title("할 일 등록")
                        .customView(R.layout.dialog_add, false)
                        .positiveText("등록")
                        .negativeText(android.R.string.cancel)
                        .onPositive((dialog, which) -> {
                            EditText et = (EditText) dialog.getCustomView().findViewById(R.id.dialog_item);
                            final String txt = et.getText().toString();

                            if (txt.equals("")) {
                                Toast.makeText(getBaseContext(), "작성 후 등록해주세요", Toast.LENGTH_SHORT).show();
                            } else {
                                realm.executeTransaction(realm -> {
                                    mDate = new SimpleDateFormat("MM월 dd일 a hh:ss", Locale.getDefault());
                                    MainItem item = realm.createObject(MainItem.class);
                                    item.setCheck(false);
                                    item.setItem(txt);
                                    item.setDate(mDate.format(new Date()));
                                    adapter.notifyDataSetChanged();
                                    binding.noItem.setVisibility(View.GONE);
                                });
                            }
                        })
                        .onNegative((dialog, which) -> dialog.dismiss()).show();
                break;
        }
    }
}
