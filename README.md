# ACalendarView
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
}
  

dependencies {
	        implementation 'com.github.ausnrl:ACalendarView:Tag'
}
```  


Use it
```
setDate(year, month, startDay)
```  
- example : setDate(2021, 0, 1)  
![setDate](https://user-images.githubusercontent.com/77829897/106912281-3933cd80-6746-11eb-9075-56cdb33e70ea.jpg)

```  
callback{ // click date  
  i, j->  
  Log.d("CAL", "year : ${getYear(i, j)} , month : ${getMonth(i, j)} , day : ${getDay(i, j)}") // click get year, month, day  
}
```

```
drawAdd(DrawPosition().drawImage(DrawPosition.StartDraw.UNDER_DATE, YMD(2021, 1, 1), R.drawable.ic_sample,0f, 0f, true))  
```  
![drawAdd_image](https://user-images.githubusercontent.com/77829897/106912297-3e911800-6746-11eb-91ef-2cdf196a9333.jpg)

```
drawAdd(DrawPosition().drawText(DrawPosition.StartDraw.UNDER_DATE, IJ(i, j), "Hello", 0f, 0f, 30))  
drawAdd(DrawPosition().drawText(DrawPosition.StartDraw.UNDER_DATE, IJ(i, j), "World!!", 0f, 30f, 30))  
```  
![click](https://user-images.githubusercontent.com/77829897/106912313-418c0880-6746-11eb-8235-a8b82abbc76c.gif)


Setting
```
setAutoTextSize(bool)                 - base text size => true -> Automatic text size calculation  
setDayOfWeekTextSize(int)             - autoSize : true -> this value  
setDayOfMonthTextSize(int)            - autoSize : true -> this value  
setLineColor(color)                   - horizontal and vertical line color  
setVerticalLineColor(color)           - only vertical line color  
setHorizontalLineColor(color)         - only horizontal line color  
setDayOfMonthTextColor(color)         - date text color  
setDayOfMonthSatTextColor(color)      - date saturday text color  
setDayOfMonthSunTextColor(color)      - date sunday text color  
setHorizontalLineVisibility(bool)     - true -> visible, false -> gone  
setVerticalLineVisibility(bool)       - true -> visible, false -> gone  
setDayOfWeekTextColor(color)          - day of the week text color  
setDayOfWeekSatTextColor(color)       - saturday text color  
setDayOfWeekSunTextColor(color)       - sunday text color  
setDateCenter(bool)                   - sort date => true -> center top, false -> left top  
setLastMonthVisible(bool)             - visible past month  
```
