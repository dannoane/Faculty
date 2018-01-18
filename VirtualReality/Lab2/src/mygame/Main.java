package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
    
    float totalTime = 0;
    boolean toggle = true;
    Node pivotNeck;
    Node pivotShoulderLeft;    
    Node pivotShoulderRight;     
    Node pivotElbowLeft;      
    Node pivotElbowRight;     
    Node pivotHipLeft;     
    Node pivotHipRight;     
    Node pivotKneeLeft;          
    Node pivotKneeRight;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        initPivots();
        
        initBody();
        initHead();
        initLeftHand();
        initLeftLeg();
        initRightHand();
        initRightLeg();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        //rootNode.setLocalTranslation(new Vector3f(1, 1, 1 + totalTime));
        rootNode.rotate(0, 0, -FastMath.cos(totalTime) * 0.0002f);

        pivotNeck.rotate(0, 0, FastMath.cos(totalTime) * 0.0003f);
        
        totalTime += tpf;
        
        pivotHipLeft.rotate(-FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotKneeLeft.rotate(FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotShoulderLeft.rotate(FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotElbowLeft.rotate(FastMath.cos(totalTime) * 0.002f, 0, 0);
        
        pivotHipRight.rotate(FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotKneeRight.rotate(-FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotShoulderRight.rotate(-FastMath.cos(totalTime) * 0.002f, 0, 0);
        pivotElbowRight.rotate(-FastMath.cos(totalTime) * 0.002f, 0, 0);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void initBody() {

        Box body = new Box(1, 2, 0.2f);
        
        Material bodyMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        bodyMat.setColor("Color", ColorRGBA.Blue);
        
        Geometry bodyGeom = new Geometry("Box", body);
        bodyGeom.setLocalTranslation(new Vector3f(0, 0, 0));
        bodyGeom.setMaterial(bodyMat);
        
        rootNode.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(bodyGeom);
    }

    private void initHead() {
        
        Sphere head = new Sphere(20, 20, 0.8f);
        
        Material headMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        headMat.setColor("Color", ColorRGBA.Yellow);
        
        Geometry headGeom = new Geometry("Sphere", head);
        headGeom.setLocalTranslation(new Vector3f(0, 1.8f, 0));
        headGeom.setMaterial(headMat);
        
        pivotNeck.setLocalTranslation(new Vector3f(0, 1, 0));
        pivotNeck.attachChild(headGeom);
        rootNode.attachChild(pivotNeck);
    }

    private void initLeftHand() {
        
        // UPPER ARM
        Box upperArmLeft = new Box(0.2f, 1, 0.2f);
        
        Material upperArmLeftMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        upperArmLeftMat.setColor("Color", ColorRGBA.Red);
        
        Geometry upperArmLeftGeom = new Geometry("Box", upperArmLeft);
        upperArmLeftGeom.setMaterial(upperArmLeftMat);
        
        
        // LOWER ARM
        Box lowerArmLeft = new Box(0.2f, 1, (float) 0.2f);
       
        Material lowerArmLeftMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lowerArmLeftMat.setColor("Color", ColorRGBA.Green);
        
        Geometry lowerArmLeftGeom = new Geometry("Box", lowerArmLeft);
        lowerArmLeftGeom.setMaterial(lowerArmLeftMat);
        
        // PIVOTS
        pivotElbowLeft.setLocalTranslation(0, -1, 0);
        lowerArmLeftGeom.setLocalTranslation(0, -1, 0);
        pivotElbowLeft.attachChild(lowerArmLeftGeom);
        
        pivotShoulderLeft.setLocalTranslation(-1.2f, 1, 0);
        pivotShoulderLeft.attachChild(upperArmLeftGeom);
        pivotShoulderLeft.attachChild(pivotElbowLeft);
        
        rootNode.attachChild(pivotShoulderLeft);
    }

    private void initLeftLeg() {
        
        // UPPER LEG
        Box upperLegLeft = new Box(0.3f, 1, 0.2f);
        
        Material upperLegLeftMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        upperLegLeftMat.setColor("Color", ColorRGBA.Green);
        
        Geometry upperLegLeftGeom = new Geometry("Box", upperLegLeft);
        upperLegLeftGeom.setMaterial(upperLegLeftMat);
        
        // LOWER LEG
        Box lowerLegLeft = new Box(0.3f, 1, 0.2f);
        
        Material lowerLegLeftMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lowerLegLeftMat.setColor("Color", ColorRGBA.Orange);
        
        Geometry lowerLegLeftGeom = new Geometry("Box", lowerLegLeft);
        lowerLegLeftGeom.setMaterial(lowerLegLeftMat);
        
        // PIVOTS
        pivotKneeLeft.setLocalTranslation(0, -2, 0);
        lowerLegLeftGeom.setLocalTranslation(0, -1, 0);
        pivotKneeLeft.attachChild(lowerLegLeftGeom);
        
        pivotHipLeft.setLocalTranslation(-0.7f, -2, 0);
        upperLegLeftGeom.setLocalTranslation(0, -1, 0);
        pivotHipLeft.attachChild(upperLegLeftGeom);
        pivotHipLeft.attachChild(pivotKneeLeft);
        
        rootNode.attachChild(pivotHipLeft);
    }

    private void initRightHand() {
        
        // UPPER ARM
        Box upperArmRight = new Box(0.2f, 1, 0.2f);
        
        Material upperArmRightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        upperArmRightMat.setColor("Color", ColorRGBA.Red);
        
        Geometry upperArmRightGeom = new Geometry("Box", upperArmRight);
        upperArmRightGeom.setMaterial(upperArmRightMat);
        
        
        // LOWER ARM
        Box lowerArmRight = new Box(0.2f, 1, (float) 0.2f);
       
        Material lowerArmRightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lowerArmRightMat.setColor("Color", ColorRGBA.Green);
        
        Geometry lowerArmRightGeom = new Geometry("Box", lowerArmRight);
        lowerArmRightGeom.setMaterial(lowerArmRightMat);
        
        // PIVOTS
        pivotElbowRight.setLocalTranslation(0, -1, 0);
        lowerArmRightGeom.setLocalTranslation(0, -1, 0);
        pivotElbowRight.attachChild(lowerArmRightGeom);
        
        pivotShoulderRight.setLocalTranslation(1.2f, 1, 0);
        pivotShoulderRight.attachChild(upperArmRightGeom);
        pivotShoulderRight.attachChild(pivotElbowRight);
        
        rootNode.attachChild(pivotShoulderRight);
    }

    private void initRightLeg() {
        
        // UPPER LEG
        Box upperLegRight = new Box(0.3f, 1, 0.2f);
        
        Material upperLegRightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        upperLegRightMat.setColor("Color", ColorRGBA.Green);
        
        Geometry upperLegRightGeom = new Geometry("Box", upperLegRight);
        upperLegRightGeom.setMaterial(upperLegRightMat);
        
        // LOWER LEG
        Box lowerLegRight = new Box(0.3f, 1, 0.2f);
        
        Material lowerLegRightMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        lowerLegRightMat.setColor("Color", ColorRGBA.Orange);
        
        Geometry lowerLegRightGeom = new Geometry("Box", lowerLegRight);
        lowerLegRightGeom.setMaterial(lowerLegRightMat);
        
        // PIVOTS
        pivotKneeRight.setLocalTranslation(0, -2, 0);
        lowerLegRightGeom.setLocalTranslation(0, -1, 0);
        pivotKneeRight.attachChild(lowerLegRightGeom);
        
        pivotHipRight.setLocalTranslation(0.7f, -2, 0);
        upperLegRightGeom.setLocalTranslation(0, -1, 0);
        pivotHipRight.attachChild(upperLegRightGeom);
        pivotHipRight.attachChild(pivotKneeRight);
        
        rootNode.attachChild(pivotHipRight);
    }

    private void initPivots() {
        
        pivotNeck              = new Node("pivotHead");
        pivotShoulderLeft      = new Node("pivotShoulderLeft");
        pivotShoulderRight     = new Node("pivotShoulderRight");
        pivotElbowLeft         = new Node("pivotElbowLeft");
        pivotElbowRight        = new Node("pivotElbowRight");
        pivotHipLeft           = new Node("pivotHipLeft");
        pivotHipRight          = new Node("pivotHipRight");
        pivotKneeLeft          = new Node("pivotKneeLeft");
        pivotKneeRight         = new Node("pivotKneeRight");
    }
}
