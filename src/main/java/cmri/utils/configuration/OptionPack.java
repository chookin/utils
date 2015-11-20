package cmri.utils.configuration;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by zhuyin on 11/17/14.
 */
public class OptionPack {
    private static Logger LOG = Logger.getLogger(OptionPack.class);
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final NavigableMap<String, String> options = new TreeMap<>();

    public OptionPack put(Map<String, String> options){
        if(options == null){
            return this;
        }
        lock.writeLock().lock();
        try{
            for(Map.Entry<String, String> entry: options.entrySet()){
                this.options.put(entry.getKey(), entry.getValue());
            }
            return this;
        }finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * for each arg, key and value are separated by '=', if '=' not exists, then set value to "".
     * @param args array of args.
     * @return this
     */
    public OptionPack put(String[] args){
        if(args == null){
            return this;
        }
        Map<String, String> myOptions = new HashMap<>();
        for (String arg : args) {
            String myArg;
            String val = "";
            int indexEqualSign = arg.indexOf("=");
            if (indexEqualSign == -1) {
                indexEqualSign = arg.length();
            } else {
                val = arg.substring(indexEqualSign + 1, arg.length());
            }
            myArg = arg.substring(0, indexEqualSign);
            if (!myArg.isEmpty()) {
                myOptions.put(myArg, val);
            }
        }
        return put(myOptions);
    }

    private String checkConfigured(String property) {
        String val = get(property);
        if (val == null) {
            throw new IllegalArgumentException("property " + property + " is not configured");
        }
        return val;
    }

    /**
     * Get the value of a option.
     *
     * @param option name of this option.
     * @return null if not have this option.
     */
    public String get(String option) {
        lock.readLock().lock();
        try {
            return this.options.get(option);
        }finally {
            lock.readLock().unlock();
        }
    }

    public String get(String option, String defaultVal) {
        String str = get(option);
        if(str == null){
            return defaultVal;
        }
        return str;
    }

    /**
     * The {@link boolean} returned represents the value {@code true} if the string argument
     * is not {@code null} and is equal, ignoring case, to the string
     * {@code "true"}. <p>
     */
    public boolean getAsBool(String item) {
        String val = checkConfigured(item);
        return Boolean.parseBoolean(val);
    }

    public boolean getAsBool(String item, boolean defaultValue) {
        String val = get(item);
        return StringUtils.isEmpty(val) ?defaultValue: Boolean.valueOf(val);
    }

    public Map<String, String> options(){
        lock.readLock().lock();
        try{
            return new HashMap<>(this.options);
        }finally {
            lock.readLock().unlock();
        }
    }

    public boolean exists(String option) {
        return get(option) != null;
    }

    public boolean notExists(String option) {
        return get(option) == null;
    }

    public OptionPack clear(){
        lock.writeLock().lock();
        try{
            this.options.clear();
            return this;
        }finally {
            lock.writeLock().unlock();
        }
    }

    public int size(){
        lock.readLock().lock();
        try{
            return this.options.size();
        }finally {
            lock.readLock().unlock();
        }
    }
    /**
     * check whether args contain this option, and if contains, print prompt of processing this option.
     * @param option the option to check
     * @return false if not contain this option, true if else.
     */
    public boolean process(String option) {
        if (get(option) == null) {
            return false;
        }
        String paras = get(option);
        if(paras.isEmpty()){
            LOG.info(String.format("process option '%s'", option));
        } else {
            LOG.info(String.format("process option '%s=%s'", option, paras));
        }
        return true;
    }
}
