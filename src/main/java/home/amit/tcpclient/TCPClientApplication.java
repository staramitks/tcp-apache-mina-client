package home.amit.tcpclient;

import home.amit.tcpclient.handler.MinaClientHandler;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

@SpringBootApplication
public class TCPClientApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(TCPClientApplication.class, args);
	}
	int  PORT=9999;

	@Override
	public void run(String... args) throws IOException, InterruptedException
	{
		IoConnector connector = new NioSocketConnector();
		connector.getSessionConfig().setReadBufferSize(2048);

		connector.getFilterChain().addLast("logger", new LoggingFilter());
		connector.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"))));

		connector.setHandler(new MinaClientHandler("Hello Server"));
		ConnectFuture future = connector.connect(new InetSocketAddress("localhost", PORT));
		future.awaitUninterruptibly();

		if (!future.isConnected())
		{
			return;
		}
		IoSession session = future.getSession();
		session.getConfig().setUseReadOperation(true);
		session.getCloseFuture().awaitUninterruptibly();
		System.out.println(session.read().getMessage());
		System.out.println("After Writing");
		connector.dispose();
    }
}
