
# ROS-Joystick-Controller
## About:
ROS Controller is an open-source Controller App that allows you to use a Logitech/Xbox/Ps4 or any controller that works with your phone to send output to the ROS node `/cmd_vel` and/or to `/joy` via the rosbridge protocol. This app has been tested with a Logitech 710 controller, connected via an USB Wifi dongle.

This project supports Android SDK version 21 (Android 5.0) to 29 (including Android 10) and on the server side ROS Kinetic and ROS Melodic. Click image to watch a demo video:

[![Click to watch the video](https://github.com/mtbsteve/ROSJoyController/blob/master/Art/IMG_7374.jpg)](https://youtu.be/Eqjn8VuzvqU)

## Getting Started
Download Android Studio 3.2.0 or higher and then use it to build the project files into an .apk file
If you are on your Android device, you can directly download the ROSJoyController.apk file from here: https://github.com/mtbsteve/ROSJoyController/raw/master/app/release/app-release.apk
On the host computer, you need to have ROS Kinetic or Melodic installed. In addition, you are required to install rosbridge (see http://wiki.ros.org/rosbridge_suite)

## Instructions:
- On the ROS Master computer, open a terminal window, start 
  `roslaunch rosbridge_server rosbridge_websocket.launch`
This opens a websocket connection between the Android device and the ROS Master computer via the port 9090. The port is hard-coded, in case you need to change it edit the variable in the file Gamepad_Joystick_Fragment.java and in the rosbridge launch file.
- Connect your joystick to the USB port of your Android device and turn the Joystick on.
- Go to Select Controller, select the Logitech 710 icon. If the ROS_IP to the master server is set correctly, you will see a "Connected to ROS" message and moving the joystick controls should be reflected in the on screen graphics.
- In case of a connect error, open ROS Settings menu and set your ROS_IP pointing to the IP of the computer running the ROS master and press SET. Go back to the Select Controller menu.
- You can verify the results by entering `rostopic list` on the ROS host. There you should see the topics `/joy` and `/cmd_vel` published.
- Enter `rostopic echo /cmd_vel` and move the sticks. You should see the changes reflected in the values for the linear and angular offsets:
```
linear: 
  x: -0.92156863
  y: -0.32549018
  z: 0.0
angular: 
  x: 0.0
  y: 0.0
  z: 0.0
```
For the /joy topic, the result should look like this:
```
header: 
  seq: 2174
  stamp: 
    secs: 1582024936
    nsecs:        17
  frame_id: ''
axes: [-0.45098036527633667, -0.3490195870399475, 0.8588236570358276, -0.6470588445663452, 0.0, 0.0]
buttons: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
```

## Whats new and currently known restrictions/issues
This project is under development. New stuff includes:
- Implementation of the /joy topic added
- Support of the cmd_vel topic added
- ROS_IP pre-set to apsync companion computers (10.0.1.128)

<b>Known restrictions/issues:</b>
- ROS Melodic to be tested

     
## Thank you
This project is based on the work from Nathan Ramanathan (https://github.com/nathanRamaNoodles/Tinker-Controller) and the ROS.org team (http://wiki.ros.org/android)
