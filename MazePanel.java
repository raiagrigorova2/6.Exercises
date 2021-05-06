package uprajnenie_6;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.Arrays;

import javax.swing.*;

public class MazePanel extends JPanel {
	Maze m; 
	//��� �� �� ����� ���������� ��� ����������� �� ����� ���������� � ��� - �� 
	//����, ����� ���� �� ����������, �� ����� 0, ���� �� ���������� - 1, ���������� - 2, ����� - 3 
	int [][] squaresToColor;
	
	JButton findButton;
	JButton clearButton; 
	JLabel solvableLabel; 
	MazePanel(){			
		solvableLabel = new JLabel("");
		
		findButton = new JButton("Find Path");
		findButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				boolean solvable = solveMaze();
				if(solvable) {
					solvableLabel.setText("Path found");
				}
				else {
					solvableLabel.setText("Path not found");
				}
				repaint();
				
			}
			
		});
		
		clearButton = new JButton("Clear Path");
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//������ ������ ������ ���� � ���� 
				for(int i = 0; i < squaresToColor.length; i++) {
					Arrays.fill(squaresToColor[i], 0);
				}
				solvableLabel.setText("");
				repaint();
				
			}
			
		});
		this.add(findButton);
		this.add(clearButton);
		this.add(solvableLabel);
	}
	private boolean solveMaze() {
		try {
			m = new Maze("mazes.txt");
			//��������� ������ � ������������ �� ������������� 
			int rows = m.maze.length;
			this.squaresToColor = new int[rows][];
			for(int i = 0; i < rows; i++) {
				this.squaresToColor[i] = new int[m.maze[i].length];
			}
			
			for(int i = 0; i < squaresToColor.length; i++) {
				for(int j = 0; j < squaresToColor[i].length; j++) {
					//�� ������� ���� ����� 
					if(m.maze[i][j] == 2) {
						squaresToColor[i][j] = 3;
						break;
					}
				}
			}
			//��������� �� ������� �� � �������� � ������ 
			squaresToColor[m.start.y][m.start.x] = 1;
		}
		catch (FileNotFoundException e) {
			System.out.println("dead");
			e.printStackTrace();
		}
		Position p = m.start;
		m.path.push(p);
	
		while (true) {
			int y = m.path.peek().y;
			int x = m.path.peek().x;
	
			m.maze[y][x] = 0; // marking the positions I have already been on with zero, so we avoid going in
								// circles
	
			// down
			if (isValid(y + 1, x, m)) {
				if (m.maze[y + 1][x] == 2) {
					System.out.println("Moved down");
					return true;
				} else if (m.maze[y + 1][x] == 1) {
					squaresToColor[y + 1][x] = 1;
					System.out.println("Moved down");
					m.path.push(new Position(y + 1, x));
					continue;
				}
			}
	
			// left
			if (isValid(y, x - 1, m)) {
				if (m.maze[y][x - 1] == 2) {
					System.out.println("Moved left");
					return true;
				} else if (m.maze[y][x - 1] == 1) {
					squaresToColor[y][x - 1] = 1;
					System.out.println("Moved left");
					m.path.push(new Position(y, x - 1));
					continue;
				}
			}
	
			// up
			if (isValid(y - 1, x, m)) {
				if (m.maze[y - 1][x] == 2) {
					System.out.println("Moved up");
					return true;
				} else if (m.maze[y - 1][x] == 1) {
					squaresToColor[y - 1][x] = 1;
					System.out.println("Moved up");
					m.path.push(new Position(y - 1, x));
					continue;
				}
			}
	
			// right
			if (isValid(y, x + 1, m)) {
				if (m.maze[y][x + 1] == 2) {
					System.out.println("Moved right");
					return true;
				} else if (m.maze[y][x + 1] == 1) {
					squaresToColor[y][x + 1] = 1;
					System.out.println("Moved right");
					m.path.push(new Position(y, x + 1));
					continue;
				}
			}
	
			Position deadEndPos =  m.path.pop(); // going back in case we have reached a dead end
			squaresToColor[deadEndPos.y][deadEndPos.x] = 2;
			System.out.println("Moved back");
			if (m.path.size() <= 0) { // nowhere new to go
				return false;
			}
		}
	}

	public static boolean isValid(int y, int x, Maze m) {
		if (y < 0 || y >= m.maze.length || x < 0 || x >= m.maze[y].length) {
			return false;
		}
		return true;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(squaresToColor != null) {
			for(int i = 0; i < squaresToColor.length; i++) {
				for(int j = 0; j < squaresToColor[i].length; j++) {
					drawSquare(i, j, g);
				}
			}
		}
	}
	public void drawSquare(int i, int j, Graphics g) {
		//����� ����������� �� ����� ��� �� � squaresToColor[i].length
		//�������� �� ����������� �� � �������� �� ������ ������ �� ���� �� ������������� �� ���� 
		int a = this.getBounds().width / squaresToColor[i].length;
		// �� x ������������ ����������� ������ �� ���� a * j
		//�� y ����������� ������ �� ���� a * i + 100 (+100, �� �� ��� ����� �� label) 
		int x = a * j;
		int y = a * i + 100;
		
		//������ ���� ����� ��� �� squaresToColor[i][j] �� �������� �������� ���� 
		switch(squaresToColor[i][j]) {
		case 0: //���� ����������, ������ ���������
			g.setColor(Color.BLACK);
			g.drawRect(x, y, a, a);
			break;
		case 1: //��� ����������, ��� 
			g.setColor(Color.GREEN);
			g.fillRect(x, y, a, a);
			break;
		case 2://�������� 
			g.setColor(Color.RED);
			g.fillRect(x, y, a, a);
			break;
		case 3://���
			g.setColor(Color.BLUE);
			g.fillRect(x, y, a, a);
			break;
		}
	}
}
