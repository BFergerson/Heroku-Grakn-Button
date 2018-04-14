package heroku.button.grakn;

import ai.grakn.GraknSystemProperty;
import ai.grakn.bootup.GraknBootup;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import java.io.*;
import java.util.Properties;

/**
 * @author github.com/BFergerson
 */
public class GraknButton {

    public static void main(String[] args) throws Exception {
        String graknHomeLocation = "/app/";
        String graknPropertiesLocation = graknHomeLocation + "grakn/conf/grakn.properties";
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(graknPropertiesLocation)) {
            prop.load(input);

            try (FileOutputStream out = new FileOutputStream(graknPropertiesLocation)) {
                prop.setProperty("log.level", "INFO");

                prop.setProperty("server.port", args[0]);
                prop.setProperty("storage.hostname", args[1]);
                prop.setProperty("storage.username", args[2]);
                prop.setProperty("storage.password", args[3]);
                prop.store(out, null);
            }
        }

        new Thread(() -> {
            while (true) {
                try {
                    File glog = new File("/app/grakn/logs/grakn.log");
                    if (glog.exists()) {
                        Tailer tailer = Tailer.create(glog, new MyListener(), 500);
                        tailer.run();

                        while (true) {
                            Thread.sleep(500);
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        GraknSystemProperty.CURRENT_DIRECTORY.set(graknHomeLocation + "grakn/");
        GraknSystemProperty.CONFIGURATION_FILE.set(graknPropertiesLocation);
        GraknSystemProperty.GRAKN_PID_FILE.set("/tmp/grakn.pid");
        GraknBootup.main(new String[]{"server", "start", "queue"});
        GraknBootup.main(new String[]{"server", "start", "grakn"});
    }

    private static class MyListener extends TailerListenerAdapter {
        @Override
        public void handle(String line) {
            System.out.println(line);
        }
    }


}
