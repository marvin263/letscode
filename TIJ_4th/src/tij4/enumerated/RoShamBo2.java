//: enumerated/RoShamBo2.java
// Switching one enum on another.
package tij4.enumerated;

import static tij4.enumerated.Outcome.DRAW;
import static tij4.enumerated.Outcome.LOSE;
import static tij4.enumerated.Outcome.WIN;

public enum RoShamBo2 implements Competitor<RoShamBo2> {
	/**
	 * 1. 当前(this)对象是：RoShamBo2
	 * 
	 * 2. DRAW-->得到 vPAPER(DRAW)     结果（这时比较的对象是：RoShamBo2 PAPER，都是由compete()来决定）
	 * 
	 * 3. LOSE-->得到 vSCISSORS(LOSE) 结果（这时比较的对象是：RoShamBo2 SCISSORS，都是由compete()来决定）
	 * 
	 * 4. WIN-->得到 vROCK(WIN)       结果（这时比较的对象是：RoShamBo2 ROCK，都是由compete()来决定）
	 * 
	 */
	PAPER(DRAW, LOSE, WIN), SCISSORS(WIN, DRAW, LOSE), ROCK(LOSE, WIN, DRAW);
	private Outcome vPAPER, vSCISSORS, vROCK;

	// 初始化其实就建立一个“lookup table”。这里很重要：最终结果就是在这里直接写出来的
	// 1. vPAPER(Outcome)-->传入PAPER(RoShamBo2)时应该得到的结果
	// 2. vSCISSOR(Outcome)-->传入SCISSOR(RoShamBo2)时应该得到的结果
	// 3. vROCK(Outcome)-->传入ROCK(RoShamBo2)时应该得到的结果
	RoShamBo2(Outcome paper, Outcome scissors, Outcome rock) {
		this.vPAPER = paper;
		this.vSCISSORS = scissors;
		this.vROCK = rock;
	}

	public Outcome compete(RoShamBo2 it) {
		switch (it) {
		default:
		case PAPER:
			return vPAPER;
		case SCISSORS:
			return vSCISSORS;
		case ROCK:
			return vROCK;
		}
	}

	public static void main(String[] args) {
		RoShamBo.play(RoShamBo2.class, 20);
	}
} /*
 * Output: ROCK vs. ROCK: DRAW SCISSORS vs. ROCK: LOSE SCISSORS vs. ROCK: LOSE
 * SCISSORS vs. ROCK: LOSE PAPER vs. SCISSORS: LOSE PAPER vs. PAPER: DRAW PAPER
 * vs. SCISSORS: LOSE ROCK vs. SCISSORS: WIN SCISSORS vs. SCISSORS: DRAW ROCK
 * vs. SCISSORS: WIN SCISSORS vs. PAPER: WIN SCISSORS vs. PAPER: WIN ROCK vs.
 * PAPER: LOSE ROCK vs. SCISSORS: WIN SCISSORS vs. ROCK: LOSE PAPER vs.
 * SCISSORS: LOSE SCISSORS vs. PAPER: WIN SCISSORS vs. PAPER: WIN SCISSORS vs.
 * PAPER: WIN SCISSORS vs. PAPER: WIN
 */// :~
