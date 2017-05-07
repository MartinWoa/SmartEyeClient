package org.easypr;

import static org.bytedeco.javacpp.opencv_highgui.imread;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.ImageProducer;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import org.easypr.GUI.Cameralistener;
import org.easypr.GUI.Openlistener;
import org.easypr.GUI.drawPR;
import org.easypr.core.CharsRecognise;
import org.easypr.core.PlateDetect;

import java.io.*;
import java.util.Vector;

public class GUI {
    
	private JFrame frame;
	JLabel prchar;
	drawPR PR ;
	int nowing;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	
		
	
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 706, 501);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		PR = new drawPR();
		PR.setBounds(10, 10, 298, 313);
		frame.getContentPane().add(PR);
		
		
		JPanel CHAR = new JPanel();
		CHAR.setBounds(10, 330, 265, 31);
		frame.getContentPane().add(CHAR);
		
		 prchar = new JLabel();
		CHAR.add(prchar);
		
		JButton open = new JButton("OPEN");
		open.setBounds(554, 10, 93, 23);
		frame.getContentPane().add(open);
		open.addActionListener(new Openlistener());
		
		JButton btnCamera = new JButton("CAMERA");
		btnCamera.setBounds(554, 43, 93, 23);
		frame.getContentPane().add(btnCamera);
		btnCamera.addActionListener(new Cameralistener());
	
	
	}
	 Image toBufferedImage(Mat matrix) {
		          int type = BufferedImage.TYPE_BYTE_GRAY;
		          if (matrix.channels() > 1) {
		              type = BufferedImage.TYPE_3BYTE_BGR;
		         }
		         int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
		         byte[] buffer = new byte[bufferSize];
		        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
	        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
	        return image;
	     }
	
	class Cameralistener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
		      new Thread(new Runnable(){
		    	  public void run(){	OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);    
				    try {
						grabber.start();
						  CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口  
						  canvas.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				              
						      
						    while(true)  
						    {  
						        if(!canvas.isDisplayable())  
						        {//窗口是否关闭  
						      
						            grabber.stop();//停止抓取  
						           break;
						        }  
						        canvas.showImage(grabber.grab());//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像  
						         Image a=canvas.getIconImage();
						         System.out.println(a);
						   
						        Thread.sleep(50);//50毫秒刷新一次图像  
						    }  
					} catch (org.bytedeco.javacv.FrameGrabber.Exception | InterruptedException e) {
						// TODO 自动生成的 catch 块
						e.printStackTrace();
					}   //开始获取摄像头数据  
				    }}).start();
		      
		  
			// TODO 自动生成的方法存根
			
		}
	}

	class Openlistener implements  ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO 自动生成的方法存根
			JFileChooser choose=new JFileChooser();
			choose.setFileSelectionMode(choose.FILES_ONLY);
			choose.showDialog(new JButton(),"选择");
			File file=choose.getSelectedFile();
			String fs=file.getAbsolutePath();
			fs=fs.replaceAll("\\\\","/");
		      Mat src = imread(fs);
		        PlateDetect plateDetect = new PlateDetect();
		        plateDetect.setPDLifemode(true);
		        Vector<Mat> matVector = new Vector<Mat>();
		        Image h;
		        if (0 == plateDetect.plateDetect(src, matVector)) {
		            CharsRecognise cr = new CharsRecognise();
		            
		            for (int i = 0; i < matVector.size(); ++i) {
		            	h=toBufferedImage(matVector.get(i));
		                String result = cr.charsRecognise(matVector.get(i));
		                System.out.println("Chars Recognised: " + result);
		                prchar.setText(result);
		               // PR.setimage(h);
		                PR.showimage(fs);
		            }
		        }
		}}
	
	
	class drawPR extends JPanel
	{  
	    Image img;
	    int i=0;
		void showimage(String f)
		 
		{  
		     ImageIcon file=new ImageIcon(f);
		
				file.setImage(file.getImage().getScaledInstance(298, 313, Image.SCALE_DEFAULT));
			     img=file.getImage();
		      this.repaint();
		    
		
		}
		void setimage(Image image)
		{
			img=image;
			this.repaint();
		}
		public void paintComponent(Graphics g)
		{  
			g.drawImage(img, 0, 0, this);
		}
	}
}
