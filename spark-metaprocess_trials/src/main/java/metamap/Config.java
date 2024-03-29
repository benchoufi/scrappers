package metamap;

/**
 * Created by @nowami on 18/08/15.
 */
public class Config {

    private static Config config = null;
    private static String options = "-A -V USAbase -J " +
            "acab,anab,comd,cgab,dsyn,emod,inpo,mobd,neop,patf,sosy" + " -y -Z 2014AB";

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    /*
    * Use StringBuilder to append if this becomes more appended
    */
    public void addOptions(String custom) {
        this.options = custom + " " + this.options;
    }

    private Config(){}

    public static Config getInstance() {
        if (config == null) {
            config = new Config();
            config.setOptions(options);
        }
        return config;
    }
}
