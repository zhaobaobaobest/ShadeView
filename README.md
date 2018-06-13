# ShadeView
仿QQ空间的广告动画效果


### 使用说明
    下载项目，引入 shadeview依赖包，引入自定义控件
    <com.ct.shadeview.ShadeImageView
       android:id="@+id/iv2"
       android:layout_width="match_parent"
       android:layout_height="match_parent"
       android:src="@drawable/test"
     />    
    如果需要背景形成两广告图片的切换，可以如下布局：
    <RelativeLayout
    android:layout_width="match_parent"
    android:layout_height="240dp">
    <!--背景广告放在前面-->
    <ImageView
        android:id="@+id/iv1"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:scaleType="centerCrop"
        android:src="@drawable/test2"
        />
    <com.ct.shadeview.ShadeImageView
        android:id="@+id/iv2"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:src="@drawable/test"
        />

    </RelativeLayout>    
    具体使用可以见demo.
    效果图如下：
