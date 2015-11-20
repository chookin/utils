package cmri.utils.lang;

import cmri.utils.configuration.ConfigManager;
import cmri.utils.configuration.OptionPack;
import cmri.utils.web.NetworkHelper;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * Created by zhuyin on 3/24/15.
 */
public abstract class BaseOper {
    private static Logger LOG;
    private final OptionPack optionPack = new OptionPack();

    static {
        // configure log4j to log to custom file at runtime. In the java program directly by setting a system property (BEFORE you make any calls to log4j).
        try {
            String actionName = System.getProperty("action");
            if(actionName ==null) {
                System.setProperty("hostname.time", InetAddress.getLocalHost().getHostName() + "-" + TimeHelper.toString(new Date(), "yyyyMMddHHmmss"));
            }else{
                System.setProperty("hostname.time", actionName + "-" + InetAddress.getLocalHost().getHostName() + "-" + TimeHelper.toString(new Date(), "yyyyMMddHHmmss"));
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        LOG = Logger.getLogger(BaseOper.class);
        NetworkHelper.setDefaultProxy();
    }

    public Logger getLogger(){
        return LOG;
    }

    public BaseOper setArgs(String[] args){
        String[] myArgs = null;
        if (args.length == 0) {
            String defaultArgs = ConfigManager.get("cli.paras");
            if (defaultArgs != null) {
                myArgs = defaultArgs.split(" ");
            }
        } else {
            myArgs = args;
        }
        optionPack.put(myArgs);
        LOG.info("args: " + Arrays.toString(myArgs));
        return this;
    }
    public BaseOper setArgs(Map<String, String> options){
        optionPack.put(options);
        return this;
    }
    public OptionPack getOptionPack(){
        return optionPack;
    }

    /**
     * @return true if execute.
     */
    public abstract boolean action();
}