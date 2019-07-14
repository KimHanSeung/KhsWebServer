import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;


public class RequestHandler  extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connetion;

    public RequestHandler(Socket connetionSocket){
        this.connetion = connetionSocket;
    }

    public void run(){
        log.debug("New Client Connect! Connected IP : {}, Port : {}",
        connetion.getInetAddress(), connetion.getPort());

        try{
            InputStream in = connetion.getInputStream();
            OutputStream out = connetion.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            System.out.println();
            String url[] = br.readLine().split(" ");

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body = "Not Found Page".getBytes();

            System.out.println(url[0] + " , " +url[1] + ", " + url[2]);

            if(url[2].equals("HTTP/1.1")){
                if(url[1].equals("/")){
                    body = "Hello world".getBytes();
                }else if(url[1].equals("/index.html")){
                    File file = new File("/Users/bill/dev/WebServer/src/main/resources/html/index.html");
                    // File file = new File("./resources/html/index.html");
                    body = Files.readAllBytes(file.toPath());
                }
            }

            response200Header(dos, body.length);
            responseBody(dos, body);
        }catch (Exception e){
            log.error("RequestHandler {}", e.getStackTrace());
        }

    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent){
        try{
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: "+ lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        }catch (Exception e){
            log.error("response200Header {}", e.getStackTrace());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body){
        try{
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        }catch (Exception e){
            log.error("responseBody {}", e.getStackTrace());
        }
    }

}
