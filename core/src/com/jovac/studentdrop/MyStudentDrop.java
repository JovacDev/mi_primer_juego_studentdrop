package com.jovac.studentdrop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyStudentDrop extends ApplicationAdapter {
	private Texture dropImage;
	private Texture bucketImage;
	private Sound dropSound;
	private Music rainMusic;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	private Rectangle bucket;
	private Array<Rectangle> raindrops;
	private long lastDropTime;
	private int contador;
	private double speed = 200;
	
	@Override
	public void create () {
		/*
			Cargamos los assets que hemos guardado como una nueva textura.
		*/
		dropImage = new Texture(Gdx.files.internal("droplet.png"));
		bucketImage = new Texture(Gdx.files.internal("bucket.png"));

		/*
			Cargamos los sonidos y la musica.
		*/
		dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

		/*
		   Seteamos la musica del rain, al ponre setLooping hacemos que se reproduzca continuamente
		   Luego le damos play para que comience.
		*/
		rainMusic.setLooping(true);
		rainMusic.play();

		/*
			Creo una camara para la orientación, y le pongo false para que me salga el cubo abajo
			si no la orientacion me cambiaria y saldria el cubo arriba.
		*/
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);


		/*
			Hago un SpritBatch para poder poner los objetos en este caso el cubo donde quiero que este.
		 */
		batch = new SpriteBatch();

		/*
			Creo un rectangulo de la foto del cubo le pongo posicion donde debe estar
			y le pongo una dimesion.
		 */
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 20;
		bucket.width = 64;
		bucket.height = 64;

		/*

		 */
		raindrops = new Array<Rectangle>();
		spawnRaindrop();

	}

	@Override
	public void render () {

		/*
			Color de fondo del juego
		 */
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		/*
			Pongo la camara.
		 */
		camera.update();

		/*
			Renderizo los sprits de la camara y el sprite del Bucket con su Bucket x, y
		 */
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(bucketImage, bucket.x, bucket.y);
		batch.end();

		/*
			Este metodo mueve el cubo cuando presionas sobre la pantalla con el raton.
		 */
		if(Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}

		/*
			Metodos para mover el bucket con las flechas de direcion
		 */
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 200 * Gdx.graphics.getDeltaTime();

		/*
			Limites para que el cubo no se salga de la pantalla x
		 */
		if(bucket.x < 0) bucket.x = 0;
		if(bucket.x > 800 - 64) bucket.x = 800 - 64;

		/*
			Tiempo hasta que aparece una nueva gota y llama otra vez al metodo despues de ese tiempo.
		 */
		if(TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

		/*
			For para que la gota se mueva hacia abajo y si llega la parte de abajo se borra
		 */
		for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
			Rectangle raindrop = iter.next();
			raindrop.y -= speed * Gdx.graphics.getDeltaTime();
			/*
				Si se cae fuera se borra, es decir el final de la camara de y
			 */
			if(raindrop.y + 64 < 0) iter.remove();

			/*
				Si la gota cae dentro del cubo hace el sonido y se borra
			 */
			if(raindrop.overlaps(bucket)) {
				dropSound.play();
				contador = contador + 1;
				iter.remove();
			}

			if(contador == 10){
				speed = speed * 1.5;
				contador = 0;
			}
		}

		/*
			Aqui es para que se vea la gota definida en el metodo private spawnRaindrop
		 */
		batch.begin();
		for(Rectangle raindrop: raindrops) {
			batch.draw(dropImage, raindrop.x, raindrop.y);
		}
		batch.end();

	}
	
	@Override
	public void dispose () {
		dropImage.dispose();
		bucketImage.dispose();
		dropSound.dispose();
		rainMusic.dispose();
		batch.dispose();
	}

	/*
		Este metodo hace que la gota aparezca de forma aletaoria en un punto x de juego.
	 */
	private void spawnRaindrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800-64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastDropTime = TimeUtils.nanoTime();
	}
}