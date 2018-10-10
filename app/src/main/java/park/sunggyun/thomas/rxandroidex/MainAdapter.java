package park.sunggyun.thomas.rxandroidex;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Vector;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import park.sunggyun.thomas.rxandroidex.databinding.ItemMainBinding;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private Vector<AppInfo> appInfoVector = new Vector<>();
    Context context;

    MainAdapter(Context context) {
        this.context = context;
        this.publishSubject = PublishSubject.create();
    }

    private PublishSubject<AppInfo> publishSubject;

    @NonNull
    @Override
    public MainViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemMainBinding binding = ItemMainBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainViewHolder mainViewHolder, int position) {
        ItemMainBinding binding = mainViewHolder.binding;

        AppInfo appInfo = appInfoVector.get(position);
        binding.imgView.setImageDrawable(appInfo.getIcon());
        binding.txtView.setText(appInfo.getName());

        mainViewHolder.getClickObserver(appInfo).subscribe(publishSubject);

    }

    @Override
    public int getItemCount() {
        return appInfoVector.size();
    }

    void update(AppInfo appInfo) {
        appInfoVector.add(appInfo);
    }

    PublishSubject<AppInfo> getPublishSubject() {
        return this.publishSubject;
    }

    class MainViewHolder extends RecyclerView.ViewHolder {
        ItemMainBinding binding;
        View view;
        MainViewHolder(ItemMainBinding binding) {
            super(binding.getRoot());
            view = binding.getRoot();
            this.binding = binding;
        }

        Observable<AppInfo> getClickObserver(AppInfo appInfo) {
            return Observable.create(event -> view.setOnClickListener(view -> event.onNext(appInfo)));
        }
    }
}
