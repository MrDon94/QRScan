## Android上一个识别率极高的二维码扫描库，兼容support版本，主要是针对老旧项目更新麻烦的问题

目前项目已封装界面、相机请求权限、条形码扫描动画、相机预览、识别震动反馈等功能，你只需要按照下面几步即可实现基本功能，如需自定义请自行参考源码修改

### JitPack:
Step 1. Add the JitPack repository in your root build.gradle at the end of repositories:

```gradle
allprojects {
    repositories {
        ...
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

Step 2. Add the dependency

```gradle
dependencies {
    implementation 'com.github.MrDon94:QRScan:v1.0.1'
}
```
## 使用方法

1、启动扫描界面

```java
Intent intent = new Intent(this, QrScanActivity.class);
startActivityForResult(intent, REQUEST_CODE);
```

2、回调接收识别结果

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
        String result = data.getStringExtra(Intents.QR_SCAN_RESULT);
    }
}
```

