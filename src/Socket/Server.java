package Socket;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class UserThread implements Runnable{
	private String nickName = null;
	//为什么用这个而不用inputstream，因为这个会阻塞，当有输入的时候才向下执行
	private Scanner clientScanner = null;
	private List<UserThread> list = null;
	private Socket accept = null;
	private PrintStream sendMessage = null;
	
	public UserThread(Socket accept,List<UserThread> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		this.accept = accept;
		try {
			this.clientScanner = new Scanner(this.accept.getInputStream());
			this.sendMessage = new PrintStream(this.accept.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//在客户端规定用户第一次输入的时候是名字,这里用while循环主要是不能让名字为空
		while(clientScanner.hasNext()){
			this.nickName = clientScanner.nextLine();
			if(!"".equals(this.nickName)){
				this.sendMessage(this.nickName+"加入了聊天室！");
				break;
			}
		}
		
		//调用接收消息的方法
		acceptMessage();
		
		try {
			this.clientScanner.close();
			this.sendMessage.close();
			this.accept.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 接收客户端的消息
	 */
	public void acceptMessage(){
		while(true){
			String userTalk =null;
			
			if(clientScanner.hasNext()){
				userTalk = this.nickName+"："+clientScanner.nextLine();
			}
			
			if(userTalk==null||userTalk.equals(this.nickName+"：Bye")){
				//用户退出聊天室
				sendMessage(this.nickName+"退出聊天室");
				//在用户列表里移除自己
				this.list.remove(this);
				System.out.println("直至当前用户数："+list.size());
				break;
			}else{
				sendMessage(userTalk);
			}
		}
	}
	
	/**
	 * 向所有的客户端发送消息
	 * @param userTalk
	 * @throws IOException
	 */
	public void sendMessage(String userTalk){
		for(UserThread userThread :list){
			userThread.sendMessage.println(userTalk);
		}
	}
}
public class Server {
	public static void main(String[] args) throws IOException {
		ServerSocket server = new ServerSocket(8888);
		List<UserThread> list = new ArrayList<>();
		
		UserThread userThread = null;
		while(true){
			Socket accept = server.accept();
			userThread = new UserThread(accept,list);
			new Thread(userThread).start();
			list.add(userThread);
			System.out.println("直至当前用户数："+list.size());
		}
		
	}
}
