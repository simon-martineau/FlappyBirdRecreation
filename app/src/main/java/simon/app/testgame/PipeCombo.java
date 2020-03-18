package simon.app.testgame;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class PipeCombo {
	private List<Pipe> pipes = new ArrayList<Pipe>();
	private int x;

	private boolean passedBird = false;

	public PipeCombo(Pipe top, Pipe bottom) {
		pipes.add(top);
		pipes.add(bottom);
		x = (top.getRect().left + top.getRect().right) / 2;
	}

	public void update() {
		for (Pipe p : pipes) {
			p.update();
		}
		x = pipes.get(0).getMiddleX();
	}

	public void draw(Canvas canvas) {
		for (Pipe p : pipes) {
			p.draw(canvas);
		}
	}

	public boolean addScore(Player player) {
		if (!passedBird) {
			if (player.getCenterX() > x) {
				passedBird = true;
				return true;
			}
		}
		return false;
	}

	public boolean birdCollide(Player player) {
		for (Pipe p : pipes) {
			if (p.birdCollide(player)) return true;
		}
		return false;
	}

}
