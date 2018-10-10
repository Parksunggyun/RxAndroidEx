package park.sunggyun.thomas.rxandroidex;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import park.sunggyun.thomas.rxandroidex.databinding.ActivityMainBinding;


public class MainActivity  extends RxAppCompatActivity {

    MainAdapter adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
            binding.rxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            Log.e("onCreate()", "application started!");
            adapter = new MainAdapter(this);
            binding.rxRecyclerView.setAdapter(adapter);
            adapter.getPublishSubject()
                    .subscribe(s ->
                            Toast.makeText(getApplicationContext(), s.getName(), Toast.LENGTH_SHORT).show()
                    ).isDisposed();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter == null) {
            return;
        }

        getAppInfo()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(item -> {
                    Log.e("getAppInfo", item.getName());
                    adapter.update(item);
                    adapter.notifyDataSetChanged();
                }).isDisposed();
    }

    private Observable<AppInfo> getAppInfo() {
        final PackageManager manager = this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        return Observable.fromIterable(manager.queryIntentActivities(i, 0))
                .sorted(new ResolveInfo.DisplayNameComparator(manager))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(item -> {
                    Drawable img = item.activityInfo.loadIcon(manager);
                    String title = item.activityInfo.loadLabel(manager).toString();
                    return new AppInfo(title, img);
                });
    }
}
