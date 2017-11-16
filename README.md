# PhysicsLayout

![Sample Gif](http://fat.gfycat.com/TotalCheerfulDromedary.gif)

[![Build Status](https://travis-ci.org/Jawnnypoo/PhysicsLayout.svg?branch=master)](https://travis-ci.org/Jawnnypoo/PhysicsLayout) [![](https://jitpack.io/v/Jawnnypoo/PhysicsLayout.svg)](https://jitpack.io/#Jawnnypoo/PhysicsLayout) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-PhysicsLayout-brightgreen.svg?style=flat)](http://android-arsenal.com/details/1/1762)

Android layout that simulates physics using [JBox2D](https://github.com/jbox2d/jbox2d). Simply add views, enable physics, and watch them fall!

The gif is choppy, see it in action with the sample app:

[![Google Play](https://raw.githubusercontent.com/Jawnnypoo/PhysicsLayout/master/art/google-play-badge.png)](https://play.google.com/store/apps/details?id=com.jawnnypoo.physicslayout.sample)

## Gradle Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}
```

Then, add the library to your project `build.gradle`
```gradle
dependencies {
    implementation 'com.github.Jawnnypoo:PhysicsLayout:2.1.0'
}
```

## Basic Usage
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
## Custom XML Attributes
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

## Custom Physics Configuration
Each view contained within the layout has a physics configuration that it uses to create itself in the Box2D world. This defines its shape, mass, restitutaion, and other physics related variables. A custom configuration can be applied to each view as well:
```xml
    <TextView
        android:id="@+id/text"
        android:layout_width="20dp"
        android:layout_height="20dp"
        app:layout_shape="circle"
        app:layout_circleRadius="20dp"
        app:layout_bodyType="kinematic"
        app:layout_fixedRotation="true"
        app:layout_friction="0.8"
        app:layout_restitution="0.3"
        app:layout_density="0.5" />
```
or alternatively, the Physics definition can be made programmatically:
```java
final View circleView = findViewById(R.id.circle);
PhysicsConfig config = PhysicsConfig.create();
        config.shapeType = PhysicsConfig.SHAPE_TYPE_CIRCLE;
        config.radius = dpToPx(30);
        config.fixtureDef = fixtureDef;
        config.bodyDef = bodyDef;
Physics.setPhysicsConfig(circleView, config);
```

This is useful especially if you have view that would be considered circular, as the default for all views is a RETANGLE shape type. Most of the time, if you are just dealing with rectangular views, the defaults will work for you and you will not have to worry about this. 

Check out the sample app to see most of these things in action.

## Using this library?

I wanna see the cool ideas and stuff you guys have made with this. If you're using this library in one of your projects just [send me a tweet](https://twitter.com/Jawnnypoo) and I'll add your project to the list.


Icon | Application
------------ | -------------
<img src="https://lh6.ggpht.com/bD8GKGQKsT-QD7vk6eV74I1JvOUOdDv7dxHN2_RghjigfStO7_kjk4PRqOb2XohG2Q=w300-rw" width="48" height="48" /> | [DejaVu]
<img src="https://lh3.googleusercontent.com/yUX513TrmvL7qnpCeyGsnw5ydjGVokY2ZKqOgc5pGD60F4JkVE4smmJyKVb8H-IZsw=w300-rw" width="48" height="48" /> | [Y'U]
<img src="https://lh3.googleusercontent.com/kI_H1o6q1ug7YcsD6B0BPkq0DUxdLYTOAEvny7wRE4fEPa130rFlzZS-6viGBumzhw=w300-rw" width="48" height="48" /> | [LabCoat]
[DejaVu]:https://play.google.com/store/apps/details?id=vincorp.in.dejavu
[Y'U]:https://play.google.com/store/apps/details?id=com.brounie.yumultimedia
[LabCoat]:https://play.google.com/store/apps/details?id=com.commit451.gitlab

## Making a Game?
This library was designed with the intention of allowing for playful animations within normal Android apps. It is not built to be a game engine or meant to compete with the likes. If you are looking to do more intense mobile games, we recommend libraries such as [libGDX](https://libgdx.badlogicgames.com/) or [Unity](https://unity3d.com/)

License
--------

    Copyright 2016 John Carlson

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
