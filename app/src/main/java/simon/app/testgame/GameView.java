package simon.app.testgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;


import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
	// Threading
	public MainThread thread;

	// Pipes
	private List<PipeCombo> mPipeCombos = new ArrayList<PipeCombo>();
	private Bitmap pipeBitmap;
	private int pipeCoolDown = 120;

	// Planets
	private List<Bitmap> mPlanetBitmaps = new ArrayList<Bitmap>();
	private List<Planet> mPlanets = new ArrayList<Planet>();
	private int planetTimeout = 10;
	private long planetIndex = 0;
	private int nPlanets = 9;

	public static final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
	public static final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
	private long ticks = 0;

	private Rect ground;
	private Paint groundPaint;
	private Rect sky;
	private Paint skyPaint;

	private Player ufo;
	private ScrollingBackground scrollingBackground;


	private Bitmap reversePipeBitmap;
	private Bitmap background;

	private int endGameTimeOut = 60;

	public int score = 0;

	private boolean userTouched = false;
	private boolean collision = false;
	public boolean gameOver = false;



	public GameView(Context context) {
		super(context);

		getHolder().addCallback(this);

		thread = new MainThread(getHolder(), this);
		setFocusable(true);
	}


	public void update() {
		if (!gameOver) {

			scrollingBackground.update();

			generatePlanet(-6);

			if (pipeCoolDown > 0) {
				pipeCoolDown--;
			} else {
				if (ticks % 80 == 0) {
					generatePipeCombo(550, (int) (45 + Math.random() * 35));
				}
			}

			for (Planet p : mPlanets) {
				p.update();
			}

			if (userTouched) {
				ufo.flap();
				userTouched = false;
			} else {
				ufo.isGroundCollide = ufo.groundCollide(ground);
			}
			ufo.update();

			for (PipeCombo pipeCombo : mPipeCombos) {
				if (pipeCombo.birdCollide(ufo)) {
					collision = true;
				}
				if (pipeCombo.addScore(ufo)) score ++;
				pipeCombo.update();
			}

			if (collision) {
				gameOver = true;
			}

			ticks++;
		} else {
			if (endGameTimeOut != 0) endGameTimeOut --;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

		scrollingBackground.draw(canvas);

		for (Planet p : mPlanets) {
			p.draw(canvas);
		}

		ufo.draw(canvas);
		for (PipeCombo pipeCombo : mPipeCombos) {
			pipeCombo.draw(canvas);
		}

		canvas.drawRect(ground, groundPaint);

		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setTextSize(60);
		canvas.drawText("FPS : " + thread.averageFPS, 100, screenHeight - 50, p);
		drawGameScore(canvas);
		if (gameOver) ufo.drawGameOver(canvas);

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		thread.setRunning(true);
		thread.start();

		skyPaint = new Paint();
		skyPaint.setColor(Color.rgb(168, 234, 240));
		sky = new Rect();
		sky.set(0, 0, screenWidth, (int) (screenHeight - 0.25 * screenHeight));

		groundPaint = new Paint();
		groundPaint.setColor(Color.rgb(50, 50, 50));
		ground = new Rect();
		ground.set(0, (int) (screenHeight - 0.25 * screenHeight), screenWidth, screenHeight);

		pipeBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe), 280, 1608, true);
		reversePipeBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.pipe_inverted), 280, 1608, true);
		background = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.seamless_space), ground.top, ground.top, true);

		ufo = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.ufo), ground.top);
		scrollingBackground = new ScrollingBackground(-4, background);

		loadPlanetsArray(256);

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		boolean retry = true;
		while (retry) {
			try {
				thread.setRunning(false);
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			retry = false;
		}
	}

	 void generatePipeCombo(int separation, int heightPercentage) {
		if (separation % 2 != 0) separation += 1;

		int middleY =  (int) (screenHeight - (screenHeight * (heightPercentage / 100.0)));

		Pipe bottomPipe = new Pipe(pipeBitmap, false, middleY + separation / 2, screenWidth + 300, -12);
		Pipe topPipe = new Pipe(reversePipeBitmap, true, middleY - separation / 2, screenWidth + 300, -12);

		PipeCombo combo = new PipeCombo(topPipe, bottomPipe);

		mPipeCombos.add(combo);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!gameOver) {
			userTouched = true;
		} else {
			if (endGameTimeOut == 0) startNewGame();
		}
		return super.onTouchEvent(event);
	}

	private void drawGameScore(Canvas canvas) {
		Paint textPaint = new Paint();
		textPaint.setTextSize(80);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextAlign(Paint.Align.CENTER);


		int xPos = (canvas.getWidth() / 2);
		int yPos = (int) (ground.top + 500 - ((textPaint.descent() + textPaint.ascent()) / 2));

		canvas.drawText("Score: " + score, xPos, yPos, textPaint);
	}

	public void startNewGame() {


		try {
			thread.setRunning(false);
			thread.join();
		} catch (Exception e) {}

		thread = new MainThread(getHolder(), this);
		mPipeCombos.clear();
		mPlanets.clear();
		pipeCoolDown = 120;
		endGameTimeOut = 60;
		score = 0;
		ufo = new Player(BitmapFactory.decodeResource(getResources(), R.drawable.ufo), ground.top);
		thread.setRunning(true);
		thread.start();
		collision = false;
		gameOver = false;
	}

	public static Bitmap scaleToHeight(Bitmap bmp, int height) {
		float aspectRatio = bmp.getWidth() / (float) bmp.getHeight();

		return Bitmap.createScaledBitmap(bmp, (int) (height * aspectRatio), height, true);
	}

	private void loadPlanetsArray(int height) {
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_1), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_2), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_3), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_4), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_5), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_6), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_7), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_8), height));
		mPlanetBitmaps.add(scaleToHeight(BitmapFactory.decodeResource(getResources(), R.drawable.planet_9), height));
	}

	private void generatePlanet(int speed) {
		if (planetTimeout == 0) {
			int y = (int) ((0.1 + Math.random() * 0.5) * screenHeight);

			Planet planet = new Planet(y, mPlanetBitmaps.get((int) (planetIndex % nPlanets)), speed, screenWidth + 500);
			mPlanets.add(planet);

			planetIndex++;
			planetTimeout = (int) (80 + Math.random() * 40);

		} else {
			planetTimeout--;
		}
	}
}
