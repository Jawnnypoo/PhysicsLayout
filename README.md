# PhysicsLayout

![Sample Gif](http://fat.gfycat.com/TotalCheerfulDromedary.gif)

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PhysicsLayout-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1762) [![Build Status](https://travis-ci.org/Jawnnypoo/PhysicsLayout.svg?branch=master)](https://travis-ci.org/Jawnnypoo/PhysicsLayout)

Android layout that simulates physics using JBox2D. Simply add views, enable physics, and watch them fall!

The gif is choppy, see it in action with the sample app:

[![Google Play](https://raw.githubusercontent.com/Jawnnypoo/PhysicsLayout/master/art/google-play-badge.png)](https://play.google.com/store/apps/details?id=com.jawnnypoo.physicslayout.sample)

# Gradle Dependency (jCenter)

Easily reference the library in your Android projects using this dependency in your module's `build.gradle` file:

```Gradle
dependencies {
    compile 'com.jawnnypoo:physicslayout:1.0.1'
}
```

# Basic Usage
If you want to see what your layout looks like when physics is applied to it, simply change your root layout to a physics layout. 
```xml
  <com.jawnnypoo.physicslayout.PhysicsLinearLayout
    android:id="@+id/physics_layout"
    android:layout_width="match_parent"
    android:layout_height="200dp">
            
      <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_launcher"/>

      <ImageView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:src="@drawable/ic_launcher"/>
              
      <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Hello world, I have physics!"/>
            
  </com.jawnnypoo.physicslayout.PhysicsLinearLayout>
```     
# Custom XML Attributes
You can also further customize the behaviour of your PhysicsLayout
    
```xml  
  <com.jawnnypoo.physicslayout.PhysicsLinearLayout
    android:id="@+id/physics_layout"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    app:physics="true"
    app:gravityX="0.0"
    app:gravityY="9.8"
    app:bounds="true"
    app:boundsSize="50dp"/>
```            

 * `physics` boolean, Determines if physics will be applied to the layout (Default true)
 * `gravityX` float, Sets the gravity in the X direction (positive is right, negative is left) (Default 0)
 * `gravityY` float, Sets the gravity in the Y direction (positive is down, negative is up) (Default 9.8)
 * `bounds` boolean, Determines if the layout should have bounds on the edges of itself (Default true)
 * `boundsSize` dimenstion, Sets the width/height of the bounds on the edges (Default 20dp)

# Custom Physics Configuration
Each view contained within the layout has a physics configuration that it uses to create itself in the Box2D world. This defines its shape, mass, restitutaion, and other physics related variables. A custom configuration can be applied to each view as well, using the PhysicsConfiguration builder:

```java
final View circleView = findViewById(R.id.circle);
PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .setRadius(100)
                .setAllowRotation(true)
                .setBodyDefType(BodyType.STATIC)
                .setDensity(1.0f)
                .setFriction(1.0f)
                .setRestitution(1.0f)
                .build();
physicsLayout.getPhysics().setPhysicsConfig(circleView, config);
```

This is useful especially if you have view that would be considered circular, as the default for all views is a RETANGLE shape type. Most of the time, if you are just dealing with rectangular views, the defaults will work for you and you will not have to worry about this. 

Check out the sample app to see most of these things in action.

## Using this library?

I wanna see the cool ideas and stuff you guys have made with this. If you're using this library in one of your projects just [send me a tweet](https://twitter.com/Jawnnypoo) and I'll add your project to the list.


Icon | Application
------------ | -------------
<img src="https://lh6.ggpht.com/bD8GKGQKsT-QD7vk6eV74I1JvOUOdDv7dxHN2_RghjigfStO7_kjk4PRqOb2XohG2Q=w300-rw" width="48" height="48" /> | [DejaVu]

[DejaVu]:https://play.google.com/store/apps/details?id=vincorp.in.dejavu
