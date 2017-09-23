/**
 
 ReRide Main Program for Android Mobile v>5.0 Marshmallow
 Author: Gaurav Singh
 For INTERACT 2017 demo
 Version 1.0
 
 Generate SSL Client certificate with keytool
 Generate a client key pair:
 keytool -genkey -alias rerideClient -keystore clientKeyStore.key
 Password: 12345678
 
 Generate a client certificate:
 reride_new $keytool -export -alias rerideClient -keystore clientKeyStore.key -rfc -file rrClient.cert
 
 Install a client certificate in the server's trust store
 keytool -import -alias aDerbyClient -file rrClient.cert -keystore serverTrustStore.key
 
 
 **/

// HTTP Requests
import http.requests.*;

// Declaring RR instance
ReRide r;

// Initialization Status
// 0: Loading and starting the application
// 1: Checking Bluetooth Connection
// 2: Checking Internet Connection
// 3: Establish connection with AWS
// 4: Test Lambda Fetch
// 5: Test Lambda Push
// 6: Start streaming
int init_status;

void setup() {

  // Fetch display resolution from mobile
  // Uncomment below line and remove the line after
  fullScreen();
  // size(600, 300);

  // Initializing the RR instance
  r = new ReRide();

  // Initial App Load Status
  init_status=0;

  // Create neccessary Android instances if simulating through Android
  createAndroidInstance();

  smooth(4);
}

void draw() {

  // Initialize Display
  if (init_status<3) {
    init_status=r.init();
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
  
}

void mousePressed() {

  // Cycle display mode
  r.changeMode();
}

void testHTTPRequest() {

  PostRequest post = new PostRequest("http://httprocessing.heroku.com");
  post.addData("name", "Rune");
  post.send();
  System.out.println("Reponse Content: " + post.getContent());
  System.out.println("Reponse Content-Length Header: " + post.getHeader("Content-Length"));
}

void testHTTP() {
  PostRequest post = new PostRequest("https://ougkqprxzc.execute-api.us-west-2.amazonaws.com/prod/reride_push");
  String json=
    "{"+
    "\"id\":\"2\","+
    "\"fsr_left\":\"100\","+
    "\"fsr_right\":\"120\","+
    "\"acc_x\":\"12\","+
    "\"acc_y\":\"12\","+
    "\"acc_z\":\"14\","+
    "\"flag\":\"1\""+
    "}";
  post.addHeader("ContentType", "application/json");
  post.addJson(json);
  post.send();
  text("Response Content: " + post.getContent(),0,0);
  println("Response Content-Length Header: " + post.getHeader("Content-Length"));
}