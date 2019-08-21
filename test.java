import java.awt.*; 
import java.awt.geom.*;  
import java.awt.event.*;

public class test extends Frame implements WindowListener {

	private myCanvas cv;
	private Button up,down,left,right,zoom_in,zoom_out,zoom_inin,zoom_outout;
	private Label lbpos;
	private Panel p1,p2;
	public test() { 
		setLayout(new FlowLayout());
		setSize(800, 1000); 
		addWindowListener(this);

		cv=new myCanvas();
		cv.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e){
				cv.set_center(e.getPoint());
			}
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent evt) { }
		});
		cv.addMouseMotionListener(new MouseMotionListener(){
			@Override public void mouseDragged(MouseEvent e) {}
			@Override public void mouseMoved(MouseEvent e) { 
				Point mos_pos=e.getPoint();
				Point2D.Double cord=cv.get_cord(mos_pos);
				lbpos.setText("("+cord.x+","+cord.y+")");
			}
		});
		add(cv);

		p1=new Panel();
		p1.setSize(800,100);
		up=new Button("↑");
		up.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.moves(0,50);
			}
		});
		p1.add(up);

		down=new Button("↓");
		down.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.moves(0,-50);
			}
		});
		p1.add(down);

		left=new Button("←");
		left.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.moves(50,0);
			}
		});
		p1.add(left);

		right=new Button("→");
		right.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.moves(-50,0);
			}
		});
		p1.add(right);

		zoom_in=new Button("+");
		zoom_in.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.zoom(2.0);
			}
		});
		p1.add(zoom_in);

		zoom_out=new Button("-");
		zoom_out.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.zoom(0.5);
			}
		});
		p1.add(zoom_out);

		zoom_inin=new Button("++");
		zoom_inin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.zoom(10.0);
			}
		});
		p1.add(zoom_inin);

		zoom_outout=new Button("--");
		zoom_outout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				cv.zoom(0.1);
			}
		});
		p1.add(zoom_outout);
		add(p1);

		p2=new Panel();
		p2.setSize(800,100);
		lbpos=new Label("                                                                                                        ");
		p2.add(lbpos);
		add(p2);
		setVisible(true); 
 	} 	

	@Override
	public void windowClosing(WindowEvent evt) {
		System.exit(0);  // Terminate the program
	}

	@Override public void windowOpened(WindowEvent evt) { }
	@Override public void windowClosed(WindowEvent evt) { }
	@Override public void windowIconified(WindowEvent evt) { }
	@Override public void windowDeiconified(WindowEvent evt) { }
	@Override public void windowActivated(WindowEvent evt) { }
	@Override public void windowDeactivated(WindowEvent evt) { }


	public static void main(String[] args) {
		new test();
	}

private class myCanvas extends Canvas{

	private int center_x,center_y;
	private int size_x,size_y;
	private double scale;

	public myCanvas () {
		setBackground (Color.WHITE);
		size_x=800;
		size_y=800;
		setSize(size_x, size_y);
		center_x=size_x/2;
		center_y=size_y/2;
		scale=200.0;
	}

	public void paint (Graphics g) {
		draw(g);
	}

	private void draw(Graphics g){
		Mandelbrot mb;
		double x_re;
		double x_im;
		double point_color,cr,cg,cb,clen;
		Point2D.Double point_color2;

		mb= new Mandelbrot();

		for (int i = 1; i <= size_x-1; ++i) {
			for (int j = 1; j <= size_y-1; ++j) {
				x_re=(i-center_x)/scale;
				x_im=-(j-center_y)/scale;
				point_color2=mb.decide2(0.5,0.99,x_re,x_im);
				clen=Math.sqrt(point_color2.x*point_color2.x+point_color2.y*point_color2.y);

				cr=1.0f-Math.min(Math.abs((point_color2.x+2.0f)/4.0f),1.0f);
				cg=1.0f-Math.min(Math.abs((point_color2.y+2.0f)/4.0f),1.0f);
				cr=cr/(1+Math.exp(clen-5));
				cg=cg/(1+Math.exp(clen-5));
				cb=1.0f-Math.min(clen,1.0f);
				g.setColor(new Color((float)cr,(float)cg,(float)cb));
				g.drawRect(i,j,0,0);
			}
		}
 	}	

	private void moves(int len_x,int len_y){
		center_x=center_x+len_x;
		center_y=center_y+len_y;
		repaint();
	}
	private void set_center(Point pos){
		center_x=center_x+size_x/2-pos.x;
		center_y=center_y+size_y/2-pos.y;
		repaint();
	}

	private Point2D.Double get_cord(Point pos){
		Point2D.Double cord=new Point2D.Double();
		cord.x=(pos.x-center_x)/scale;
		cord.y=-(pos.y-center_y)/scale;
		return cord;
	}

	private void zoom(double scl){
		center_x=size_x/2-(int)((size_x/2-center_x)*scl);
		center_y=size_y/2-(int)((size_y/2-center_y)*scl);
		scale=scale*scl;
		repaint();
	}

}

private class Mandelbrot{
	public Mandelbrot() { 
 	}
	private double decide( double x_re, double x_im,double c_re, double c_im ){
		double x2_re=x_re;
		double x2_im=x_im;
		double x3_re;
		double x3_im;
		for (int i = 1; i <= 1000; ++i) {
			x3_re=x2_re*x2_re-x2_im*x2_im+c_re;
			x3_im=2*x2_re*x2_im+c_im;
			x2_re=x3_re;
			x2_im=x3_im;
			if (x2_re*x2_re+x2_im*x2_im>10) break;
		}
		return (x2_re*x2_re+x2_im*x2_im);
	}
	private Point2D.Double decide2( double x_re, double x_im,double c_re, double c_im ){
		double x2_re=x_re;
		double x2_im=x_im;
		double x3_re;
		double x3_im;
		for (int i = 1; i <= 2000; ++i) {
			x3_re=x2_re*x2_re-x2_im*x2_im+c_re;
			x3_im=2*x2_re*x2_im+c_im;
			x2_re=x3_re;
			x2_im=x3_im;
			if (x2_re*x2_re+x2_im*x2_im>100) break;
		}
		return new Point2D.Double(x2_re,x2_im);
	}
}

}