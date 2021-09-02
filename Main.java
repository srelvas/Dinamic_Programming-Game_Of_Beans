/***
 * Joao Arvana 54982
 * Sara Relvas 55596
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import gameBeansII.*;

public class Main {

	public static void main(String[] args) throws IOException {

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

		int nTests = Integer.parseInt(in.readLine());
		GameBeansIIDinamica[] g = new GameBeansIIDinamica[nTests];
		for(int i = 0; i<nTests; i++){
			String[] s = in.readLine().split(" ");
			int P = Integer.parseInt(s[0]);
			int D = Integer.parseInt(s[1]);
			Boolean jaba = false;
			ArrayList<Integer> beans = new ArrayList<Integer>();
			String[] sequence = in.readLine().split(" ");
			for(int j = 0; j < P; j++){
				beans.add(Integer.parseInt(sequence[j]));
			}
			String first = in.readLine();
			if(first.equals("Jaba")){
				jaba = true;
			}
			g[i] = new GameBeansIIDinamica(P,D,beans,jaba);
		}

		for(GameBeansIIDinamica game: g){
			System.out.println(game.resolve());
		}

	}

}
