// Importing Android libraries to access Phone sensors
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

// Declare Android instances
Context context;
SensorManager manager;
Sensor sensor;
AccelerometerListener listener;

// Accellerometer Data will be fed into these variables -- can be accessed globally
float ax, ay, az;

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

void createAndroidInstance() {
  context = getActivity();
  manager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
  sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  listener = new AccelerometerListener();
  manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_GAME);
}