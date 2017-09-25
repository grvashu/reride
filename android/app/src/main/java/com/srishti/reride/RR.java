package com.srishti.reride;

import processing.core.*;

import android.content.Context;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class RR extends PApplet {


    // Min-Max left/right tilt in degrees
    final int MIN_TILT = -60;
    final int MAX_TILT = 60;

    // Min-Max readings of FSRs
    final int MAX_FSR = 1023;
    final int MIN_FSR = 0;

    // Min-Max tilt of trapezium in pixels
    final int MIN_DISP_TILT = -200;
    final int MAX_DISP_TILT = 200;

    // Trapezium offset from bottom edge of the screen
    final int TRAP_OFFSET = 300;

    // Trail of Trapezium (0-100)
    final int TRAIL_FACTOR = 0;

    // Declaring RR instance
    ReRide r;

    int acc;
    int push;
    int fsr;

    /*
    Initialization Status
    0: Loading and starting the application
    1: Checking Bluetooth Connection
    2: Checking Internet Connection
    3: Establish connection with AWS
    4: Test Lambda Fetch
    5: Test Lambda Push
    6: Start streaming
    */

    int init_status;
    // Declare Android instances
    Context context;
    SensorManager manager;
    Sensor sensor;
    AccelerometerListener listener;
    // Accelerometer Data will be fed into these variables -- can be accessed globally
    float ax, ay, az;

    public void setup() {


        // Fetch display resolution from mobile
        // Uncomment below line and remove the line after

        // size(600, 300);

        // Initializing the RR instance
        r = new ReRide();

        // Initial App Load Status
        init_status = 0;

        // Create neccessary Android instances if simulating through Android
        createAndroidInstance();

        // Test AWS stream -- at reride_push HTTP endpoint
        // streamData(502, 100, 200, 10, 20, 30, 1);



    }

    public void draw() {


        // Initialize Display
        if (init_status < 3) {
            init_status = r.init();
        }

        // Start Feedback
        else {

            // Clear previous plot (if any)
            r.updateBackground();

            // Fetch recent data from AWS
            r.getData();

            // Draw instantaneous feedback
            r.abstractPosture();

            // Draw ride history feedback
            r.timeView();

            // Combine both feedback
            r.update();
        }

        if (push==1){
            background(255,0,0);
        }else{
            background(0);
        }
    }

    public void mousePressed() {

        // Cycle display mode
        r.changeMode();
    }

    private void streamData(int id, int fsr_left, int fsr_right, int acc_x, int acc_y, int acc_z, int flag) {
        try {
            PostRequest post = new PostRequest("https://ougkqprxzc.execute-api.us-west-2.amazonaws.com/prod/reride_push");
            String json =
                    "{" +
                            "\"id\":\"" + str(id) + "\"," +
                            "\"fsr_left\":\"" + str(fsr_left) + "\"," +
                            "\"fsr_right\":\"" + str(fsr_right) + "\"," +
                            "\"acc_x\":\"" + str(acc_x) + "\"," +
                            "\"acc_y\":\"" + str(acc_y) + "\"," +
                            "\"acc_z\":\"" + str(acc_z) + "\"," +
                            "\"flag\":\"" + str(flag) + "\"" +
                            "}";
            post.addHeader("ContentType", "application/json");
            post.addJson(json);
            post.send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //text("Response Content: " + post.getContent(), 0, 0);
    }

    public void createAndroidInstance() {
        context = getActivity();
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new AccelerometerListener();
        manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
    }

    public void settings() {
        fullScreen();
        smooth(4);
    }

    class AccelerometerListener implements SensorEventListener {

        // This code will be trigged automatically upon the change of sensor value -- DO NOT CALL EXPLICITLY
        public void onSensorChanged(SensorEvent event) {
            ax = event.values[0];
            ay = event.values[1];
            az = event.values[2];
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }

    // Data Packet
    class Data {

        int acc1;
        int push1;
        int fsr1;

        //int id;
        // FSR Values
        float fsr_left;
        float fsr_right;

        // Helmet Accelerometer values
        float acc_x;
        float acc_y;
        float acc_z;

        // Timestamp as dd-mm-yy-hh-mm-ss-ms
        long timestamp;

        // Push button (1/0)
        int flag;

        // Constructor
        Data() {
        }

        // Get Tilt from FSR data
        float tilt() {

            //Get simulation data from mouse movement
//            if (fsr_left>fsr_right) {
//              return map(fsr_left-fsr_right, MIN_FSR, MAX_FSR, MIN_TILT, MAX_TILT);
//            } else {
//              return map(fsr_right-fsr_left, MIN_FSR, MAX_FSR, MIN_TILT, MAX_TILT);
//            }
//
            // Get simulation data from phone tilt along y axis
            //return map(ay, 5, -5, MIN_TILT, MAX_TILT);

            return fsr1;
        }

        // Get Lateral Tilt from Accelerometer data
        float lateral_pos() {
            // Get lateral posture angle from mouse movement
            // return map(mouseY, 0, height, -100, 100);

            // Get simulation data from phone tilt along y axis
            // return map(az, 5, -5, 100, -400);
            return map(acc1,0,200,100,-400);
        }

        // Get Random data for simulation
        void getRandom() {

            fsr_left = map(random(width), 0, width, MIN_FSR, MAX_FSR);
            fsr_right = map(random(width), width, 0, MIN_FSR, MAX_FSR);
            acc_x = 10 * noise(frameCount);
            acc_y = 10 * noise((float) (frameCount * 1.232));
            acc_z = 10 * noise((float) (frameCount * 1.202));
        }

        // Get Data from BLE peripherals
        // Get Data from BLE --> Send to RR class for display --> Stream data to AWS --> Data is fetch on another device/app post-ride
        void getData() {
            // BLE code
            acc1=acc;
            push1=push;
            fsr1=fsr;
            Log.i("Data fetched","acc:"+acc1+",push:"+push+",fsr:"+fsr);
        }

        // Parse JSON and load data into variables
        void parseJSON() {
            // Parse JSON into discrete values
        }

        // Stream data to AWS
        void streamData(int id) {
            try {
                PostRequest post = new PostRequest("https://ougkqprxzc.execute-api.us-west-2.amazonaws.com/prod/reride_push");
                String json =
                        "{" +
                                "\"id\":\"" + str(id) + "\"," +
                                "\"fsr_left\":\"" + str(fsr_left) + "\"," +
                                "\"fsr_right\":\"" + str(fsr_right) + "\"," +
                                "\"acc_x\":\"" + str(acc_x) + "\"," +
                                "\"acc_y\":\"" + str(acc_y) + "\"," +
                                "\"acc_z\":\"" + str(acc_z) + "\"," +
                                "\"flag\":\"" + str(flag) + "\"" +
                                "}";
                post.addHeader("ContentType", "application/json");
                post.addJson(json);
                post.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //text("Response Content: " + post.getContent(), 0, 0);
        }

        // Fetch data from AWS
        // For Dashboard Program only
        void fetchData(int id) {
            try {
                PostRequest post = new PostRequest("https://ougkqprxzc.execute-api.us-west-2.amazonaws.com/prod/reride_fetch");
                String json = "{\"id\":\"" + str(id) + "\"";
                post.addHeader("ContentType", "application/json");
                post.addJson(json);
                post.send();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class ReRide {

        int user_id;          // Unique User id which will be mapped with each tuple in AWS and app login (in future)
        ArrayList<Data> data; // Store historical data in ArrayList to visualise (either real or from AWS)
        int init_status;      // Initialization status
        int disp_mode;        // 0: Split mode, 1:
        int index;
        int c;

        // Left and Right Graphics
        PGraphics pgl, pgr;

        ReRide() {
            data = new ArrayList<Data>();
            init_status = 0;
            disp_mode = 2;
            index = -1;
        }

        public int init() {
            String status;
            pushStyleMatrix();
            centerOrigin();
            background(0);
            fill(255);
            textSize(50);
            textAlign(CENTER);
            switch (init_status) {
                case 0:
                    status = "Initializing system...";
                    text("ReRide", 0, 0);
                    textSize(20);
                    text(status, 0, 50);
                    init_status = 1;
                    initGraphics();
                    delay(1000);
                    break;
                case 1:
                    status = "Checking internet connection...";
                    text("ReRide", 0, 0);
                    textSize(20);
                    text(status, 0, 50);
                    init_status = 2;
                    delay(1000);
                    break;
                case 2:
                    status = "Logging in...";
                    text("ReRide", 0, 0);
                    textSize(20);
                    text(status, 0, 50);
                    init_status = 3;
                    delay(1000);
                    break;
                default:
                    break;
            }
            popStyleMatrix();
            return init_status;
        }

        public void displayTest() {
            pgl.beginDraw();
            pgl.background(0);
            pgl.endDraw();
            pgr.beginDraw();
            pgr.background(0);
            pgr.endDraw();
            update();
        }

        public void updateBackground() {
            pgl.beginDraw();
            pgl.background(0);
            pgl.endDraw();
            pgr.beginDraw();
            pgr.noStroke();
            pgr.background(0, map(TRAIL_FACTOR, 0, 100, 255, 0));
            pgr.endDraw();
            update();
        }

        public void getData() {
            Data now = new Data();
            now.getData();
            data.add(now);
            index++;
        }

        public void timeView() {
            pushStyleMatrix();
            pgl.beginDraw();
            pgl.translate(pgl.width / 2, pgl.height / 2);
            pgl.stroke(255);
            pgl.strokeWeight(60);
            pgl.noFill();
            pgl.colorMode(HSB);
            float start = 0;
            for (int i = 0; i < data.size(); i++) {
                Data now = data.get(i);
                float tilt = now.tilt();
                pgl.stroke(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200, 50);
                pgl.arc(0, 0, 200, 200, start, start + (PI / 120), OPEN);
                start += PI / 120;
            }
            pgl.colorMode(RGB);
            pgl.endDraw();
            popStyleMatrix();
        }

        public void abstractPosture() {
            Data now = data.get(index);
            pushStyleMatrix();

            try {
                //now.streamData(index);
            } catch (Exception e) {
            }
            pgr.beginDraw();

            pgr.translate(pgr.width / 2, pgr.height / 2);
            float tilt = now.tilt();
            pgr.colorMode(HSB);
            c = color(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200);
            pgr.fill(c);
            pgr.beginShape();
            float new_tilt = +map(tilt, 0, 100, MIN_DISP_TILT, MAX_DISP_TILT);
            //bottom left
            pgr.vertex(-pgr.width / 3, (pgr.height / 3) - new_tilt);
            //top left
            pgr.vertex((-pgr.width / 3) + TRAP_OFFSET, now.lateral_pos());
            //top right
            pgr.vertex((pgr.width / 3) - TRAP_OFFSET, now.lateral_pos());
            //bottom right
            pgr.vertex(pgr.width / 3, (pgr.height / 3) + new_tilt);
            pgr.endShape();
            pgr.strokeWeight(10);
            pgr.stroke(255, 0, 0);
            pgr.ellipse(0, now.lateral_pos(), map(now.lateral_pos(), -100, 400, 200, 50), map(now.lateral_pos(), -100, 400, 200, 10));
            pgr.colorMode(RGB);
            pgr.fill(255);
            pgr.textSize(50);
            //pgr.text("x:" + ax + ",y:" + ay + ",z:" + az, 0, 0);
            pgr.endDraw();

            popStyleMatrix();
            delay(100);
        }

        public void update() {
            switch (disp_mode) {
                case 0:
                    image(pgl, 0, 0);
                    image(pgr, width / 2, 0);
                    break;
                case 1:
                    image(pgl, 0, 0);
                    break;
                case 2:
                    image(pgr, 0, 0);
                    break;
            }
        }

        public void centerOrigin() {
            translate(width / 2, height / 2);
        }

        public void pushStyleMatrix() {
            pushMatrix();
            pushStyle();
        }

        public void popStyleMatrix() {
            popMatrix();
            popStyle();
        }

        public void initGraphics() {
            switch (disp_mode) {
                case 0:
                    pgl = createGraphics(width / 2, height);
                    pgr = createGraphics(width / 2, height);
                    break;
                case 1:
                    pgl = createGraphics(width, height);
                    pgr = createGraphics(width, height);
                    break;
                case 2:
                    pgl = createGraphics(width, height);
                    pgr = createGraphics(width, height);
                    break;
            }
        }

        public void changeMode() {
            disp_mode = (disp_mode + 1) % 3;
            initGraphics();
        }
    }
}
