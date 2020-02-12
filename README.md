
# ROS-Joystick-Controller
## About:
ROS Controller is an open-source Controller App that allows you to use a Logitech/Xbox/Ps4 or any controller that works with your phone to send output to the ROS node /cmd_vel and/or to /joy via the rosbridge protocol. This app has been tested with a Logitech 710 controller, connected via an USB Wifi dongle.

![alt text](https://github.com/mtbsteve/ROSJoyController/blob/master/Art/IMG_7374.jpg)

## Getting Started
Download Android Studio 3.2.0 or higher and then use it to build the project files into an .apk file
If you are on your Android device, you can directly download the ROSJoyController.apk file.

## Instructions:
- On the ROS Master computer, open a terminal window, start `roslaunch rosbridge_server rosbridge_websocket.launch`
This opens a websocket connection between the Android devicd and the ROS Master computer via the port 9090.
- Connect your joystick to the USB port of your Android device and turn the Joystick on.
- Go to Select Controller, select the Logitech 710 icon. If the ROS_IP to the master server is set correctly, you will see a "Connected to ROS" message and moving the joystick controls should be reflected in the on screen graphics.
- In case of a connect error, open ROS Settings menu and set your ROS_IP pointing to the IP of the computer running the ROS master and press SET. Go back to the Select Controller menu.
- You can verify the results by entering rostopic list. There you should see the topics /joy and /cmd_vel published.
- Enter rostopic echo /cmd_vel and move the sticks. You should see the changes reflected in the values for the linear and angular offsets:
```
linear: 
  x: 0.0
  y: 0.37254906
  z: 0.0
angular: 
  x: 227.5
  y: 227.5
  z: 0.0
---
```
## Videos
This video below demonstrates and explains how it works:

     
## Thank you
This project is based on the work from Nathan Ramanathan (https://github.com/nathanRamaNoodles/Tinker-Controller) and the ROS.org team (http://wiki.ros.org/android)
