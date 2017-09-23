/**
  // all points moving but bottom line center is fixed 
  void abstractPosture4() {
    Data now = data.get(index);
    pushStyleMatrix();

    pgr.beginDraw();

    pgr.translate(pgr.width/2, pgr.height/2);
    float tilt = now.tilt();
    pgr.colorMode(HSB);
    c=color(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200);
    pgr.fill(c);
    pgr.noStroke();
    pgr.beginShape();
    float new_tilt=+map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT);
    pgr.vertex(-pgr.width/3, (pgr.height/3)+new_tilt);
    pgr.vertex(-pgr.width/3+TRAP_OFFSET, now.lateral_pos()+new_tilt);
    pgr.vertex(pgr.width/3-TRAP_OFFSET, now.lateral_pos()-new_tilt);
    pgr.vertex(pgr.width/3, (pgr.height/3)-new_tilt);
    pgr.endShape();
    pgr.colorMode(RGB);
    pgr.fill(255);
    pgr.textSize(50);
    pgr.text("x:"+ax+",y:"+ay+",z:"+az, 0, 0);
    pgr.endDraw();

    popStyleMatrix();
  }
  
  // abstract posture - bottom points tilting
  void abstractPosture2() {
    Data now = data.get(index);
    pushStyleMatrix();

    pgr.beginDraw();

    pgr.translate(pgr.width/2, pgr.height/2);
    float tilt = now.tilt();
    pgr.colorMode(HSB);
    c=color(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200);
    pgr.fill(c);
    pgr.noStroke();
    pgr.beginShape();
    pgr.vertex(-pgr.width/3, -now.lateral_pos()+map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT));
    pgr.vertex(-pgr.width/3+TRAP_OFFSET, -pgr.height/3);
    pgr.vertex(pgr.width/3-TRAP_OFFSET, -pgr.height/3);
    pgr.vertex(pgr.width/3, -now.lateral_pos()-map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT));
    pgr.endShape();
    pgr.colorMode(RGB);
    pgr.fill(255);
    pgr.textSize(50);
    pgr.text("x:"+ax+",y:"+ay+",z:"+az, 0, 0);
    pgr.endDraw();

    popStyleMatrix();
  }
  
  // top points tilting
  void abstractPosture1() {
    Data now = data.get(index);
    pushStyleMatrix();

    pgr.beginDraw();

    pgr.translate(pgr.width/2, pgr.height/2);
    float tilt = now.tilt();
    pgr.colorMode(HSB);
    c=color(map(tilt, MIN_TILT, MAX_TILT, 0, 255), 200, 200);
    pgr.fill(c);
    pgr.noStroke();
    pgr.beginShape();
    pgr.vertex(-pgr.width/3, pgr.height/3);
    pgr.vertex(-pgr.width/3+TRAP_OFFSET, now.lateral_pos()+map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT));
    pgr.vertex(pgr.width/3-TRAP_OFFSET, now.lateral_pos()-map(tilt, MIN_TILT, MAX_TILT, MIN_DISP_TILT, MAX_DISP_TILT));
    pgr.vertex(pgr.width/3, pgr.height/3);
    pgr.endShape();
    pgr.colorMode(RGB);
    pgr.fill(255);
    pgr.textSize(50);
    pgr.text("x:"+ax+",y:"+ay+",z:"+az, 0, 0);
    pgr.endDraw();

    popStyleMatrix();
  }
**/