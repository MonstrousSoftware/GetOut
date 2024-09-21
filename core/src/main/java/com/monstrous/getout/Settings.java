package com.monstrous.getout;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class Settings {


    public static String        title = "Get The Heck Out";

    public static boolean       release = false;

    public static float         ambientLightLevel = 0.1f; //0.03f;
    public static float         directionalLightLevel = 0.0f;

    static public Color         fogColour = new Color(.0f, 0.0f, 0.0f, 1f);

    // shadows
    public static int           shadowMapSize = 2048;
    public static int           shadowViewportSize = 20;
    public static float         shadowNear = 0f;
    public static float         shadowFar = 1500f;
    public static int           inverseShadowBias = 15000;

    // cascaded shadows
    public static boolean       cascadedShadows = false;
    public static int           numCascades = 2;
    public static float         cascadeSplitDivisor = 4f;

    public static boolean       debugShowLightBox = false;
    public static boolean       debugShowCascades = false;
    public static boolean       debugShowFrustum = false;

    public static boolean       showLightSettingsMenu = false;


    public static boolean       useGLprofiler = false;      // set to true to get GL errors reported (NB big impact on frame rate)


    static public boolean       supportControllers = false;       // disable in case it causes issues
    static public float         verticalReadjustSpeed = 4f;

    static public float         eyeHeight = 1.2f;   // meters

    static public float         walkSpeed = 4f;    // m/s
    static public float         runFactor = 2f;     // multiplier for walk speed
    static public float         turnSpeed = 120f;   // degrees/s
    static public float         groundRayLength = 1.2f;
    static public float         navHeight = 1.6f;       // should be about half the height of the characters


    static public boolean       invertLook = false;
    static public boolean       freeLook = true;
    static public float         headBobDuration = 0.5f; // s
    static public float         headBobHeight = 0.04f;  // m
    static public float         degreesPerPixel = 0.05f; // mouse sensitivity


    static public boolean       noClip = false;
    static public boolean       showColliders = false;
    static public boolean       showFPS = true;

    static public boolean       postFilter = false;   // post-processing shader effects
    static public boolean       camStabilisation = !release;

    static public boolean       fullScreen = false;

    static public boolean       playMusic = release;

    static public boolean       noBots = false;
    static public boolean       playerIsInvisible = true;


}
