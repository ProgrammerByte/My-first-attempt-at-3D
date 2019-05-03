
package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class ThreeDimensional extends ApplicationAdapter {
	
	//CameraInputController camController;
	PerspectiveCamera cam;
	ModelBatch modelBatch;
	Model[] model;
	ModelInstance[] instance;
	Environment environment;
	
	float differencex, differencey, previous;
	float[][] dimensions = {{100, 2, 2}, {2, 100, 2}, {2, 2, 100}, {5, 5, 5}};
	float[][] colors = {{0, 0, 1, 1}, {0, 1, 0, 1}, {1, 0, 0, 1}, {1, 1, 1, 1}};
	float[][] position = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {-10, -10, -10}}; //Position of the center of an object
	int playerx, playery, playerz;
	boolean ispressed, isup;
	
	@Override
	public void create () {
		Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2.5f, -5, -10));
		
		modelBatch = new ModelBatch();
		
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 0, 0);
        cam.lookAt(-10,-10,-10);
        cam.near = 1f;
        cam.far = 300f;
        cam.update();
        
        ModelBuilder modelBuilder = new ModelBuilder();
        model = new Model[dimensions.length];
        instance = new ModelInstance[model.length];
        
        for (int i = 0; i < model.length; i++) {
        model[i] = modelBuilder.createBox(dimensions[i][0], dimensions[i][1], dimensions[i][2], 
            new Material(ColorAttribute.createDiffuse(colors[i][0], colors[i][1], colors[i][2], colors[i][3])),
            Usage.Position | Usage.Normal);
        
	        instance[i] = new ModelInstance(model[i]);
	        instance[i].transform.translate(position[i][0], position[i][1], position[i][2]);
        }
        
        Gdx.input.setCursorCatched(true);
        Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		isup = false;
		ispressed = false;
		Vector3 direction = cam.direction.cpy();
		
		Vector3 crossProduct = new Vector3(-direction.z, 0, direction.x); //CROSSPRODUCT OF PLAYER DIRECTION AGAINST Y AXIS

		differencey = -Gdx.input.getY() + (Gdx.graphics.getHeight() / 2);
		if ((cam.direction.y > -0.8 || differencey > 0) && (cam.direction.y < 0.8 || differencey < 0)) {
		//	if (Math.asin(cam.direction.y) + differencey > 80) {
		//		differencey = (float) (80 - Math.asin(cam.direction.y));
		//	}
			
		//	else if (Math.asin(cam.direction.y) + differencey < -80) {
		//		differencey = (float) (-80 + Math.asin(cam.direction.y));
		//	}
			
			cam.rotate(crossProduct, differencey / 10);
			cam.update();
		}
		
		differencex = Gdx.input.getX() - (Gdx.graphics.getWidth() / 2);
		cam.rotate(new Vector3(0, -1, 0), differencex / 10);
		cam.update();
		
		crossProduct = new Vector3(-direction.z, 0, direction.x); //CROSSPRODUCT OF PLAYER DIRECTION AGAINST Y AXIS
		
		if (Gdx.input.isKeyPressed(Keys.S)) {
			ispressed = true;
		}
		else if (Gdx.input.isKeyPressed(Keys.W)) {
			ispressed = true;
			direction.mulAdd(direction, -2);
		}
		
		if (ispressed == true) {
			for (int i = 0; i < model.length; i++) {
				instance[i].transform.translate(direction);
			}
		}
		
		
		
		
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.D)) {
			isup = true;
			if (Gdx.input.isKeyPressed(Keys.D)) {
				crossProduct.mulAdd(crossProduct, -2);
			}
		}
		
		if (isup == true) {
			for (int i = 0; i < instance.length; i++) {
				instance[i].transform.translate(crossProduct);
			}
		}
		
		modelBatch.begin(cam);
		for (int i = 0; i < instance.length; i++) {
			modelBatch.render(instance[i], environment);
		}
		modelBatch.end();
		
		Gdx.input.setCursorPosition(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
		
		//System.out.println(cam.direction.x + " " + cam.direction.y + " " + cam.direction.z);
	}
	@Override
	public void dispose () {
		modelBatch.dispose();
		for (int i = 0; i < model.length; i++) {
			model[i].dispose();
		}
	}
	

}

