package com.ROSJoyController.fragments;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.ROSJoyController.R;
import com.ROSJoyController.Utility;
import com.ROSJoyController.activities.GamepadActivity;
import com.ROSJoyController.ClientStatus;
import com.jaredrummler.cyanea.app.CyaneaFragment;
import com.xbw.ros.ROSClient;
import com.xbw.ros.rosbridge.ROSBridgeClient;

import androidx.annotation.RequiresApi;
import androidx.core.widget.ImageViewCompat;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.ROSJoyController.BuildConfig.DEBUG;


public class Gamepad_Joystick_Fragment extends CyaneaFragment implements GamepadActivity.JoystickListener {
    //Tag for Logging
    private final String TAG = "myTag";

    private Toast mToast;
    private ClientStatus mClientStatus;

    private Unbinder unbinder;
    private ROSBridgeClient client;
    private float[] joy_axes = {(float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0, (float) 0.0};
    private int[] joy_buttons = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private int sequence_ID = 0;
    private long time_stamp_sec;
    private int timestamp_nsec=0;
    private long time_stamp_prev;


    public void setRosClient(ROSBridgeClient client) {
        this.client = client;
    }


    //right joystick
    @BindView(R.id.right_outer_edge_joystick)
    ImageView mJoystickRight;
    @BindView(R.id.right_inner_edge_joystick)
    ImageView mJoystickRight_inner;
    @BindView(R.id.right_outline_joystick)
    ImageView mJoystickRight_outline;

    //Left joystick
    @BindView(R.id.left_outer_edge_joystick)
    ImageView mJoystickLeft;
    @BindView(R.id.left_inner_edge_joystick)
    ImageView mJoystickLeft_inner;
    @BindView(R.id.left_outline_joystick)
    ImageView mJoystickLeft_outline;

    //Back Button
    @BindView(R.id.button_back)
    ImageView mBackButton;

    //Select/Menu Button
    @BindView(R.id.button_menu)
    ImageView mMenuButton;

    //X Button
    @BindView(R.id.button_left)
    ImageView mButtonX;
    //Y Button
    @BindView(R.id.button_up)
    ImageView mButtonY;
    //B Button
    @BindView(R.id.button_right)
    ImageView mButtonB;
    //A Button
    @BindView(R.id.button_down)
    ImageView mButtonA;

    //D-Pad left
    @BindView(R.id.dPad_left)
    ImageView mDpadLeft;
    //D-Pad Up
    @BindView(R.id.dPad_up)
    ImageView mDpadUp;
    //D-Pad Right
    @BindView(R.id.dPad_right)
    ImageView mDpadRight;
    //D-Pad Down
    @BindView(R.id.dPad_down)
    ImageView mDpadDown;

    //Left Trigger
    @BindView(R.id.trigger_left)
    ImageView mTriggerLeft;
    //Right Trigger
    @BindView(R.id.trigger_right)
    ImageView mTriggerRight;
    //Analog Left trigger
    @BindView(R.id.analog_trigger_left)
    ImageView mAnalogTriggerLeft;
    //Analog Right trigger
    @BindView(R.id.analog_trigger_right)
    ImageView mAnalogTriggerRight;

    private int[] joystickLeft_CenterLocation = new int[6];
    private int[] joystickRight_CenterLocation = new int[6];
    private int dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_CENTER;
    private int brightStandOutColor = Utility.getComplimentColor(getCyanea().getAccent());

    private String connected_to_ros_message = "";
    private boolean connected_to_ros = false;
    private String message;
    EditText mEdit;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mClientStatus = (ClientStatus) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement MyInterface ");
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_gamepad_joystick, container, false);
        unbinder = ButterKnife.bind(this, v);
        v.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onGlobalLayout() {
                        // Layout has happened here.
                        //Fetch our initial center coordinates for our joysticks
                        joystickLeft_CenterLocation[0] = mJoystickLeft.getLeft();
                        joystickLeft_CenterLocation[1] = mJoystickLeft.getTop();
                        joystickLeft_CenterLocation[2] = mJoystickLeft_outline.getLeft();
                        joystickLeft_CenterLocation[3] = mJoystickLeft_outline.getTop();
                        joystickLeft_CenterLocation[4] = mJoystickLeft_inner.getLeft();
                        joystickLeft_CenterLocation[5] = mJoystickLeft_inner.getTop();

                        joystickRight_CenterLocation[0] = mJoystickRight.getLeft();
                        joystickRight_CenterLocation[1] = mJoystickRight.getTop();
                        joystickRight_CenterLocation[2] = mJoystickRight_outline.getLeft();
                        joystickRight_CenterLocation[3] = mJoystickRight_outline.getTop();
                        joystickRight_CenterLocation[4] = mJoystickRight_inner.getLeft();
                        joystickRight_CenterLocation[5] = mJoystickRight_inner.getTop();

                        // Don't forget to remove your listener when you are done with it.
                        if (Build.VERSION.SDK_INT < 16) {
                            v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });

        //Setup up our interface listeners
        ((GamepadActivity) getActivity()).setJoystickListener(this);

        // try to connect to the ROS server and check for the ROS_IP to the server
        if (ROS_Settings_Fragment.ROS_IP == "") {
            //we use the default ROS_IP address in the template in case it has not been set by the user interactively
            View x = inflater.inflate(R.layout.fragment_ros_settings, container, false);
            mEdit = x.findViewById(R.id.rosuri);
            message = mEdit.getText().toString();
        } else {
            // otherwise we use the IP address entered by the user in ROS Settings
            message = ROS_Settings_Fragment.ROS_IP;
        }

        if (ROS_Settings_Fragment.ROS_PORT == "") {
            //we use the default port address in the template in case it has not been set by the user interactively
            View x = inflater.inflate(R.layout.fragment_ros_settings, container, false);
            mEdit = x.findViewById(R.id.editText2);
            message = message + ":" + mEdit.getText().toString();
        } else {
            // otherwise we use the port address entered by the user in ROS Settings
            message = message + ":" + ROS_Settings_Fragment.ROS_PORT;
        }

        // we try to establish the connection to the ROSBridge server
        message = "ws://" + message;
        ROSBridgeClient client = new ROSBridgeClient(message);
        client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(true);
                setRosClient(client);
                onLoginSuccess();
            }
            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                String errmsg;
                if (normal == true) {
                    errmsg = "ROS Server disconnected";
                } else {
                   errmsg = "ROS Error: " + reason;
                }
                onLoginFailed(errmsg);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                //onLoginFailed("ROS communication error" + ex);
            }
        });

        // prompt the connection status  on the screen
        sayToast(connected_to_ros_message);

        return v;
    }

    private void onLoginSuccess() {
        // we advertise the ROS topic node we want to use for the Joystick (cmd_vel supports only the analog axis movements,
        // the joy node is the "real" ROS topic to publish all joystick features)
        Advertise("/cmd_vel", "geometry_msgs/Twist");
        Advertise("/joy", "sensor_msgs/Joy");
        connected_to_ros =true;
        connected_to_ros_message = "Connected to ROS Server: " + message;
    }

    private void onLoginFailed(String message) {
        connected_to_ros =false;
        connected_to_ros_message = message;
    }

    private void Advertise(String cmd, String type) {
        String toSend = "{\"op\":\"advertise\",\"topic\":\""+ cmd +"\",\"type\":\"" + type + "\"}";
        client.send(toSend);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.i(TAG, "onSaveInstanceState(Bundle)");
        super.onSaveInstanceState(outState);
        //outState.putBoolean(CONNECTED_TO_WIFI, mAsyncFragment.isConnected());
    }


    private void sayToast(String message) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getActivity(), message, Toast.LENGTH_LONG);
        mToast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // disconnect from ROS Server
        if (connected_to_ros == true) {
            client.disconnect();
            sayToast(connected_to_ros_message);
        }
        unbinder.unbind();
    }

    @Override
    public void onJoystick(float[] joystickData) {
        float translationX = Utility.mapFloat(joystickData[0], -1, 1, -40, 40);
        float translationY = Utility.mapFloat(joystickData[1], -1, 1, -40, 40);
        mJoystickLeft.setX(translationX + joystickLeft_CenterLocation[0]);
        mJoystickLeft_outline.setX(translationX + joystickLeft_CenterLocation[2]);
        mJoystickLeft_inner.setX(translationX + joystickLeft_CenterLocation[4]);

        mJoystickLeft.setY(translationY + joystickLeft_CenterLocation[1]);
        mJoystickLeft_outline.setY(translationY + joystickLeft_CenterLocation[3]);
        mJoystickLeft_inner.setY(translationY + joystickLeft_CenterLocation[5]);

        translationX = Utility.mapFloat(joystickData[2], -1, 1, -40, 40);
        translationY = Utility.mapFloat(joystickData[3], -1, 1, -40, 40);
        mJoystickRight.setX(translationX + joystickRight_CenterLocation[0]);
        mJoystickRight_outline.setX(translationX + joystickRight_CenterLocation[2]);
        mJoystickRight_inner.setX(translationX + joystickRight_CenterLocation[4]);

        mJoystickRight.setY(translationY + joystickRight_CenterLocation[1]);
        mJoystickRight_outline.setY(translationY + joystickRight_CenterLocation[3]);
        mJoystickRight_inner.setY(translationY + joystickRight_CenterLocation[5]);

        animateAnalogTriggers(mAnalogTriggerLeft, joystickData[4]);
        animateAnalogTriggers(mAnalogTriggerRight, joystickData[5]);

        // we go for values between -1 and 1 for all 4 joystick axes supported by the Logitech 710. Axes q, r are not supported but left in.
        joy_axes[0] = Utility.mapFloat(joystickData[0], -1, 1, -1, 1);
        joy_axes[1] = Utility.mapFloat(joystickData[1], -1, 1, -1, 1);
        joy_axes[2] = Utility.mapFloat(joystickData[2], -1, 1, -1, 1);
        joy_axes[3] = Utility.mapFloat(joystickData[3], -1, 1, -1, 1);

        //send joystick movements to ROSBridge
        if(connected_to_ros) {
            try {
                sendtoROSServer();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean onButton(int buttonPress, boolean isPressed) {
        boolean handled = true;
        switch (buttonPress) {
            case KeyEvent.KEYCODE_BACK:
                sayToast("Back Button");
                changeImageViewColor(mBackButton, isPressed);
                joy_buttons[8]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_START:
                sayToast("Start/Menu Button");
                changeImageViewColor(mMenuButton, isPressed);
                joy_buttons[9]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                sayToast("X Button");
                changeImageViewColor(mButtonX, isPressed);
                joy_buttons[0]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_Y:
                sayToast("Y Button");
                changeImageViewColor(mButtonY, isPressed);
                joy_buttons[3]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                sayToast("B Button");
                changeImageViewColor(mButtonB, isPressed);
                joy_buttons[2]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_A:
                sayToast("A Button");
                changeImageViewColor(mButtonA, isPressed);
                joy_buttons[1]=(isPressed ? 1 : 0);
                break;


            case KeyEvent.KEYCODE_BUTTON_THUMBL:
                sayToast("Left Joystick Button");
                changeImageViewColor(mJoystickLeft, isPressed);
                changeImageViewColor(mJoystickLeft_inner, isPressed);
                joy_buttons[10]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_THUMBR:
                sayToast("Right Joystick Button");
                changeImageViewColor(mJoystickRight, isPressed);
                changeImageViewColor(mJoystickRight_inner, isPressed);
                joy_buttons[11]=(isPressed ? 1 : 0);
                break;


            case KeyEvent.KEYCODE_BUTTON_L1:
                sayToast("Left Trigger");
                changeImageViewColor(mTriggerLeft, isPressed);
                joy_buttons[4]=(isPressed ? 1 : 0);

                break;
            case KeyEvent.KEYCODE_BUTTON_R1:
                sayToast("Right Trigger");
                changeImageViewColor(mTriggerRight, isPressed);
                joy_buttons[5]=(isPressed ? 1 : 0);
                break;
            case KeyEvent.KEYCODE_BUTTON_L2:
                sayToast("2nd Left Trigger");
                changeImageViewColor(mAnalogTriggerLeft, isPressed);
                joy_buttons[6]=(isPressed ? 1 : 0);

                break;
            case KeyEvent.KEYCODE_BUTTON_R2:
                sayToast("2nd Right Trigger");
                changeImageViewColor(mAnalogTriggerRight, isPressed);
                joy_buttons[7]=(isPressed ? 1 : 0);
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:
                sayToast("D-pad Center");
                clearPreviousDpad();
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_CENTER;
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_CENTER;
                joy_axes[4]=0;
                joy_axes[5]=0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                clearPreviousDpad();
                changeImageViewColor(mDpadLeft, isPressed);
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_LEFT;
                joy_axes[4]=-1;
                sayToast("D-pad Left");
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                clearPreviousDpad();
                changeImageViewColor(mDpadUp, isPressed);
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_UP;
                joy_axes[5]=1;
                sayToast("D-pad Up");
                break;

            case KeyEvent.KEYCODE_DPAD_RIGHT:
                clearPreviousDpad();
                changeImageViewColor(mDpadRight, isPressed);
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_RIGHT;
                joy_axes[4]=1;
                sayToast("D-pad Right");
                break;

            case KeyEvent.KEYCODE_DPAD_DOWN:
                clearPreviousDpad();
                changeImageViewColor(mDpadDown, isPressed);
                dPadCurrentLocation = KeyEvent.KEYCODE_DPAD_DOWN;
                joy_axes[5]=-1;
                sayToast("D-pad Down");
                break;
            default:
                handled = false;
        }
        if(connected_to_ros && handled) {
            try {
                sendtoROSServer();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return handled;
    }


    private void sendtoROSServer() throws JSONException {
        sequence_ID=sequence_ID+1;
        long time_stamp = System.currentTimeMillis();

        timestamp_nsec = (int) (time_stamp - time_stamp_prev);
        time_stamp_prev = time_stamp;
        time_stamp_sec = time_stamp/1000;
        //build the json array for the axes and buttons
        JSONArray json_joy_axes= new JSONArray();
        for (int i = 0; i < joy_axes.length; i++) {
            json_joy_axes.put(joy_axes[i]);
        }
        JSONArray json_joy_buttons= new JSONArray();
        for (int i = 0; i < joy_buttons.length; i++) {
            json_joy_buttons.put(joy_buttons[i]);
        }

        //send the movements to /joy_node
        String joynode = "{\"op\":\"publish\",\"topic\":\"/joy\",\"msg\":{\"header\":{\"seq\":" + sequence_ID + ",\"stamp\":{\"secs\":" + time_stamp_sec + ",\"nsecs\":" + timestamp_nsec + "},\"frame_id\":\"\"},\"axes\":" + json_joy_axes +",\"buttons\":" + json_joy_buttons + "}}";
        client.send(joynode);
        //send the movements to /cmd_vel_node
        String cmdvel = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + joy_axes[0] + ",\"y\":" + joy_axes[1] + ",\"z\":" + joy_axes[3] + "},\"angular\":{\"x\":" + joy_axes[4] + ",\"y\":" + joy_axes[5] + ",\"z\":" + joy_axes[2] + "}}}";
        client.send(cmdvel);
    }

    private void clearPreviousDpad() {
        switch (dPadCurrentLocation) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                changeImageViewColor(mDpadLeft, false);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                changeImageViewColor(mDpadUp, false);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                changeImageViewColor(mDpadRight, false);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                changeImageViewColor(mDpadDown, false);
                break;
        }
    }

    private void animateAnalogTriggers(final ImageView img, float ratio) {
        int colorTo = brightStandOutColor;
        int colorFrom = getCyanea().getPrimaryDark();

        int red = (int) Math.abs((ratio * Color.red(colorTo)) + ((1 - ratio) * Color.red(colorFrom)));
        int green = (int) Math.abs((ratio * Color.green(colorTo)) + ((1 - ratio) * Color.green(colorFrom)));
        int blue = (int) Math.abs((ratio * Color.blue(colorTo)) + ((1 - ratio) * Color.blue(colorFrom)));


        int transparentColor = Color.rgb(red, green, blue);
        ImageViewCompat.setImageTintList(img, ColorStateList.valueOf(transparentColor));

    }

    public void changeImageViewColor(ImageView img, boolean isPressed) {
        ImageViewCompat.setImageTintList(img, ColorStateList.valueOf((isPressed) ? brightStandOutColor : getCyanea().getPrimaryDark()));
    }
}
