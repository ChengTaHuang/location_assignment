# Demo

![demo1](https://github.com/ChengTaHuang/location_assignment/blob/master/demo/demo1.gif)
![demo2](https://github.com/ChengTaHuang/location_assignment/blob/master/demo/demo2.gif)
* Login Account 
  * UserName : test
  * Password : test1234
  * Country : Singapore
## Tech Stacks
* MVVM architecture - Architecture
* Rxjava - An API for asynchronous programming with observable streams
* Retrofit - A type-safe HTTP client for Android and Java
* Koin - Dependency injection
* Room - An abstraction layer over SQLite to allow for more robust database access while harnessing the full power of SQLite.
* Lootie - Animation
* Unit test - A software testing method by which individual units of source code

## Features
* Checking password format
  * alphanumeric
  * at least 8 characters
* Login Button status
  * username, password and country code should be valid and cannot be empty
* Marker tracking
  * when the marker is focused, change the marker color and zoom in Google map
  * when the marker is clicked, the corresponding user item will turn the gary light into a green light
  * maker location based on longitude and latitude
* User Item
  * when the user item is selected, turn the gary light into a green light
  * when the user item is clicked, Google map will zoom in to the corresponding marker
  * user item display username and phone number
* No network handling
  * show snackbar to try again to get user api
* Rescale icon
  * Re-zoom Google map zoom level
