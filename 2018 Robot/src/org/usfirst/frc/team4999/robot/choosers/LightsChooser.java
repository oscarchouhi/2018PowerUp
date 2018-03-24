package org.usfirst.frc.team4999.robot.choosers;

import org.usfirst.frc.team4999.lights.Color;

import java.util.HashMap;
import java.util.Vector;

import org.usfirst.frc.team4999.lights.Animator;
import org.usfirst.frc.team4999.lights.animations.*;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.TableEntryListener;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Manages the current animation of the {@link NeoPixels}
 * @author jordan
 *
 */
public class LightsChooser extends SendableChooser<Animation> {
	
	private final String NAME = "Lights Chooser";
	
	private Animator animator;
	
	// The animationTable registers keys to animations
	HashMap<String, Animation> animationTable;
	// The animations Vector holds the keys of registered animations, in the order that they are registered
	Vector<String> animations;
	
	// Some basic animations
	public final Animation blinkRed = new Blink(new Color[] {Color.RED, Color.BLACK}, 50);
	public final Animation whiteSnake = new Snake(new Color[] {Color.WHITE, Color.BLACK, Color.BLACK}, 50);
	public final Animation reverseWhiteSnake = new Snake(new Color[] {Color.WHITE, Color.BLACK, Color.BLACK}, 50, true);
	public final Animation blinkGreen = new Blink(new Color[] {Color.GREEN, Color.BLACK}, 50);
	
	public LightsChooser() {
		super();
		
		AnimationSequence momentum = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Color.MOMENTUM_PURPLE, Color.MOMENTUM_BLUE, 1, 5, 2, 125),
				new Fade(new Color[]{Color.MOMENTUM_BLUE, Color.WHITE, Color.MOMENTUM_PURPLE}, 200, 0),
				Snake.twoColorSnake(Color.MOMENTUM_BLUE, Color.MOMENTUM_PURPLE, 3, 0, 3, 250),
				new Fade(new Color[]{Color.MOMENTUM_BLUE, Color.WHITE, Color.MOMENTUM_PURPLE}, 250,0),
		}, 5000);
		
		AnimationSequence rainbow = new AnimationSequence(new Animation[] {
				Snake.rainbowSnake(70),
				Fade.RainbowFade(50, 20),
				Snake.rainbowSnake(150),
				Fade.RainbowFade(200, 0)
		}, new int[] {5000, 5000, 1000, 12000});
		
		AnimationSequence christmas = new AnimationSequence(new Animation[] {
				Snake.twoColorSnake(Color.RED, Color.WHITE, 2, 0, 4, 250),
				new Fade(new Color[] {Color.RED, new Color(60,141,13), Color.WHITE}, 0, 250),
				new Snake(new Color[] {new Color(255,223,0), new Color(60,141,13), new Color(45,100,13), new Color(45,100,13), new Color(39,84,14), new Color(39,84,14) }, 150)
		}, 5000);
		
		Animation solid = new Solid(Color.WHITE);
		
		Animation random = new RandomColors(500, 120);
		
		addDefault("Momentum", momentum);
		addObject("Rainbow", rainbow);
		addObject("Christmas",christmas);
		addObject("Solid White", solid);
		addObject("Random", random);
		
		SmartDashboard.putData(NAME, this);
		
		animator = new Animator();
		animations = new Vector<String>();
		animationTable = new HashMap<String, Animation>();
		animator.setAnimation(getSelected());
		
		NetworkTableInstance.getDefault().getTable("SmartDashboard").getSubTable(NAME).getEntry("selected").addListener((notification) -> {
			if(animations.isEmpty()) {
				animator.setAnimation(getSelected());
				System.out.println("Setting animation to " + notification.value.getString());
			}
		},TableEntryListener.kUpdate|TableEntryListener.kImmediate);
		
	}
	
	/**
	 * Adds an animation to the stack of animations.
	 * <p>
	 * Animations are prioritized by the order in which they're added, with newer animations being prioritized over older animations.
	 * The most recent animation registered is shown, defaulting to what is selected on the SmartDashboard if no animations are registered.
	 * @param key Unique key of the animation to be registered
	 * @param a The animation to register
	 */
	public void pushAnimation(String key, Animation a) {
		// Prevent duplicate entries
		if(animations.contains(key))
			return;
		animationTable.put(key, a);
		animations.add(key);
		animator.setAnimation(animationTable.get(animations.lastElement()));
	}
	/**
	 * Removes an animation from the stack of animations.
	 * @see #pushAnimation
	 * @param key The unique key of the animation to remove
	 */
	public void popAnimation(String key) {
		animations.removeElement(key);
		animationTable.remove(key);
		if(animations.isEmpty()) {
			animator.setAnimation(getSelected());
		} else {
			animator.setAnimation(animationTable.get(animations.lastElement()));
		}
	}
	
}