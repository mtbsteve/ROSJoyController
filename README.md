
# ROS-Joystick-Controller
## About:
ROS Controller is an open-source Controller App that allows you to use a Logitech/Xbox/Ps4/Joycon or any controller that works with your phone to send output to the ROS node /...../.... This app has been tested with a Logitech 710 controller, connected via an USB Wifi dongle.

![alt text](https://github.com/mtbsteve/ROSJoyController/blob/master/Art/IMG_7374.jpg)

## Getting Started
Download Android Studio 3.2.0 or higher and then use it to build the project files into an .apk file
If you are on your Android device, you can download the ROSJoyController.apk file here.

## Instructions:
- Connect your joystick to the USB port of your Android device and turn the Joystick on.
- Go to Select Controller, select the Logitech 710 icon. Moving the joystick controls should be reflected in the on screen graphics.
- Open ROS Settings and set your ROS_MASTER_URI pointing to the IP of the computer running the ROS master and press connect.\
- On the ROS Master computer, open a terminal window, start roscore, then run rostopic echo /...../....
- The stick movements should be shown in the terminal window.

## Videos
This video below demonstrates and explains how it works:

     
## Thank you
This project is based on the great work from Nathan Ramanathan (https://github.com/nathanRamaNoodles/Tinker-Controller) and the ROS.org team (http://wiki.ros.org/android)
