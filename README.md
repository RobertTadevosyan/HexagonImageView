# HexagonImageView  - Latest version v1.0.1

To use the module connect via jitpack dependency or dowload adn import as module

To connect via jitpack dependency

add this line  maven { url 'https://jitpack.io' } to your settings.gradle file, inside the block dependencyResolutionManagement { repositories {// here }} ->

```Kotlin

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```

add the dependency to your app level build.gradle file -> 

```Kotlin
def latestVersion = 'v1.0.1'

dependencies {
     implementation 'com.github.RobertTadevosyan:HexagonImageView:$latestVersion'
}
    
```    

1) Simple hexagon shaped image view

![Simple hexagon shaped image view](https://raw.githubusercontent.com/RobertTadevosyan/HexagonImageView/master/one.png)

Code sample:
```XML

    <com.robert.hexagon.image.view.HexagonImageView
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:srcCompat="@drawable/ic_sample" />
        
```


------------------------------------------------------------------------------------------------------------------------------------


2) Hexagon shaped image view with borders

![Hexagon shaped image view with borders](https://raw.githubusercontent.com/RobertTadevosyan/HexagonImageView/master/two.png)

Code sample:
```XML

    <com.robert.hexagon.image.view.HexagonImageView
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_margin="20dp"
        app:borderColor="@color/teal_700"
        app:srcCompat="@drawable/ic_sample"
        app:strokeWidth="10dp" />
        
```




------------------------------------------------------------------------------------------------------------------------------------


3) Hexagon shaped image view with borders as overlay

![Hexagon shaped image view with borders as overlay](https://raw.githubusercontent.com/RobertTadevosyan/HexagonImageView/master/three.png)

Code sample:
```XML

    <com.robert.hexagon.image.view.HexagonImageView
        android:layout_width="200dp"
        android:layout_height="200dp"
        app:backgroundColor="@color/teal_200"
        app:borderColor="@color/teal_700"
        app:borderOverlay="true"
        app:srcCompat="@drawable/ic_sample"
        app:strokeWidth="10dp" />
        
```


