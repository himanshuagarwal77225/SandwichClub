package com.udacity.sandwichclub.ui.sandwichlist;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.udacity.sandwichclub.R;
import com.udacity.sandwichclub.model.Sandwich;
import com.udacity.sandwichclub.utils.AppExecutors;
import com.udacity.sandwichclub.utils.JsonUtils;
import com.udacity.sandwichclub.utils.SingleLiveEvent;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class SandwichListViewModel extends AndroidViewModel {

    private final Context mContext;

    private final MutableLiveData<List<Sandwich>> mObservableSandwiches;

    private final SingleLiveEvent<Integer> mOpenSandwichEvent = new SingleLiveEvent<>();

    public SandwichListViewModel(@NonNull Application application) {
        super(application);
        Timber.d("Creating viewModel");

        // initialize data
        mContext = application.getApplicationContext();
        AppExecutors mExecutors = AppExecutors.getInstance();
        mObservableSandwiches = new MutableLiveData<>();
        final List<Sandwich> sandwicheList = new ArrayList<>();

        // mObservableSandwiches.setValue(null);
        // parse json array on background thread
        mExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Timber.d("Start parsing Json");

                String[] sandwiches = mContext.getResources().getStringArray(R.array.sandwich_details);
                for (String sandwiche : sandwiches) {
                    Sandwich sandwich = null;
                    try {
                        sandwich = JsonUtils.parseSandwichJson(sandwiche);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sandwicheList.add(sandwich);
                }
                Timber.d("Json parsing finished");

                if (!sandwicheList.isEmpty()) {
                    Timber.d("Json not null and has " + sandwicheList.size() + " items");
                    // update sandwich MutableLiveData from background thread
                    mObservableSandwiches.postValue(sandwicheList);
                }
            }
        });
    }

    public LiveData<List<Sandwich>> getSandwichList() {
        return mObservableSandwiches;
    }

    public MutableLiveData<Integer> getOpenSandwichEvent() {
        return mOpenSandwichEvent;
    }
}
