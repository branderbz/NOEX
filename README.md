<p align = "center>
  <img src = "img/logo.png" alt = "Noex Logo" width = "733" height = "441" />
</p>
# NOEX
Noex is a startup developing wearable fitness trackers to provide physical therapy patients instant feedback on their recovery.
##Background
### What are we doing?
We are developing DOT (Data on Therapy), a wearable device designed to passively monitor and track the activity of an individual 
regaining range of motion after injury or surgery. While DOT is worn, it provides the patient with statistics on their range of 
motion progress and gives clinicians hard data on the patient’s activity outside the office. 

### Why do this?
Patients benefit from increased awareness on their recovery and the motivation it offers; clinicians are able to provide more 
effective and specialized care.

## Android Application

The Noex Live Exercice applicaiton is the interface between the DOT device and the user. It guieds the user through a preset workout
presents the data collected by the DOT device. This both ensures that the exercise is done properly and keeps track of the patients 
progress of their recovery.

### Running The Application

####<i> Bluetooth and Sensor Tags </i>
Make sure __Bluetooth__ connectivity and __Sensor Tag__ is enabled on hex devices.<br/>
Also ensure that __Bluetooth__ is activated on your phone.

#### <i>Change Device Addresses:</i>
Change the addresses to your specific devices <br/>

<i> DeviceScanActivity.java</i>
````java
  public static final String KWARP_ADDRESS = "00:39:40:0A:00:07";
  public static final String KWARP_ADDRESS_TWO = "00:48:40:0B:00:2B";
````

### Recuirements
`Minimum SDK: 18 (Android 4.3 Jelly Bean)`<br/>
`Bluetooth Low Energy Connectivity`

### Testing

Using LG Tribute 2 (LG LS665)<br />
Runnig on Android 5.1



## Contributors 
* [Scott Booney](https://www.linkedin.com/in/scottbonney1)
* [Brandon Dryer] (https://www.linkedin.com/in/brandondryer)
* [Dan Zaleski](https://www.linkedin.com/in/daniel-zaleski-769152112)
* [Kaycee Ndukwe](https://www.linkedin.com/in/kaycee-ndukwe-209238127)
* [Ancel Hernandez](https://www.linkedin.com/in/ancelhernandez)
* [Danielle Harrod](https://www.linkedin.com/in/danielle-harrod-79946490)
