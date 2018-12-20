package com.kos0254.gyruss;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class Gyruss extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background,tubeTop,tubeBottom;
	ShapeRenderer shapeRenderer;


	Texture[] birds;
	Texture[] enemyType;

	int flapState = 0;
	float birdY = 0;
	float velocity = 0;
	float birdYOld = 0;
	int turn = 0;
    Circle birdCircle;
	Rectangle[] tubeTopShapes = new Rectangle[4];
	Rectangle[] tubeBottomShapes = new Rectangle[4];


	int gameState = 0;
	float gravity = 0;

	int birdX,tubeTopX,tubeTopY,tubeBottomX,tubeBottomY;
	int tubeTopOffset = 600;
	int tubeBottomOffset = 600;

	int numberOfStars = 200;
	float[] starX = new float[numberOfStars];
	float[] starY = new float[numberOfStars];
	float[] starVelocityY = new float[numberOfStars];
	float[] starRadius = new float[numberOfStars];



	int gap = 400;
	float maxTubeOffset;
	Random randomGenerator;
	float tubeVelocity = 4;
	int numberOfTubes = 4;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];
	float distanceBetweenTubes;

	int distanceTraveled = 0;
	int score = 0;
	int scoringTube = 0;

	Queue<Bullet> bullets = new LinkedList<Bullet>();
	Queue<Enemy> enemies = new LinkedList<Enemy>();
	int enemyCounter = 0;
	int enemyShootCounter = 0;


	BitmapFont font;
	Texture gameOver;

	long startTime;

	Sound sound;
	Sound fire;
	Sound hit;
	Sound dead;
	long idMusic;

	boolean canfire;

	boolean vibrate;
	boolean doHighScore;
    Preferences prefs;

	Json json;
	Date date;

	GregorianCalendar calendarG;

	public static String getStringFromDate(GregorianCalendar calendar){
		SimpleDateFormat fmt = new SimpleDateFormat("dd-MMM-yyyy");
		fmt.setCalendar(calendar);
		String dateFormatted = fmt.format(calendar.getTime());
		return dateFormatted;
	}

	@Override
	public void create () {
		doHighScore = true;
		json = new Json();
    	vibrate = true;
		sound = Gdx.audio.newSound(Gdx.files.internal("music.mp3"));
		fire = Gdx.audio.newSound(Gdx.files.internal("shot.mp3"));
		hit = Gdx.audio.newSound(Gdx.files.internal("hit.mp3"));
		dead = Gdx.audio.newSound(Gdx.files.internal("dead.mp3"));

		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");

		birds = new Texture[6];
		birds[0] = new Texture("playerW1.png");
		birds[3] = new Texture("playerW2.png");
		birds[1] = new Texture("playerA1.png");
		birds[4] = new Texture("playerA2.png");
		birds[2] = new Texture("playerD1.png");
		birds[5] = new Texture("playerD2.png");

		enemyType = new Texture[1];
		enemyType[0] = new Texture("enemy1.png");

		canfire = false;

		shapeRenderer = new ShapeRenderer();
		birdCircle = new Circle();

		tubeTop = new Texture("toptube.png");
		tubeBottom = new Texture("bottomtube.png");
		maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
		randomGenerator = new Random();
		distanceBetweenTubes = Gdx.graphics.getWidth() * 3/4;

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		Random rand = new Random();
		for(int i=0; i<numberOfStars; i++){
			starX[i] = 0;
			starY[i] = rand.nextInt((Gdx.graphics.getHeight() - 1) + 1) + 1;
			starVelocityY[i] = rand.nextInt((40 - 1) + 1) + 1;
			starRadius[i] = rand.nextInt((4 - 1) + 1) + 1;
		}

        prefs = Gdx.app.getPreferences("My Preferences");

		date = new Date(Long.MIN_VALUE);
		calendarG = new GregorianCalendar();
		calendarG.setTime(date);

		if(!prefs.contains("highscore"))
		{
			int [] myarray = new int[10];
			Arrays.fill(myarray, 0);
			prefs.putString("highscore", json.toJson(myarray) );

			String [] myarray2 = new String[10];
			Arrays.fill(myarray2, getStringFromDate(calendarG));
			prefs.putString("highscoresdates", json.toJson(myarray2) );
		}





		startGame();
	}

	public void startGame(){
		startTime = System.nanoTime();
		birdY = (Gdx.graphics.getHeight()-birds[0].getHeight())/2;
		for(int i=0; i<numberOfTubes; i++){
			tubeOffset[i] = (randomGenerator.nextFloat()-0.5f) * (Gdx.graphics.getHeight() - gap - 200);
			tubeX[i] = (Gdx.graphics.getWidth() - tubeTop.getWidth()) / 2 + Gdx.graphics.getWidth() + i*distanceBetweenTubes;

			tubeTopShapes[i] = new Rectangle();
			tubeBottomShapes[i] = new Rectangle();
		}
		idMusic = sound.play(1.0f); // play new sound and keep handle for further manipulation

	}

	Integer engineTick = 0;
	@Override
	public void render () {
		engineTick++;
		if(engineTick == 2){
			engineTick = 0;
		}

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


		if(gameState == 1) {

			if(tubeX[scoringTube] < Gdx.graphics.getWidth() / 2){
				Gdx.app.log("Score",String.valueOf(score));
				if(scoringTube < numberOfTubes-1 ){
					scoringTube++;
				} else {
					scoringTube = 0;
				}
			}

			velocity = Gdx.input.getGyroscopeX()*35;

			distanceTraveled += tubeVelocity;


			birdYOld = birdY;


			if(birdY < Gdx.graphics.getHeight()-birds[flapState].getHeight()) {
				if (birdY > 0) {

					//velocity += gravity;
					birdY -= velocity;
				} else {
					birdY++;
					//gameState = 2;
				}
			}
			else{
				birdY--;
			}
/*
			if(birdY-Gdx.graphics.getHeight()/2<300){
				turn = 1;
			}
			else if(birdY-Gdx.graphics.getHeight()/2>-300){
				turn = 2;
			}
			else
			{
				turn = 0;
			}
*/
		} else if(gameState == 0) {

			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if(gameState ==2){

			prefs.flush();

			String serializedInts = prefs.getString("highscore");
			int[] deserializedScores = json.fromJson(int[].class, serializedInts);

			String serializedDates = prefs.getString("highscoresdates");
			String[] deserializedScoresDates = json.fromJson(String[].class, serializedDates);

		    if(score > deserializedScores[0] && doHighScore){
				doHighScore = false;
				deserializedScores[0] = score;

				Arrays.sort(deserializedScores);

		        prefs.putString("highscore", json.toJson(deserializedScores) );

				date = new Date();
				calendarG.setTime(date);
				deserializedScoresDates[0] = getStringFromDate(calendarG);
				List<String> deserializedScoresDates2 = Arrays.asList(deserializedScoresDates);

                Collections.sort(deserializedScoresDates2, new Comparator<String>() {
						DateFormat f = new SimpleDateFormat("dd-MMM-yyyy");
						@Override
						public int compare(String o1, String o2) {
							try {
								return f.parse(o2).compareTo(f.parse(o1));
							} catch (ParseException e) {
								throw new IllegalArgumentException(e);
							}
						}
					});
				prefs.putString("highscoresdates", json.toJson(deserializedScoresDates2.toArray(deserializedScoresDates)) );
				deserializedScoresDates = deserializedScoresDates2.toArray(deserializedScoresDates);
            }
            if(vibrate) {
				Gdx.input.vibrate(300);
				vibrate = false;
			}
		    //batch.draw(gameOver,Gdx.graphics.getWidth()/2-gameOver.getWidth()/2,Gdx.graphics.getHeight()/2-gameOver.getHeight()/2);
		    prefs.flush();

			font.draw(batch,"Top 10:",90,1850);

			for(int i = 0; i < 10; i++){
				if (deserializedScores[9-i] != 0){
					font.draw(batch, String.valueOf(deserializedScores[9-i]) + "   " + deserializedScoresDates[9-i],90,1700-i*150);

				}
			}

            sound.stop(idMusic);
			if(Gdx.input.justTouched()){
				doHighScore = true;
				vibrate = true;
				gameState = 1;
				startGame();
				score = 0;
				scoringTube = 0;
				velocity = 0;

				bullets.clear();
				enemies.clear();
				idMusic = sound.play(1.0f);
			}
		}

		if(engineTick == 0) {
			if (flapState == 0 || flapState == 3) {
				if (flapState == 0) {
					flapState = 3;
				} else {
					flapState = 0;
				}
			} else if (flapState == 1 || flapState == 4) {
				if (flapState == 1) {
					flapState = 4;
				} else {
					flapState = 1;
				}
			} else if (flapState == 2 || flapState == 5) {
				if (flapState == 2) {
					flapState = 5;
				} else {
					flapState = 2;
				}
			}
		}


		//render player
		batch.draw(birds[flapState+turn], birdX, birdY);

		font.draw(batch,String.valueOf(score),100,200);


		//enemy render + collisions
		for(Enemy enemy : enemies) {

			for(Bullet bullet : bullets) {
				if (bullet.getOwner() == 0) {
					if (bullet.getX() < Gdx.graphics.getWidth() && (bullet.getX() >= enemy.getX()) && bullet.getY() <= enemy.getY() + enemyType[0].getWidth() - 5 && bullet.getY() >= enemy.getY() + 5) {
						bullets.remove(bullet);
						enemies.remove(enemy);
						score++;
						hit.play(1.0f);
						break;
					}

				}
				if (bullet.getOwner() == 1) {
					if ((bullet.getX() <= birdX+birds[flapState].getHeight()) && bullet.getY() <= birdY + birds[0].getWidth() - 5 && bullet.getY() >= birdY + 5) {
						bullets.remove(bullet);
						gameState = 2;
						dead.play(1.0f);
						break;

					}

				}
			}
			//render enemy
			batch.draw(enemyType[0], enemy.getX(), enemy.getY());

		}

		batch.end();


		birdCircle.set(Gdx.graphics.getWidth()/2,birdY+birds[flapState].getHeight()/2,birds[flapState].getWidth()/2);

		//background stars
		Random rand = new Random();
		for(int i=0; i<numberOfStars; i++){

			starX[i] = starX[i] - starVelocityY[i];

			if(starX[i] <= 0){
				starX[i] = Gdx.graphics.getWidth();
				starY[i] = rand.nextInt((Gdx.graphics.getHeight() - 1) + 1) + 1;
			}

			shapeRenderer.setColor(new Color(235,247,73,1));
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.circle(starX[i], starY[i] , starRadius[i]);
			shapeRenderer.end();
		}

		for(int i=0; i<numberOfTubes; i++) {

			if(Intersector.overlaps(birdCircle,tubeTopShapes[i]) || Intersector.overlaps(birdCircle,tubeBottomShapes[i])){
				gameState = 2;
			}
		}


		if(gameState == 1) {

			if (Gdx.input.justTouched()) {
				canfire = true;
			}
		}


        boolean clear = false;
        do {
            clear = true;
            for(Bullet bullet : bullets) {
                if ((bullet.owner == 0 && bullet.getX() >= Gdx.graphics.getWidth()-21) || bullet.getX() < 0) {
                    bullets.remove(bullet);
                    clear = false;
                    break;
                }
            }

        } while (!clear);

		for(Bullet bullet : bullets){
			//render bullets
			if(bullet.owner == 1){
				shapeRenderer.setColor(Color.RED);

			}
			else{
				shapeRenderer.setColor(Color.GREEN);

			}
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			shapeRenderer.rect(bullet.getX(), bullet.getY(), 20, 10);
			shapeRenderer.end();
		}

		//enemy
		if(gameState == 1){
			enemyCounter++;
			if(enemyCounter == 100){
				enemyCounter = 0;
			}
			if(enemyCounter == 0 /*&& enemies.size()< 1*/){
				enemies.add(new Enemy(Gdx.graphics.getWidth()-200, (Gdx.graphics.getHeight()-birds[0].getHeight())/2, Gdx.graphics.getHeight()));
			}

			enemyShootCounter++;
			if(enemyShootCounter==40){
				enemyShootCounter=0;
			}

			if(enemyShootCounter==0 ) {
				for (Enemy enemy2 : enemies) {
					if(Math.random() < 0.5)
						bullets.add(new Bullet(enemy2.getX(), enemy2.getY(), -1, 1));
				}
			}
		}

        //same length of ticks prevent lags
		if((System.nanoTime() - startTime)/0.000001 > 50) {

			if(canfire){
				bullets.add(new Bullet(birds[flapState].getWidth() + birds[flapState].getWidth() / 2-10, birdY + birds[flapState].getHeight() / 2-5, 1, 0));
				fire.play(1.0f);
				canfire = false;
			}

			if(gameState == 1){
                for (Bullet bullet : bullets) {
                    bullet.tick();
                }
                for (Enemy enemy : enemies) {
                    enemy.tick();
                }
			}
			startTime = System.nanoTime();
		}

	}

	@Override
	public void dispose () {
		batch.dispose();
		sound.dispose();
		prefs.flush();
	}
}
