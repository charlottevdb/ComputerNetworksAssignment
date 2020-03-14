package HttpPackage;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Client {


    private String HTTPCommand;
    private String port;
    private String language;
    private String URI;
    private String host;
    public Socket socket;

    private static List<String> HTTPCommands = new ArrayList<String>(Arrays.asList("GET", "POST", "PUT", "HEAD"));
    private static int counter = 0;

    public void Client() {

    }

    /**
     * Returns input arguments splitted after spaces.
     *
     * @return
     */
    public String[] readArguments() {
        Scanner input = new Scanner(System.in);
        String command = input.nextLine();
        return command.split(" ");
    }

    public void setArguments(String[] arguments) throws IOException {
        if (this.areValidArguments(arguments)) {
            this.HTTPCommand = arguments[0];
            this.port = arguments[2];
            this.language = arguments[3];

            URL url = new URL(arguments[1]);
            this.host = url.getHost();

        }
    }

    /**
     * Checks if arguments are valid arguments.
     * @param arguments
     *        Arguments to be checked.
     * @return
     */
    public boolean areValidArguments(String[] arguments) {
        if (arguments.length != 4) {
            System.out.println("Please, give 4 arguments.");
            return false;
        }

        String HTTPCommand = arguments[0];

        if (!(HTTPCommands.contains(HTTPCommand))) {
            System.out.println("HTTP Command is not valid");
            return false;
        }

        return true;
    }

    public static boolean isValidURL(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            url.toURI();
            return true;
        } catch (Exception exception)
        {
            return false;
        }
    }

    public void connectSocket() throws IOException {
        this.socket = new Socket(this.host, Integer.parseInt(this.port));
    }


    /**
     *
     * @param method 1 van de 4
     * @param path telkens meegeven om met dezelfde client nog de images te kunnen halen
     * @param body body voor POST/PUT request
     * @throws IOException
     */
    public void sendRequest(String method, String path, String body) throws IOException {
        PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
        output.println(method + " " + path + " HTTP/1.1");
        output.println("Host: " + this.host + ":" + this.port);
        output.println("Accept-Language: " + language);

        if (body != null) {
            output.println("Content-Length: " + body.length());
        }
        output.println();
        if (body != null) {
            output.println(body);
        }

        StringBuilder headerString = new StringBuilder();
        String infoLine;
        boolean headersFinished = false;

        HashMap<String, String> headers = new HashMap<>();

        InputStream input = socket.getInputStream();
        int character;
        // read headers
        while ((character = input.read()) != -1){
            System.out.print((char)character); //to print character that matches the integer (ASCII)
            if (!headersFinished) {
                headerString.append((char)character);
                // get last 4 characters and check if 2 line breaks
                if (headerString.length() >= 4 && headerString.substring(headerString.length()-4).equals("\r\n\r\n")) {
                    headersFinished = true;
                    infoLine = headerString.toString().split("\r\n")[0];
                    String stringWithoutInfoLine = headerString.toString().replaceFirst(infoLine+"\r\n", "");

                    headers = getHeaders(stringWithoutInfoLine);
                    break;
                }
            }
        }
        // read body from response
        ByteArrayOutputStream responseBody = new ByteArrayOutputStream();
        if (headers.containsKey("Content-Length")) {
            int length = Integer.parseInt(headers.get("Content-Length"));
            for (int i = 0; i < length ; i++){
                character = input.read();
                System.out.print((char)character);
                responseBody.write(character);
            }
        } else if (headers.containsKey("Transfer-Encoding")) { // transfer encoding chunked
            while (true) {
                StringBuilder lengthString = new StringBuilder();
                //read length
                while ((character = input.read()) != 13) { //zolang geen \r (13) blijven toevoegen
                    lengthString.append((char)character);
                    System.out.print((char)character);
                }
                input.read(); // read \n
                System.out.print("\r\n");
                int length = Integer.decode("0x" + lengthString.toString());
                if (length == 0) {
                    break;
                } else {
                    for (int i = 0; i < length ; i++){
                        character = input.read();
                        System.out.print((char)character);
                        responseBody.write(character);
                    }
                }
                input.read();
                input.read(); // read \r\n
                System.out.print("\r\n");
            }
        }

        // write to file
        String contentType = headers.get("Content-Type");
        if (contentType.contains(";")) { //verwijder charset als ie er is
            contentType = contentType.substring(0, contentType.indexOf(";"));
        }
        String fileType = contentType.substring(contentType.indexOf("/")+1);//kap stuk voor slash af

        FileOutputStream out = new FileOutputStream("Downloads/file" + counter + "." + fileType);
        out.write(responseBody.toByteArray()); //stop ByteArraystream
        out.close();
        counter ++;



    }

    // function from internet
    public HashMap<String, String> getHeaders(String headerString) {
        Pattern pattern = Pattern.compile("([^:]+): (.+)\r\n");
        Matcher matcher = pattern.matcher(headerString);
        HashMap<String, String> result = new HashMap<>();

        while (matcher.find()) {
            result.put(matcher.group(1), matcher.group(2));
        }

        return result;
    }



}

