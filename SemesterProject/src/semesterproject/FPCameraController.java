package semesterproject;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.Sys;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class FPCameraController {
    //Var to hold a new chunk object
    private Chunk ch = new Chunk(0, 0, 0);
    
    //3d vector to store the camera's position in
    private Vector3f position = null;
    private Vector3f lPosition = null;
    
    //the rotation around the Y axis of the camera
    private float yaw = 0.0f;
    
    //the rotation around the X axis of the camera
    private float pitch = 0.0f;
    
    private Vector3Float me;
    
    private float timeOfDay = 0.5f; // Initial time of day (midday)
     
    private boolean inverted = false;
    private boolean skin = false;
    
    public FPCameraController(float x, float y, float z) {
        //instantiate position Vector3f to the x y z params.
        position = new Vector3f(x, y, z);
        lPosition = new Vector3f(x,y,z);
        lPosition.x = 30f;
        lPosition.y = 15f;
        lPosition.z = 75f;
    }

    //increment the camera's current yaw rotation
    public void yaw(float amount) {
        //increment the yaw by the amount param
        yaw += amount;
    }
    
    //increment the camera's current yaw rotation
    public void pitch(float amount) {
        //increment the pitch by the amount param
        pitch -= amount;
    }
    
    //moves the camera forward relative to its current rotation (yaw)
    public void walkForward(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(
        lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //moves the camera backward relative to its current rotation (yaw)
    public void walkBackwards(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x+=xOffset).put(
        lPosition.y).put(lPosition.z-=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }

    //strafes the camera left relative to its current rotation (yaw)
    public void strafeLeft(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw-90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw-90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(
        lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        FloatBuffer lightDirection = BufferUtils.createFloatBuffer(4);
        lightDirection.put(lPosition.x -= xOffset).put(lPosition.y).put(lPosition.z += zOffset).put(0.0f).flip();
        glLight(GL_LIGHT0, GL_SPOT_DIRECTION, lightDirection);
    }
    
    //strafes the camera right relative to its current rotation (yaw)
    public void strafeRight(float distance) {
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw+90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw+90));
        position.x -= xOffset;
        position.z += zOffset;
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x-=xOffset).put(
        lPosition.y).put(lPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        FloatBuffer lightDirection = BufferUtils.createFloatBuffer(4);
        lightDirection.put(lPosition.x -= xOffset).put(lPosition.y).put(lPosition.z += zOffset).put(0.0f).flip();
        glLight(GL_LIGHT0, GL_SPOT_DIRECTION, lightDirection);
    }

    //moves the camera up relative to its current rotation (yaw)
    public void moveUp(float distance) {
        position.y -= distance;
    }
    
    //moves the camera down
    public void moveDown(float distance) {
        position.y += distance;
    }

    //translates and rotate the matrix so that it looks through the camera
    //this does basically what gluLookAt() does
    public void lookThrough() {
        //roatate the pitch around the X axis
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        //roatate the yaw around the Y axis
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        //translate to the position vector's location
        glTranslatef(position.x, position.y - 50.0f, position.z);
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lPosition.x).put(lPosition.y).put(lPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);

    }

    public void gameLoop() {
        FPCameraController camera = new FPCameraController(5, 5, 5);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f; //length of frame
        float lastTime = 0.0f; // when the last frame was
        long time = 0;
        float mouseSensitivity = 0.09f;
        float movementSpeed = .35f;
        float brightness = 1.0f; // Initial brightness
        //hide the mouse
        Mouse.setGrabbed(true);


        // keep looping till the display window is closed the ESC key is down
        while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
            time = Sys.getTime();
            lastTime = time;
            //distance in mouse movement
            //from the last getDX() call.
            dx = Mouse.getDX();
            //distance in mouse movement
            //from the last getDY() call.
            dy = Mouse.getDY();
            //controll camera yaw from x movement fromt the mouse
            camera.yaw(dx * mouseSensitivity);
            //controll camera pitch from y movement fromt the mouse
            camera.pitch(dy * mouseSensitivity);

            if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
                inverted = !inverted;
                ch.toggleNewChunk(inverted);
            }
            
            //F2 changes the skin of the block
            if(Keyboard.isKeyDown(Keyboard.KEY_F2)){
                skin = !skin;
                ch.swtichSkin(skin);
            }
            
            //when passing in the distance to move
            //we times the movementSpeed with dt this is a time scale
            //so if its a slow frame u move more then a fast frame
            //so on a slow computer you move just as fast as on a fast computer
            
            if (Keyboard.isKeyDown(Keyboard.KEY_W)) { //move forward
                camera.walkForward(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_S)) { //move backwards
                camera.walkBackwards(movementSpeed);
            }
            
            if (Keyboard.isKeyDown(Keyboard.KEY_A)) { //strafe left
                camera.strafeLeft(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_D)) { //strafe right
                camera.strafeRight(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) { //move up
                camera.moveUp(movementSpeed);
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
                camera.moveDown(movementSpeed);
            }
            
            float timeSpeed = 0.001f; // Adjust as needed
            timeOfDay += timeSpeed;
            if (timeOfDay > 1.0f) {
                timeOfDay = 0.0f; // Wrap around to the next day
            }  
            
            setSkyColor();
            //added code ends
            
            //set the modelview matrix back to the identity
            glLoadIdentity();
            
            //look through the camera before you draw anything
            camera.lookThrough();
            
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            //you would draw your scene here.
            ch.render();
            
            //draw the buffer to the screen
            Display.update();
            Display.sync(60);
        }
            Display.destroy();
    }
    
    //both of these functions were added
    private void setSkyColor() {
        // Example: Transition from light blue (day) to dark blue (night)
        float blueIntensity = Math.max(0.0f, Math.min(1.0f, timeOfDay)); // Clamp between 0 and 1
        glClearColor(0.1f, 0.0f - blueIntensity, 0.9f - blueIntensity * 0.5f, 0.0f);
    }
    
    private void updateLightProperties() {
        // Example: Adjust brightness based on time of day
        float brightness = 0.5f + 0.5f * (1.0f - Math.abs(timeOfDay - 0.5f) * 2.0f);
        // Update light properties based on the brightness
        FloatBuffer whiteLight = BufferUtils.createFloatBuffer(4);
        whiteLight.put(brightness).put(brightness).put(brightness).put(0.0f).flip();
        glLight(GL_LIGHT0, GL_SPECULAR, whiteLight);
        glLight(GL_LIGHT0, GL_DIFFUSE, whiteLight);
        glLight(GL_LIGHT0, GL_AMBIENT, whiteLight);
    }
    
    private void render() {
        try{
            glBegin(GL_QUADS);
            //Top
            glColor3f(0.0f,0.0f,1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //Bottom
            glColor3f(1.0f,0.0f,0.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //Front
            glColor3f(0.0f,1.0f,0.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glEnd();

            glBegin(GL_QUADS);
            //Back
            glColor3f(1.0f,0.0f,1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glEnd();

            glBegin(GL_QUADS);
            //Left
            glColor3f(1.0f,1.0f,0.0f);
            glVertex3f(-1.0f, 1.0f,1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f,-1.0f);
            glVertex3f(-1.0f,-1.0f, 1.0f);
            glEnd();
            
            glBegin(GL_QUADS);
            //Right
            glColor3f(0.0f,1.0f,1.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f, 1.0f);
            glVertex3f( 1.0f,-1.0f,-1.0f);
                
            glEnd();
            
            glBegin(GL_LINE_LOOP);
            //Top
            glColor3f(0.0f,0.0f,0.0f);
            glVertex3f( 1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f,-1.0f);
            glVertex3f(-1.0f, 1.0f, 1.0f);
            glVertex3f( 1.0f, 1.0f, 1.0f);
                
            glEnd();
            glBegin(GL_LINE_LOOP);
            
            //Bottom
            glColor3f(0.0f,0.0f,0.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                
            glEnd();
            glBegin(GL_LINE_LOOP);
            
            //Front
            glColor3f(0.0f,0.0f,0.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                
            glEnd();
            glBegin(GL_LINE_LOOP);
            
            //Back
            glColor3f(0.0f,0.0f,0.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                
            glEnd();
            glBegin(GL_LINE_LOOP);
            
            //Left
            glColor3f(0.0f,0.0f,0.0f);
                glVertex3f(-1.0f, 1.0f, 1.0f);
                glVertex3f(-1.0f, 1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f,-1.0f);
                glVertex3f(-1.0f,-1.0f, 1.0f);
                
            glEnd();
            glBegin(GL_LINE_LOOP);
            
            //Right
            glColor3f(0.0f,0.0f,0.0f);
                glVertex3f( 1.0f, 1.0f,-1.0f);
                glVertex3f( 1.0f, 1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f, 1.0f);
                glVertex3f( 1.0f,-1.0f,-1.0f);
                
            glEnd();
        }catch(Exception e){
        }
    }
}