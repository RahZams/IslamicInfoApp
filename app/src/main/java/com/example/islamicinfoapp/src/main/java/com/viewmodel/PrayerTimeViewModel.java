package com.example.islamicinfoapp.src.main.java.com.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.islamicinfoapp.R;
import com.example.islamicinfoapp.src.main.java.com.model.PrayerTiming;
import com.example.islamicinfoapp.src.main.java.com.model.QuranApi;
import com.example.islamicinfoapp.src.main.java.com.model.QuranApiService;
import com.example.islamicinfoapp.src.main.java.com.model.QuranDatabase;
import com.example.islamicinfoapp.src.main.java.com.utilities.PrayerTimeDeserializer;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PrayerTimeViewModel extends AndroidViewModel {
    public MutableLiveData<PrayerTiming> mTimingMutableLiveData = new MutableLiveData<>();
    private QuranApi mQuranApi;
    private DBTask mdDbTask;
    private RetrieveTask mRetrieveTask;
    private DbCountTask mDbCountTask;

    public PrayerTimeViewModel(@NonNull Application application) {
        super(application);
        mQuranApi = QuranApiService.getRetrofitInstance(getApplication().getResources().
                getString(R.string.prayer_times_base_url),PrayerTiming.class,
                new PrayerTimeDeserializer()).create(QuranApi.class);
    }

//    public void fetchFromRemote(String city,String country){
//        int method = Integer.parseInt(getApplication().getResources().getString(R.string.prayer_time_calculation_method));
//        mQuranApi.getPrayerTiming(city,country,method).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableSingleObserver<PrayerTiming>() {
//                    @Override
//                    public void onSuccess(PrayerTiming prayerTiming) {
//                        mdDbTask = new DBTask();
//                        Log.d("prayer", "onSuccess: " + prayerTiming.getPrayerTimeEngDate());
//                        prayerTiming.setCity(city);
//                        prayerTiming.setCountry(country);
//                        mdDbTask.execute(prayerTiming);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("prayer", "onError: " + e.getMessage());
//                    }
//                });
//    }

    public void fetchFromRemote(String city,String country){
        int method = Integer.parseInt(getApplication().getResources().getString(R.string.prayer_time_calculation_method));
        mQuranApi.getPrayerTiming(city,country,method)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<PrayerTiming>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(PrayerTiming prayerTiming) {
                        mdDbTask = new DBTask();
                        Log.d("prayer", "onSuccess: " + prayerTiming.getPrayerTimeEngDate());
                        prayerTiming.setCity(city);
                        prayerTiming.setCountry(country);
                        mdDbTask.execute(prayerTiming);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("prayer", "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void fetchFromDatabase(String city,String country,String date){
        mRetrieveTask = new RetrieveTask();
        mRetrieveTask.execute(city + "-" + country + "-" + date);
    }

    public void fetchRecordCountFromDatabase(String cityName, String countryName, String formattedDate) {
        mDbCountTask = new DbCountTask();
        mDbCountTask.execute(cityName + "-" + countryName + "-" + formattedDate);
//        final AtomicInteger atomicInteger = new AtomicInteger();
//        Thread t = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                int count = QuranDatabase.getInstance(getApplication()).quranDao().getRecordCount(cityName, countryName, formattedDate);
//                Log.d("prayer", "run: " + count);
//                atomicInteger.set(count);
//            }
//        });
//        t.start();
//        return atomicInteger.get();
    }

//    public boolean checkIfExists(String city, String country, String date){
//        return QuranDatabase.getInstance(getApplication()).quranDao().isExists(city,country,date);
//    }

    private class DBTask extends AsyncTask<PrayerTiming,Void,Void>{

        @Override
        protected Void doInBackground(PrayerTiming... prayerTimings) {
            PrayerTiming prayerTiming = prayerTimings[0];
            Log.d("prayer", "doInBackground: " + prayerTiming.getCity());
            long result = QuranDatabase.getInstance(getApplication()).quranDao().insert(prayerTiming);
            Log.d("prayer", "doInBackground: " + "result " + result);
            return null;
        }
    }

    private class RetrieveTask extends AsyncTask<String,Void,PrayerTiming>{

        @Override
        protected PrayerTiming doInBackground(String... strings) {
            String city = strings[0].split("-")[0];
            String country = strings[0].split("-")[1];
            String date = strings[0].split("-")[2];
            return QuranDatabase.getInstance(getApplication()).quranDao().getPrayerTimingOfCity(city,country,date);
        }

        @Override
        protected void onPostExecute(PrayerTiming prayerTiming) {
            mTimingMutableLiveData.setValue(prayerTiming);
            if (prayerTiming != null){
                Log.d("prayer", "onPostExecute: " + prayerTiming.getPrayerTimeEngDate());
            }
            else{
                Log.d("prayer", "onPostExecute: " + "prayer timing null");
            }
        }
    }

    private class DbCountTask extends AsyncTask<String,Void,Integer> {
        @Override
        protected Integer doInBackground(String... strings) {
            String city = strings[0].split("-")[0];
            String country = strings[0].split("-")[1];
            String date = strings[0].split("-")[2];
            return QuranDatabase.getInstance(getApplication()).quranDao().getRecordCount(city,country,date);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Toast.makeText(getApplication(), "count" + integer, Toast.LENGTH_SHORT).show();
        }
    }
}
