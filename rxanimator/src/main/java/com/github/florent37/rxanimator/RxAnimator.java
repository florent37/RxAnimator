package com.github.florent37.rxanimator;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Property;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;

import static android.animation.ValueAnimator.RESTART;
import static android.animation.ValueAnimator.REVERSE;

/**
 * Created by florentchampigny on 08/03/2017.
 */

public class RxAnimator {

    private static final boolean LOG = BuildConfig.DEBUG;

    public static RxAnimatorObservable ofFloat(View view, String method, float... values) {
        return new RxAnimatorObservable(view, method, values);
    }

    public static RxAnimatorObservable ofFloat(View view, Property viewProperty, float... values) {
        return new RxAnimatorObservable(view, viewProperty, values);
    }

    public static RxAnimatorObservable ofFloat(float... values) {
        return new RxAnimatorObservable(values);
    }

    public static RxAnimatorObservable ofInt(View view, String method, int... values) {
        return new RxAnimatorObservable(view, method, values);
    }

    public static RxAnimatorObservable ofInt(View view, Property viewProperty, int... values) {
        return new RxAnimatorObservable(view, viewProperty, values);
    }

    public static RxAnimatorObservable ofInt(int... values) {
        return new RxAnimatorObservable(values);
    }

    public enum Event {
        START,
        END,
        CANCEL,
        REPEAT,
    }

    @IntDef({RESTART, REVERSE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RepeatMode {
    }

    public static class RxAnimatorObservable extends Observable<Object> {

        private
        @Nullable
        View view;
        private
        @Nullable
        String method;
        private
        @Nullable
        Property viewProperty;
        private
        @Nullable
        float[] valuesFloat;
        private
        @Nullable
        int[] valuesInt;

        private Event event = Event.END;

        @Nullable
        private Long duration;
        @Nullable
        private Long startDelay;
        @Nullable
        private TimeInterpolator timeInterpolator;
        @Nullable
        private TypeEvaluator evaluator;
        @Nullable
        private Integer repeatCount;
        @Nullable
        @RepeatMode
        private Integer repeatMode;
        @Nullable
        private List<ValueAnimator.AnimatorUpdateListener> animatorUpdateListeners;
        @Nullable
        private List<Animator.AnimatorListener> animatorListeners;
        /**
         * The source consumable Observable.
         */
        private final ObservableSource<Object> source = new ObservableSource<Object>() {
            @Override
            public void subscribe(final Observer<? super Object> observer) {
                final ValueAnimator valueAnimator;
                if (view != null) {
                    valueAnimator = new ObjectAnimator();
                    valueAnimator.setTarget(view);
                    if (method != null) {
                        ((ObjectAnimator) valueAnimator).setPropertyName(method);
                    } else if (viewProperty != null) {
                        ((ObjectAnimator) valueAnimator).setProperty(viewProperty);
                    }
                } else {
                    valueAnimator = new ValueAnimator();
                }

                if (valuesInt != null) {
                    valueAnimator.setIntValues(valuesInt);
                } else if(valuesFloat != null){
                    valueAnimator.setFloatValues(valuesFloat);
                }

                if (duration != null) {
                    valueAnimator.setDuration(duration);
                }
                if (startDelay != null) {
                    valueAnimator.setStartDelay(startDelay);
                }
                if (timeInterpolator != null) {
                    valueAnimator.setInterpolator(timeInterpolator);
                }
                if (evaluator != null) {
                    valueAnimator.setEvaluator(evaluator);
                }
                if (repeatCount != null) {
                    valueAnimator.setRepeatCount(repeatCount);
                }
                if (repeatMode != null) {
                    valueAnimator.setRepeatMode(repeatMode);
                }
                if (animatorUpdateListeners != null) {
                    for (ValueAnimator.AnimatorUpdateListener animatorUpdateListener : animatorUpdateListeners) {
                        valueAnimator.addUpdateListener(animatorUpdateListener);
                    }
                }
                valueAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        log("onAnimationStart");
                        if (animatorListeners != null) {
                            for (Animator.AnimatorListener listener : animatorListeners) {
                                listener.onAnimationStart(animation);
                            }
                        }
                        if (event == Event.START) {
                            observer.onNext(animation);
                            observer.onComplete();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        log("onAnimationEnd");
                        if (animatorListeners != null) {
                            for (Animator.AnimatorListener listener : animatorListeners) {
                                listener.onAnimationEnd(animation);
                            }
                        }
                        if (event == Event.END) {
                            log("onAnimationEnd / onNext");
                            observer.onNext(animation);
                            observer.onComplete();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        if (animatorListeners != null) {
                            for (Animator.AnimatorListener listener : animatorListeners) {
                                listener.onAnimationCancel(animation);
                            }
                        }
                        if (event == Event.CANCEL) {
                            observer.onNext(animation);
                            observer.onComplete();
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                        if (animatorListeners != null) {
                            for (Animator.AnimatorListener listener : animatorListeners) {
                                listener.onAnimationRepeat(animation);
                            }
                        }
                        if (event == Event.REPEAT) {
                            observer.onNext(animation);
                            observer.onComplete();
                        }
                    }
                });
                valueAnimator.start();
            }
        };

        RxAnimatorObservable(int[] valuesInt) {
            log("new int[]");
            this.valuesInt = valuesInt;
        }

        RxAnimatorObservable(View view, String method, int[] valuesInt) {
            this(valuesInt);
            this.view = view;
            this.method = method;
        }

        RxAnimatorObservable(View view, Property viewProperty, int[] valuesInt) {
            this(valuesInt);
            this.view = view;
            this.viewProperty = viewProperty;
        }

        RxAnimatorObservable(float[] valuesFloat) {
            log("new float[]");
            this.valuesFloat = valuesFloat;
        }

        RxAnimatorObservable(View view, String method, float[] valuesFloat) {
            this(valuesFloat);
            this.view = view;
            this.method = method;
        }

        RxAnimatorObservable(View view, Property viewProperty, float[] valuesFloat) {
            this(valuesFloat);
            this.view = view;
            this.viewProperty = viewProperty;
        }

        public RxAnimatorObservable animationDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public RxAnimatorObservable animationStartDelay(long startDelay) {
            this.startDelay = startDelay;
            return this;
        }


        public RxAnimatorObservable animationInterpolator(TimeInterpolator timeInterpolator) {
            this.timeInterpolator = timeInterpolator;
            return this;
        }

        public RxAnimatorObservable animationEvaluator(TypeEvaluator evaluator) {
            this.evaluator = evaluator;
            return this;
        }

        public RxAnimatorObservable animationTriggerEvent(Event event) {
            this.event = event;
            return this;
        }

        public RxAnimatorObservable animationRepeatCount(@Nullable int repeatCount) {
            this.repeatCount = repeatCount;
            return this;
        }

        public RxAnimatorObservable animationRepeatMode(@RepeatMode int repeatMode) {
            this.repeatMode = repeatMode;
            return this;
        }

        @Override
        protected void subscribeActual(Observer observer) {
            source.subscribe(observer);
        }

        public RxAnimatorObservable animationAddUpdateListener(ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
            if (this.animatorUpdateListeners == null) {
                this.animatorUpdateListeners = new ArrayList<>();
            }
            this.animatorUpdateListeners.add(animatorUpdateListener);
            return this;
        }

        public RxAnimatorObservable animationAddListener(Animator.AnimatorListener animatorListener) {
            if (this.animatorListeners == null) {
                this.animatorListeners = new ArrayList<>();
            }
            this.animatorListeners.add(animatorListener);
            return this;
        }
    }

    private static void log(String text){
        if(LOG){
            Log.d("RxAnimator", text);
        }
    }

}
