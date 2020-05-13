package Socket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class MyThread implements Runnable{
	//接收服务端的消息
	private Scanner servlerScanner = null;
	//连接对象
	private Socket socker = null;
	public MyThread(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socker = socket;
		try {
			this.servlerScanner = new Scanner(this.socker.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(servlerScanner.hasNext()){
			System.out.println(servlerScanner.nextLine());
		}
	}

}
public class Client {
	//用户的名称
	static String nickName = null;
	//用户输入
	static Scanner userScanner = new Scanner(System.in);
	//向服务端发送消息的对象
	static PrintStream printStream = null;
	
	public static void main(String[] args) throws IOException {
		//连接服务端
		Socket socket= null;
		try{
			 socket= new Socket("192.168.43.39",8888);
		}catch(Exception e){
			System.out.println("服务器连接失败！");
			return;
		}
		
		printStream = new PrintStream(socket.getOutputStream());
		
		//启动一个线程接受服务端消息功能
		MyThread myThread = new MyThread(socket);
		new Thread(myThread).start();
		
		//设置用户名称
		System.out.println("请输入您的昵称：");
		if(userScanner.hasNext()){
			nickName = userScanner.nextLine();
			printStream.println(nickName);
		}
		
		System.out.println("现在到了您输入内容的时候了");
		
		while(userScanner.hasNext()){
			String str = userScanner.nextLine();
			printStream.println(str);
			if(str.equals("Bye")){
				System.out.println("即将退出聊天室");
				break;
			}
		}
		printStream.close();
		userScanner.close();
		socket.close();
	}
}
