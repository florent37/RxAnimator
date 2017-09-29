# RxAnimator

An RxJAva2 binding for android Animator

<a href="https://play.google.com/store/apps/details?id=com.github.florent37.florent.champigny">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>

# Run sequencially

```java
RxAnimator.ofFloat(view, "translationY", 0, 300).animationDuration(500)
          .flatMap(anim -> RxAnimator.ofFloat(view, "translationX", 0, 300))
          .subscribe(anim -> {});
```

`RxAnimator` contains the same methods as ValueAnimator`

`.animationDuration(long)`

`.animationStartDelay(long)`

`.animationInterpolator(interpolator)`

`.animationEvaluator(evaluator)`

...

# Run in parallel

(Using RxBindings for the click observable)

```java
RxView.clicks(view)
          .flatMap(animation ->
                  Observable.zip(
                          RxAnimator.ofFloat(view, "scaleX", 5),
                          RxAnimator.ofFloat(view, "scaleY", 5),
                          RxAnimator.ofFloat(view, "translationY", 300),
                          RxAnimator.ofFloat(view, "translationX", 200),
                          (a, a2, a3, a4) -> a
                  )
          )
          .flatMap(animation ->
                  Observable.zip(
                          RxAnimator.ofFloat(view, "scaleX", 1),
                          RxAnimator.ofFloat(view, "scaleY", 1),
                          RxAnimator.ofFloat(view, "translationX", 0),
                          RxAnimator.ofFloat(view, "translationY", 0),
                          (a, a2, a3, a4) -> a
                  )
          )
          .subscribe(animation -> {});
```

# Trigger event

By default the observable submits when the animator ends
You can configure it using `.animationTriggerEvent(Event.START / Event.END / Event.CANCEL / EVENT.RESTART)`

# Download

<a href='https://ko-fi.com/A160LCC' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi1.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

In your module [![Download](https://api.bintray.com/packages/florent37/maven/RxAnimator/images/download.svg)](https://bintray.com/florent37/maven/RxAnimator/_latestVersion)
```groovy
compile 'com.github.florent37:rxanimator:1.0.0'
```

# Credits

Author: Florent Champigny

<a href="https://play.google.com/store/apps/details?id=com.github.florent37.florent.champigny">
  <img alt="Android app on Google Play" src="https://developer.android.com/images/brand/en_app_rgb_wo_45.png" />
</a>
<a href="https://plus.google.com/+florentchampigny">
  <img alt="Follow me on Google+"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/gplus.png" />
</a>
<a href="https://twitter.com/florent_champ">
  <img alt="Follow me on Twitter"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/twitter.png" />
</a>
<a href="https://fr.linkedin.com/in/florentchampigny">
  <img alt="Follow me on LinkedIn"
       src="https://raw.githubusercontent.com/florent37/DaVinci/master/mobile/src/main/res/drawable-hdpi/linkedin.png" />
</a>

#License

    Copyright 2017 florent37, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
