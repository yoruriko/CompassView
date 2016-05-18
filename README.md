# CompassView
Android Compass Widget

## Example

### Default
![default](https://github.com/yoruriko/CompassView/blob/master/compass_default.png)

#### In Xml
```java
    <com.gospelware.compassviewlib.CompassView
        android:id="@+id/compass"
        android:layout_width="100dp"
        android:layout_height="100dp" />
```

### Costomize
![costomize](https://github.com/yoruriko/CompassView/blob/master/compass_costomize.png)

You can costomize the compass in xml and code:
* circleColor(Default as Black)
* ringColor (Default as White)
* pointerDrawable(Default as circle with RingColor)
* pointerRotation(Default as 0)


#### In Xml
```java
    <com.gospelware.compassviewlib.CompassView
        xmlns:compass="http://schemas.android.com/apk/res-auto"
        android:id="@+id/compass"
        android:layout_width="100dp"
        android:layout_height="100dp"
        compass:circleColor="@color/dark"
        compass:ringColor="@color/red"
        compass:pointerDrawable="@drawable/pointer"
        compass:pointerRotation="0"
        compass:showRing="true" />
```

#### In Code
```java
      CompassView compassView = (CompassView) findViewById(R.id.compass);
      compassView.setCircleColor(getColor(R.color.dark));
      compassView.setRingColor(getColor(R.color.red));
      compassView.setPointerDrawable(R.drawable.pointer);
      compassView.setRotation(0);
```

### Scan Animation

![gif](https://github.com/yoruriko/CompassView/blob/master/compass_gif.gif)

The widget itself contains an Scaning Animation, uses .startScan() and .stopScan() to control the Animation.    
If you want to access the current states of the animation, uses .isScanning() which return weather the animation is running.    
By default the animation rotates clock wise with ringColor.

### Sample
```java
        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (compassView.isScanning()) {
                    compassView.stopScan();
                } else {
                    compassView.startScan();
                }

            }
        });
```
  
## How to?

**Gradle**        

```java
dependencies {    
  compile 'com.gospelware.compassView:compassviewlib:1.0.2'  
}
```
