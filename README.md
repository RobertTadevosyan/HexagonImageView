# HexagonImageView

git clone https://github.com/RobertTadevosyan/HexagonImageView.git  or download zip file (unzip the archive) 

# Steps to import Module in Android Studio 3.3 and lower.

1. Go to File > New > Import Module...
2. Select the source directory of the Module you want to import and click Finish.
3. Open Project Structure and open Module Settings for your project.
4. Open the Dependencies tab.
5. Click the (+) icon and select Module Dependency. Select the module and click Ok.
6. Open your build.gradle file and check that the module is now listed under dependencies.(implementation project(path: ':robertTadevosyan:hexagonImageView')


# Steps to import Module in Android Studio 3.4 and higher (See attached image).

1. Go to File > New > Import Module...
2. Select the source directory of the Module you want to import and click Finish.
3. Open Project Structure Dialog (You can open the PSD by selecting File > Project Structure) and from the left panel click on Dependencies.
4. Select the module from the Module(Middle) section In which you want to add module dependency.
5. Click the (+) icon from the Declared Dependencies section and click Module Dependency.
6. Select the module and click Ok.
7. Open your build.gradle file and check that the module is now listed under dependencies.(implementation project(path: ':ViewPagerIndicator')


![device-2020-08-12-232523](https://user-images.githubusercontent.com/20534636/121426399-35c28400-c97c-11eb-9262-964a841c63c6.png)


    <com.roberttadevosyan.hexagonimageview.HexagonMask              
        android:id="@+id/first_example"
        android:layout_width="200dp"
        android:layout_height="200dp"
        android:layout_marginTop="10dp"
        android:scaleType="centerCrop"
        android:src="@drawable/person_one"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
        
    <com.roberttadevosyan.hexagonimageview.HexagonMask
        android:id="@+id/second_example"
        android:layout_width="150dp"
        android:layout_height="150dp"
        android:layout_marginTop="20dp"
        android:scaleType="centerCrop"
        android:src="@drawable/person_two"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/first_example" />

    <com.roberttadevosyan.hexagonimageview.HexagonMask
        android:id="@+id/third_example"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:layout_marginTop="20dp"
        android:scaleType="centerCrop"
        android:src="@drawable/person_three"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/second_example" />
