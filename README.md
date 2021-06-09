# HexagonImageView

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
