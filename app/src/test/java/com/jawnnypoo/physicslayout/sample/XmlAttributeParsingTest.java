package com.jawnnypoo.physicslayout.sample;

import android.widget.TextView;

import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsLayoutParams;
import com.jawnnypoo.physicslayout.PhysicsLinearLayout;

import org.jbox2d.dynamics.BodyType;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

/**
 * Test to make sure that xml attributes are parsed properly
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class XmlAttributeParsingTest {

    private static final float DELTA = 0.001f;
    @BeforeClass
    public static void setUp() throws Exception {
        //for logging
        ShadowLog.stream = System.out;
    }

    @Test
    public void testXmlParsing() throws Exception {
        TestActivity activity = Robolectric.buildActivity(TestActivity.class).create().get();
        PhysicsLinearLayout physicsLinearLayout = (PhysicsLinearLayout) activity.findViewById(R.id.physics_layout);
        TextView view = (TextView) activity.findViewById(R.id.text);
        Physics physics = physicsLinearLayout.getPhysics();
        Assert.assertEquals(4, physics.getVelocityIterations());
        Assert.assertEquals(7, physics.getPositionIterations());
        Assert.assertEquals(1.0f, physics.getGravityX(), DELTA);
        Assert.assertEquals(2.0f, physics.getGravityY(), DELTA);
        Assert.assertEquals(false, physics.hasBounds());

        PhysicsLayoutParams params = (PhysicsLayoutParams) view.getLayoutParams();
        PhysicsConfig config = params.getConfig();
        Assert.assertNotNull(config);

        Assert.assertEquals(BodyType.KINEMATIC, config.bodyDef.type);
        Assert.assertEquals(true, config.bodyDef.fixedRotation);
        Assert.assertEquals(0.8f, config.fixtureDef.friction, DELTA);
        Assert.assertEquals(0.3f, config.fixtureDef.restitution, DELTA);
        Assert.assertEquals(0.5f, config.fixtureDef.density, DELTA);


    }
}
