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
                //prop.setProperty("log.level", "TRACE");

                prop.setProperty("IC_PORT", args[0]);
                prop.setProperty("IC_CONTACT_POINTS", args[1]);
                prop.setProperty("IC_USER", args[2]);
                prop.setProperty("IC_PASSWORD", args[3]);
                prop.store(out, null);
            }
        }

        GraknSystemProperty.CURRENT_DIRECTORY.set(graknHomeLocation + "grakn/");
        GraknSystemProperty.CONFIGURATION_FILE.set(graknPropertiesLocation);
        GraknSystemProperty.GRAKN_PID_FILE.set("/tmp/grakn.pid");
        GraknBootup.main(new String[]{"server", "start", "queue"});
        GraknBootup.main(new String[]{"server", "start", "grakn"});
    }

}
