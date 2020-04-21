# AndroidUtilLib
Utils commonly used in android development

## Installation Adding to project
1.Add the Jcenter repository to your build file
```
buildscript {
    repositories {
        jcenter()
    }
}
```
2.Add the dependency
```
dependencies {
    implementation 'gapp.season:SeasonUtil:0.0.5'
}
```

## Usage
initialize when Application.onCreate(), then use each util.
```
SeasonUtil.init(application, BuildConfig.DEBUG); //init

//utils

AppUtil
ActivityHolder

FileShareUtil
FileUtil
ImgUtil
ZipUtil
MediaScanUtil
FileTypeUtil
MimeTypes

LogUtil

NetworkUtil
CookieUtil
HttpSimpleUtil

ClipboardUtil
DeviceUtil
KeyBoardUtil
MemoryUtil
ScreenUtil

ThreadPoolExecutor
OnTaskCode
OnTaskDone
OnTaskValue

StringUtil
Checker
DecimalUtil
DateUtil

ToastUtil
AlertUtil

ThemeUtil
GestureDetectorUtil

SimpleTextWatcher
SimpleOnPageChangeListener
SimpleAnimationListener
```
