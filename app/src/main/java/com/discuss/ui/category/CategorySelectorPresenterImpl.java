package com.discuss.ui.category;

import android.util.Log;

import com.discuss.data.DataFetcher;
import com.discuss.data.impl.DataFetcherImpl;
import com.discuss.datatypes.Category;
import com.discuss.datatypes.Question;
import com.discuss.datatypes.User;
import com.discuss.datatypes.UserCategoryPreference;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class CategorySelectorPresenterImpl implements CategorySelectorPresenter {
    private final DataFetcher dataFetcher;
    private List<UserCategoryPreference> userCategoryPreferences;
    private int limit;
    private volatile boolean isLoading;
    private Observable<List<UserCategoryPreference>> questionObservable;
    private final ReentrantLock lock = new ReentrantLock();

    @Inject
    public CategorySelectorPresenterImpl(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    private void checkPreConditions() {
        if (null == dataFetcher || null == userCategoryPreferences) {
            init(onCompleted);
        }
    }

    private void setQuestionObservableAndSubscribeForFirstSubscriber() {
        questionObservable = dataFetcher.   /* hot observable */
                getUserCategoryPreference(""). /* TODO(Deepak): add proper values */
                onBackpressureBuffer().
                subscribeOn(Schedulers.io()).
                publish().
                refCount().
                observeOn(AndroidSchedulers.mainThread());
        questionObservable.subscribe(onNextQuestionsList, onError, (() -> {
            synchronized (lock) {
                isLoading = false;
            }
        }));
    }

    private final Action1<List<UserCategoryPreference>> onNextQuestionsList = new Action1<List<UserCategoryPreference>>() {
        @Override
        public void call(List<UserCategoryPreference> fetchedQuestions) {
            userCategoryPreferences.addAll(fetchedQuestions);
        }
    };

    private final Action1<Throwable> onError = throwable -> {};

    private final Action0 onCompleted = () -> {};


    @Override
    public void init(Action0 onCompletedAction) {
        userCategoryPreferences = new CopyOnWriteArrayList<>(); /* update operations are in bulk and not to often to degrade the performance  */
        limit = 10;
        synchronized (lock) {
            if (!isLoading) {
                isLoading = true;
                setQuestionObservableAndSubscribeForFirstSubscriber();
            }
            questionObservable.subscribe((a) -> {}, (a) ->{}, onCompletedAction);
        }
    }

    @Override
    public Observable<Boolean> refresh() {
        init(() -> {});
        return Observable.just(true);
    }

    @Override
    public Observable<UserCategoryPreference> get(int position) {
        return Observable.just(userCategoryPreferences.get(position));

    }

    @Override
    public int size() {
        return (null == userCategoryPreferences) ? 0 : userCategoryPreferences.size();
    }
}
