// RR Constants

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
final int TRAIL_FACTOR = 60;

// Data Packet
class Data {

  // FSR Values
  float fsr_left;
  float fsr_right;

  // Helmet Accelerometer values
  float acc_x;
  float acc_y;
  float acc_z;

  // Timestamp as dd-mm-yy-hh-mm-ss-ms
  long timestamp;

  // Push button (true/false)
  boolean flag;

  // Constructor
  Data() {
  }

  // Get Tilt from FSR data
  float tilt() {

    // Get simulation data from mouse movement 
    //if (fsr_left>fsr_right) {
    //  return map(fsr_left-fsr_right, MIN_FSR, MAX_FSR, MIN_TILT, MAX_TILT);
    //} else {
    //  return map(fsr_right-fsr_left, MIN_FSR, MAX_FSR, MIN_TILT, MAX_TILT);
    //}

    // Get simulation data from phone tilt along y axis
    return map(ay, 5, -5, MIN_TILT, MAX_TILT);
  }

  // Get Lateral Tilt from Accelerometer data
  float lateral_pos() {
    // Get lateral posture angle from mouse movement
    // return map(mouseY, 0, height, -100, 100);

    // Get simulation data from phone tilt along y axis
    return map(az, 5, -5, 100, -400);
  }

  // Get Random data for simulation
  void getRandom() {

    fsr_left = map(mouseX, 0, width, MIN_FSR, MAX_FSR);
    fsr_right = map(mouseX, width, 0, MIN_FSR, MAX_FSR);
    acc_x = 10*noise(frameCount);
    acc_y = 10*noise(frameCount*1.232);
    acc_z = 10*noise(frameCount*1.202);
  }

  // Get Data from BLE peripherals
  // Get Data from BLE --> Send to RR class for display --> Stream data to AWS --> Data is fetch on another device/app post-ride
  void getData() {
    // BLE code
  }

  // Parse JSON and load data into variables
  void parseJSON() {
    // Parse JSON into discrete values
  }

  // Stream data to AWS
  void streamData() {
  }

  // Fetch data from AWS
  // For Dashboard Program only
  void fetchData() {
  }
}

class ReRide {

  int user_id;          // Unique User id which will be mapped with each tuple in AWS and app login (in future)
  ArrayList<Data> data; // Store historical data in ArrayList to visualise (either real or from AWS)
  int init_status;      // Initialization status
  int disp_mode;        // 0: Split mode, 1: 
  int index;
  color c;

  // Left and Right Graphics
  PGraphics pgl, pgr;

  ReRide() {
    data = new ArrayList<Data>();
    init_status=0;
    disp_mode=2;
    index=-1;
  }

  int init() {
    String status;
    pushStyleMatrix();
    centerOrigin();
    background(0);
    fill(255);
    textSize(50);
    textAlign(CENTER);
    switch(init_status) {
    case 0:
      status = "Initializing system...";
      text("ReRide", 0, 0);
      textSize(20);
      text(status, 0, 50);
      init_status=1;
      initGraphics();
      delay(1000);
      break;
    case 1:
      status = "Checking internet connection...";
      text("ReRide", 0, 0);
      textSize(20);
      text(status, 0, 50);
      init_status=2;
      delay(1000);
      break;
    case 2:
      status = "Logging in...";
      text("ReRide", 0, 0);
      textSize(20);
      text(status, 0, 50);
      init_status=3;
      delay(1000);
      break;
    default:
      break;
    }
    popStyleMatrix();
    return init_status;
  }
  void displayTest() {
    pgl.beginDraw();
    pgl.background(0);
    pgl.endDraw();
    pgr.beginDraw();
    pgr.background(0);
    pgr.endDraw();
    update();
  }

  void updateBackground() {
    pgl.beginDraw();
    pgl.background(0);
    pgl.endDraw();
    pgr.beginDraw();
    pgr.noStroke();
    pgr.background(0, map(TRAIL_FACTOR, 0, 100, 255, 0));
    pgr.endDraw();
    update();
  }

  void getData() {
    Data now=new Data();
    now.getRandom();
    data.add(now);
    index++;
  }

  void timeView() {
    pushStyleMatrix();
    pgl.beginDraw();
    pgl.translate(pgl.width/2, pgl.height/2);
    pgl.stroke(255);
    pgl.strokeWeight(60);
    pgl.noFill();
    pgl.colorMode(HSB);
    float start=0;
    for (int i=0; i<data.size(); i++) {
      Data now = data.get(i);
      float tilt = now.tilt();
      pgl.stroke(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200, 50);
      pgl.arc(0, 0, 200, 200, start, start+(PI/120), OPEN);
      start+=PI/120;
    }
    pgl.colorMode(RGB);
    pgl.endDraw();
    popStyleMatrix();
  }

  void abstractPosture() {
    Data now = data.get(index);
    pushStyleMatrix();

    pgr.beginDraw();

    pgr.translate(pgr.width/2, pgr.height/2);
    float tilt = now.tilt();
    pgr.colorMode(HSB);
    c=color(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200);
    pgr.fill(c);
    pgr.beginShape();
    float new_tilt=+map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT);
    //bottom left
    pgr.vertex(-pgr.width/3, (pgr.height/3)-new_tilt);   
    //top left
    pgr.vertex((-pgr.width/3)+TRAP_OFFSET, now.lateral_pos());
    //top right
    pgr.vertex((pgr.width/3)-TRAP_OFFSET, now.lateral_pos());
    //bottom right
    pgr.vertex(pgr.width/3, (pgr.height/3)+new_tilt);
    pgr.endShape();
    pgr.strokeWeight(10);
    pgr.stroke(255, 0, 0);
    pgr.ellipse(0, now.lateral_pos(), map(now.lateral_pos(), -100, 400, 200, 50), map(now.lateral_pos(), -100, 400, 200, 10));
    pgr.colorMode(RGB);
    pgr.fill(255);
    pgr.textSize(50);
    pgr.text("x:"+ax+",y:"+ay+",z:"+az, 0, 0);
    pgr.endDraw();

    popStyleMatrix();
  }


  void update() {
    switch(disp_mode) {
    case 0:
      image(pgl, 0, 0);
      image(pgr, width/2, 0);
      break;
    case 1:
      image(pgl, 0, 0);
      break;
    case 2:
      image(pgr, 0, 0);
      break;
    }
  }

  void centerOrigin() {
    translate(width/2, height/2);
  }

  void pushStyleMatrix() {
    pushMatrix();
    pushStyle();
  }

  void popStyleMatrix() {
    popMatrix();
    popStyle();
  }

  void initGraphics() {
    switch(disp_mode) {
    case 0:
      pgl = createGraphics(width/2, height);
      pgr = createGraphics(width/2, height);
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

  void changeMode() {
    disp_mode=(disp_mode+1)%3;
    initGraphics();
  }
}