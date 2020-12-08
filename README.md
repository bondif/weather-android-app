# Weather Forecast Mobile Application and much more 😋

#### Demo Video     
[![Video](https://img.youtube.com/vi/c7r7rQEb1KM/hqdefault.jpg)](https://www.youtube.com/watch?v=c7r7rQEb1KM)

The application is divided into 3 parts :    
### 1. Weather Forecast
The user can search for a city to get its min/max temperature, the pressure and humidity for every 3 hours in the next 4 days.    
Using a chart, the user can also view the wind, the precipitations and temperature of every day in the the next week.    
The app uses [Open Weather Map](https://openweathermap.org/) to get the all weather forecast information mentioned above.    

### 2. Images Search Engine
Using a keyword, the use can look for images he/she wants.    
The app uses [Pixabay API](https://pixabay.com/) to search images.

### 3. Images Gallery
The user can take photos using his/her phone camera (camera permission needed).    
Every time the user takes a photo, the application records the location and store locally (SQLite DB) with the photo.    
Later, the user can view photos (gallery permission needed), and see the location they've been taken on, using Google Maps.    
The application uses [Google Maps API](https://developers.google.com/maps/documentation), and it uses [SQLite](https://www.sqlite.org/) to store the image and its location.

#### More Technical Details
The application uses :


- [Android Volley](https://developer.android.com/training/volley) for HTTP Calls.
- [Picasso](https://square.github.io/picasso/) to render images.
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart) for charts.

Make sure you edit the Google Maps API Key in [app/src/main/AndroidManifest.xml](https://github.com/bondif/weather-android-app/blob/master/app/src/main/AndroidManifest.xml) in order for the Map to work.

If you like it do not hesitate to give it a star ⭐️
